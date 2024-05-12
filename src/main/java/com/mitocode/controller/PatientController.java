package com.mitocode.controller;

import com.mitocode.dto.PatientDTO;
import com.mitocode.dto.PatientRecord;
import com.mitocode.model.Patient;
import com.mitocode.service.IPatientService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/patients")
//@RequestMapping("${patient.controller.path}")
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PatientController {

    //@Autowired
    private final IPatientService service;// = new PatientService();
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    /*public PatientController(PatientService service) {
        this.service = service;
    }*/

    @GetMapping
    public ResponseEntity<List<PatientDTO>> findAll(){
        List<PatientDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> findById(@PathVariable("id") Integer id){
        Patient obj = service.findById(id);

        return ResponseEntity.ok(convertToDto(obj));
        //return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody PatientDTO dto){
        Patient obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdPatient()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> update(@Valid @PathVariable("id") Integer id, @RequestBody PatientDTO dto){
        dto.setIdPatient(id);
        Patient obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(convertToDto(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);

        return ResponseEntity.noContent().build(); //204 NO CONTENT
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<PatientDTO> findByIdHateoas(@PathVariable("id") Integer id){
        EntityModel<PatientDTO> resource = EntityModel.of(convertToDto(service.findById(id)));

        //generar un link informativo
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());

        resource.add(link1.withRel("patient-info-byId"));
        resource.add(link2.withRel("patient-all-info"));

        return resource;
    }

    @GetMapping("/pageable")
    public ResponseEntity<Page<PatientDTO>> listPage(Pageable pageable){

        Page<PatientDTO> page = service.listPage(pageable).map(this::convertToDto);

        return ResponseEntity.ok(page);
    }

    private PatientDTO convertToDto(Patient obj){
        return modelMapper.map(obj, PatientDTO.class);
    }

    private Patient convertToEntity(PatientDTO dto){
        return modelMapper.map(dto, Patient.class);
    }


    /*@GetMapping
    public Patient save(){
        Patient patient = new Patient();
        patient.setIdPatient(0);
        patient.setFirstName("mito");
        return service.validAndSave(patient);
    }*/

}
