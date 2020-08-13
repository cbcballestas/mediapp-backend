package com.mitocode.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mitocode.dto.ConsultaListaExamenDTO;
import com.mitocode.dto.ConsultaResumenDTO;
import com.mitocode.dto.FiltroConsultaDTO;
import com.mitocode.model.Consulta;
import com.mitocode.repo.IConsultaExamenRepo;
import com.mitocode.repo.IConsultaRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IConsultaService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ConsultaServiceImpl extends CRUDImpl<Consulta, Integer> implements IConsultaService{

	@Autowired
	private IConsultaRepo repo;
	
	@Autowired
	private IConsultaExamenRepo ceRepo;
	
	@Override
	protected IGenericRepo<Consulta, Integer> getRepo(){
		return repo;
	}

	@Transactional//(propagation = )
	@Override
	public Consulta registrarTransaccional(ConsultaListaExamenDTO dto) throws Exception {
		/*for(DetalleConsulta det : consulta.getDetalleConsulta()) {
			det.setConsulta(consulta);
		}*/
		
		dto.getConsulta().getDetalleConsulta().forEach(det -> det.setConsulta(dto.getConsulta()));
		repo.save(dto.getConsulta());
		
		dto.getLstExamen().forEach(exa -> ceRepo.registrar(dto.getConsulta().getIdConsulta(), exa.getIdExamen()));		
		
		return dto.getConsulta();
		//Consulta: 1 | Objto 1
		//DetalleConsulta | n inserts (1
	}
	
	@Override
	public List<Consulta> buscar(FiltroConsultaDTO filtro) {		
		return repo.buscar(filtro.getDni(), filtro.getNombreCompleto());
	}
	
	@Override
	public List<Consulta> buscarFecha(FiltroConsultaDTO filtro) {
		return repo.buscarFecha(filtro.getFechaConsulta(), filtro.getFechaConsulta().plusDays(1));
	}
	
	@Override
	public List<ConsultaResumenDTO> listarResumen() {
		//cantidad		fecha  List<Object[]>
		//[1,	"03/07/2020"]
		//[6,	"04/07/2020"]
		//[1,	"13/06/2020"]
		//[1,	"27/06/2020"]
		
		List<ConsultaResumenDTO> consultas = new ArrayList<>();
		repo.listarResumen().forEach(x -> {
			ConsultaResumenDTO cr = new ConsultaResumenDTO();
			cr.setCantidad(Integer.parseInt(String.valueOf(x[0])));
			cr.setFecha(String.valueOf(x[1]));
			consultas.add(cr);
		});
		
		return consultas;
	}

	@Override
	public byte[] generarReporte() {
		byte[] data = null;
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("txt_titulo", "Prueba de titulo");
		
		try{
			File file = new ClassPathResource("/reports/consultas.jasper").getFile();
			JasperPrint print = JasperFillManager.fillReport(file.getPath(), parametros, new JRBeanCollectionDataSource(this.listarResumen()));
			data = JasperExportManager.exportReportToPdf(print);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	
}
