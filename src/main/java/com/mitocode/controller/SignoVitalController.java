package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.SignoVital;
import com.mitocode.service.ISignoVitalService;

@RestController
@RequestMapping("/signos-vitales")
public class SignoVitalController {

	@Autowired
	private ISignoVitalService service;

	@GetMapping
	public ResponseEntity<List<SignoVital>> listar() throws Exception {
		List<SignoVital> lista = service.listar();
		return new ResponseEntity<List<SignoVital>>(lista, HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Page<SignoVital>> listarPageable(Pageable pageable) throws Exception {
		Page<SignoVital> signos = service.listarPageable(pageable);
		return new ResponseEntity<Page<SignoVital>>(signos, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<SignoVital> listarPorId(@PathVariable("id") Integer id) throws Exception {
		SignoVital obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<SignoVital>(obj, HttpStatus.OK);
	}

	// https://docs.spring.io/spring-hateoas/docs/current/reference/html/
	// Hateoas 0.9 = > Spring Boot 1.5 y 2.1
	// Hateoas 1.0 = > Spring Boot 2.2
	// Hateoas 1.1 = > Spring Boot 2.3
	@GetMapping("/hateoas/{id}")
	public EntityModel<SignoVital> listarPorIdHateoas(@PathVariable("id") Integer id) throws Exception {
		SignoVital obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}

		// localhost:8080/pacientes/{id}
		EntityModel<SignoVital> recurso = EntityModel.of(obj); // Antes: new EntityModel<SignoVital>(obj);
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).listarPorId(id));

		recurso.add(linkTo.withRel("signo-recurso"));

		return recurso;
	}

	/*
	 * @PostMapping public ResponseEntity<SignoVital> registrar(@Valid @RequestBody
	 * SignoVital signo) { SignoVital obj = service.registrar(signo); return new
	 * ResponseEntity<SignoVital>(obj, HttpStatus.CREATED); }
	 */

	@PostMapping
	public ResponseEntity<Void> registrar(@Valid @RequestBody SignoVital signo) throws Exception {
		SignoVital obj = service.registrar(signo);

		// localhost:8080/pacientes/5
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdSigno())
				.toUri();

		return ResponseEntity.created(location).build();
	}

	@PutMapping
	public ResponseEntity<SignoVital> modificar(@Valid @RequestBody SignoVital signo) throws Exception {
		SignoVital obj = service.modificar(signo);
		return new ResponseEntity<SignoVital>(obj, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		SignoVital obj = service.listarPorId(id);
		if (obj.getIdSigno() == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		service.eliminar(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
