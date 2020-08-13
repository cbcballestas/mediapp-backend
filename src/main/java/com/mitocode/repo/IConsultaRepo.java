package com.mitocode.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mitocode.model.Consulta;

//@Respository
public interface IConsultaRepo extends IGenericRepo<Consulta, Integer> {

	// JPQL
	@Query("FROM Consulta c WHERE c.paciente.dni =:dni OR LOWER(c.paciente.nombres) LIKE %:nombreCompleto% OR LOWER(c.paciente.apellidos) LIKE %:nombreCompleto%")
	List<Consulta> buscar(@Param("dni") String dni, String nombreCompleto);

	// >= | <
	@Query("FROM Consulta c WHERE c.fecha BETWEEN :fechaConsulta AND :fechaSgte")
	List<Consulta> buscarFecha(LocalDateTime fechaConsulta, @Param("fechaSgte") LocalDateTime fechaSgte);

	@Query(value = "select * from fn_listarResumen()", nativeQuery = true)
	List<Object[]> listarResumen();
	
	//cantidad		fecha
	//[1,	"03/07/2020"]
	//[6,	"04/07/2020"]
	//[1,	"13/06/2020"]
	//[1,	"27/06/2020"]
}
