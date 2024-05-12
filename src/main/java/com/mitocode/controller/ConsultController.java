package com.mitocode.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mitocode.dto.*;
import com.mitocode.model.Consult;
import com.mitocode.model.Exam;
import com.mitocode.model.MediaFile;
import com.mitocode.model.Patient;
import com.mitocode.service.IConsultService;
import com.mitocode.service.IMediaFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.core.Local;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/consults")
//@RequestMapping("${consult.controller.path}")
@RequiredArgsConstructor
public class ConsultController {

    //@Autowired
    private final IConsultService service;// = new ConsultService();
    @Qualifier("consultMapper")
    private final ModelMapper modelMapper;
    private final Cloudinary cloudinary;
    private final IMediaFileService mfService;

    /*public ConsultController(ConsultService service) {
        this.service = service;
    }*/

    @GetMapping
    public ResponseEntity<List<ConsultDTO>> findAll(){
        List<ConsultDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultDTO> findById(@PathVariable("id") Integer id){
        Consult obj = service.findById(id);

        return ResponseEntity.ok(convertToDto(obj));
        //return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    /*@PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ConsultDTO dto){
        Consult obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsult()).toUri();

        return ResponseEntity.created(location).build();
    }*/
    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody ConsultListExamDTO dto) throws Exception{
        Consult cons = convertToEntity(dto.getConsult());
        //List<Exam> exams = dto.getLstExam().stream().map(e -> modelMapper.map(e, Exam.class)).toList();
        List<Exam> exams = modelMapper.map(dto.getLstExam(), new TypeToken<List<Exam>>(){}.getType());
        Consult obj = service.saveTransactional(cons, exams);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsult()).toUri();

        return ResponseEntity.created(location).build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<ConsultDTO> update(@Valid @PathVariable("id") Integer id, @RequestBody ConsultDTO dto){
        dto.setIdConsult(id);
        Consult obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(convertToDto(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);

        return ResponseEntity.noContent().build(); //204 NO CONTENT
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<ConsultDTO> findByIdHateoas(@PathVariable("id") Integer id){
        EntityModel<ConsultDTO> resource = EntityModel.of(convertToDto(service.findById(id)));

        //generar un link informativo
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());

        resource.add(link1.withRel("consult-info-byId"));
        resource.add(link2.withRel("consult-all-info"));

        return resource;
    }

    ////queries/////
    @GetMapping("/search/dates")
    public ResponseEntity<List<ConsultDTO>> searchByDates(
            @RequestParam(value = "date1", defaultValue = "2024-04-11") String date1,
            @RequestParam(value = "date2", defaultValue = "2024-04-11") String date2
    ){
        List<Consult> consults = service.searchByDates(LocalDateTime.parse(date1), LocalDateTime.parse(date2));
        List<ConsultDTO> consultsDTOs = modelMapper.map(consults, new TypeToken<List<ConsultDTO>>(){}.getType());
        return ResponseEntity.ok(consultsDTOs);
    }

    @PostMapping("/search/others")
    public ResponseEntity<List<ConsultDTO>> searchByOthers(@RequestBody FilterConsultDTO filterDTO){
        List<Consult> consults = service.search(filterDTO.getDni(), filterDTO.getFullname());
        List<ConsultDTO> consultsDTOs = modelMapper.map(consults, new TypeToken<List<ConsultDTO>>(){}.getType());
        return ResponseEntity.ok(consultsDTOs);
    }

    @GetMapping("/callProcedureProjection")
    public ResponseEntity<List<IConsultProcDTO>> callProcedureProjection(){
        return ResponseEntity.ok(service.callProcedureOrFunctionProjection());
    }

    @GetMapping("/callProcedureNative")
    public ResponseEntity<List<ConsultProcDTO>> callProcedureNative(){
        return ResponseEntity.ok(service.callProcedureOrFunctionNative());
    }

    @GetMapping(value = "/generateReport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) //MediaType.APPLICATION_PDF_VALUE
    public ResponseEntity<byte[]> generateReport() throws Exception{
        byte[] data = service.generateReport();

        return ResponseEntity.ok(data);
    }

    @PostMapping(value = "/saveFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile multipartFile) throws Exception{ //, @RequestParam("patient")Patient patient

        //DB
        MediaFile mf = new MediaFile();
        mf.setFilename(multipartFile.getOriginalFilename());
        mf.setFileType(multipartFile.getContentType());
        mf.setContent(multipartFile.getBytes());
        mfService.save(mf);

        //Repo Externo
        /*File f = this.convertToFile(multipartFile);
        Map<String, Object> response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
        JSONObject json = new JSONObject(response);
        String url = json.getString("url");*/

        //System.out.println(url);
        //mfService.updatePhoto(url);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/readFile/{idFile}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> readFile(@PathVariable("idFile") Integer idFile) throws Exception{
        byte[] data = mfService.findById(idFile).getContent();

        return ResponseEntity.ok(data);
    }

    public File convertToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(multipartFile.getBytes());
        outputStream.close();
        return file;
    }


    private ConsultDTO convertToDto(Consult obj){
        return modelMapper.map(obj, ConsultDTO.class);
    }

    private Consult convertToEntity(ConsultDTO dto){
        return modelMapper.map(dto, Consult.class);
    }
}
