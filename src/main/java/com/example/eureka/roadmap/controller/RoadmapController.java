package com.example.eureka.roadmap.controller;


import com.example.eureka.domain.model.Roadmap;
import com.example.eureka.roadmap.dto.RoadmapDTO;
import com.example.eureka.roadmap.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService  roadmapService;

    @GetMapping
    public ResponseEntity<List<Roadmap>> findAll() {
        return ResponseEntity.ok(roadmapService.findAll());
    }

    @GetMapping("/emprendimiento/{id}")
    public ResponseEntity<Roadmap> findAllByEmprendimientoId(@PathVariable String id) {
        return ResponseEntity.ok(roadmapService.findByIdCompany(Integer.parseInt(id)));
    }

    @PostMapping("/save")
    public ResponseEntity<Roadmap> save(@RequestBody RoadmapDTO roadmap) {
        return ResponseEntity.ok(roadmapService.save(roadmap));
    }




}
