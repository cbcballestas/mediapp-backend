package com.mitocode.controller;

import com.mitocode.dto.SignDTO;
import com.mitocode.model.Sign;
import com.mitocode.model.Sign;
import com.mitocode.service.ISignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("${vital-signs.controller.path}")
@RequiredArgsConstructor
public class SignController {

    //@Autowired
    private final ISignService service;
    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<SignDTO>> findAll(){
        List<SignDTO> list = service.findAll().stream().map(this::convertToDto).toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignDTO> findById(@PathVariable("id") Integer id){
        Sign obj = service.findById(id);

        return ResponseEntity.ok(convertToDto(obj));
    }

    @PostMapping
    public ResponseEntity<Void> save(@Valid @RequestBody SignDTO dto){
        Sign obj = service.save(convertToEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSign()).toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SignDTO> update(@Valid @PathVariable("id") Integer id, @RequestBody SignDTO dto){
        dto.setIdSign(id);
        Sign obj = service.update(id, convertToEntity(dto));

        return ResponseEntity.ok(convertToDto(obj));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id){
        service.delete(id);

        return ResponseEntity.noContent().build(); //204 NO CONTENT
    }

    private SignDTO convertToDto(Sign obj){
        return modelMapper.map(obj, SignDTO.class);
    }

    private Sign convertToEntity(SignDTO dto){
        return modelMapper.map(dto, Sign.class);
    }
}
