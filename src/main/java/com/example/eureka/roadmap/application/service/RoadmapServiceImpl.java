package com.example.eureka.roadmap.application.service;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.entrepreneurship.domain.model.DescripcionEmprendimiento;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosDescripcionRepository;
import com.example.eureka.roadmap.domain.Roadmap;
import com.example.eureka.entrepreneurship.port.out.IEmprendimientosRepository;
import com.example.eureka.roadmap.infrastructure.dto.RoadmapDTO;
import com.example.eureka.roadmap.port.out.IRoadmapRepository;
import com.example.eureka.roadmap.port.in.RoadmapService;
import com.example.eureka.shared.config.openapi.GPTRoadmap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

    private final IRoadmapRepository roadmapRepository;
    private final GPTRoadmap gptRoadmap;
    private final IEmprendimientosRepository  emprendimientosRepository;
    private final IEmprendimientosDescripcionRepository emprendimientosDescripcionRepository;

    @Override
    public Roadmap findByIdCompany(Integer id) {
        Emprendimientos emp =  emprendimientosRepository.findById(id).orElse(null);
        return roadmapRepository.findByEmprendimiento(emp);
    }

    @Override
    public Roadmap findById(Integer id) {
        return roadmapRepository.findById(id).orElse(null);
    }

    @Override
    public Roadmap save(RoadmapDTO roadmap) {

        Emprendimientos emp = emprendimientosRepository.findById(roadmap.getIdEmprendimiento()).orElse(null);
        List<DescripcionEmprendimiento> descripcionEmprendimientos = emprendimientosDescripcionRepository.findByEmprendimientoId(roadmap.getIdEmprendimiento());
        String historia = "";
        String objetivo = "";
        for(DescripcionEmprendimiento e: descripcionEmprendimientos) {
  /*          if(e.getTipoDescripcion().equals("HISTORIA")){
                historia += " "+e.getTipoDescripcion();
            }
            if(e.getTipoDescripcion().equals("PROPOSITO")){
                objetivo += " "+e.getTipoDescripcion();
            }
            if (e.getTipoDescripcion().equals("DIFERENCIAL")){
                objetivo += " "+e.getTipoDescripcion();
            }
            if (e.getTipoDescripcion().equals("PUBLICO OBJETIVO")){
                objetivo += " " + e.getTipoDescripcion();
            }
*/

        }
        roadmap.setHistoria(historia);
        roadmap.setObjetivo(objetivo);
        Roadmap rm = new Roadmap();
        if(!roadmapRepository.existsByEmprendimiento(emp)){
            String contenido = gptRoadmap.generarRoadmap(
                    roadmap.getHistoria(),
                    roadmap.getObjetivo()
            );
            rm.setContenido(contenido);
            rm.setEmprendimiento(emp);
            rm.setHistoria(roadmap.getHistoria());
            rm.setObjetivo(roadmap.getObjetivo());
            rm.setFechaCreacion(roadmap.getFechaCreacion());

        }else{
            Roadmap r =  roadmapRepository.findByEmprendimiento(emp);
            rm = r;
        }


        return roadmapRepository.save(rm);
    }

    @Override
    public Page<Roadmap> findAll(Pageable pageable) {
        return roadmapRepository.findAll(pageable);
    }
}
