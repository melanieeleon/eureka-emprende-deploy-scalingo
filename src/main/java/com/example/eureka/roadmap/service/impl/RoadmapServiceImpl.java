package com.example.eureka.roadmap.service.impl;

import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.domain.model.Roadmap;
import com.example.eureka.entrepreneurship.repository.IEmprendimientosRepository;
import com.example.eureka.roadmap.dto.RoadmapDTO;
import com.example.eureka.roadmap.repository.IRoadmapRepository;
import com.example.eureka.roadmap.service.RoadmapService;
import com.example.eureka.shared.config.openapi.GPTRoadmap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

    private final IRoadmapRepository roadmapRepository;
    private final GPTRoadmap gptRoadmap;
    private final IEmprendimientosRepository  emprendimientosRepository;

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
    public List<Roadmap> findAll() {
        return roadmapRepository.findAll();
    }
}
