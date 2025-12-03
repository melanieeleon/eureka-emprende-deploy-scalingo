package com.example.eureka.roadmap.service;

import com.example.eureka.domain.model.Roadmap;
import com.example.eureka.roadmap.dto.RoadmapDTO;

import java.util.List;

public interface RoadmapService {

    Roadmap findByIdCompany(Integer id);
    Roadmap findById(Integer id);
    Roadmap save(RoadmapDTO roadmap);
    List<Roadmap> findAll();

}
