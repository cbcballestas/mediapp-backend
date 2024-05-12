package com.mitocode.controller;

import com.mitocode.dto.SpecialtyDTO;
import com.mitocode.model.Specialty;
import com.mitocode.service.ISpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/specialties")
//@RequestMapping("${specialty.controller.path}")
@RequiredArgsConstructor
public class SpecialtyController {

    //@Autowired
    private final ISpecialtyService service;// = new SpecialtyService();
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    /*public SpecialtyController(SpecialtyService service) {
        this.service = service;
    }*/

    @GetMapping
    public ResponseEntity<List<SpecialtyDTO>> findAll(){
        List<SpecialtyDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyDTO> findById(@PathVariable("id") Integer id){
        Specialty obj = service.findById(id);

        return ResponseEntity.ok(convertToDto(obj));
        //return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody SpecialtyDTO dto){
        Specialty obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSpecialty()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialtyDTO> update(@Valid @PathVariable("id") Integer id, @RequestBody SpecialtyDTO dto){
        dto.setIdSpecialty(id);
        Specialty obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(convertToDto(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);

        return ResponseEntity.noContent().build(); //204 NO CONTENT
    }

    @GetMapping("/hateoas/{id}")
    public EntityModel<SpecialtyDTO> findByIdHateoas(@PathVariable("id") Integer id){
        EntityModel<SpecialtyDTO> resource = EntityModel.of(convertToDto(service.findById(id)));

        //generar un link informativo
        WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).findById(id));
        WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).findAll());

        resource.add(link1.withRel("specialty-info-byId"));
        resource.add(link2.withRel("specialty-all-info"));

        return resource;
    }

    private SpecialtyDTO convertToDto(Specialty obj){
        return modelMapper.map(obj, SpecialtyDTO.class);
    }

    private Specialty convertToEntity(SpecialtyDTO dto){
        return modelMapper.map(dto, Specialty.class);
    }
}
