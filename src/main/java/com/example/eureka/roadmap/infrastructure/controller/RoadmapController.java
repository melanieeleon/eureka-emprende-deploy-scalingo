package com.example.eureka.roadmap.infrastructure.controller;


import com.example.eureka.roadmap.domain.Roadmap;
import com.example.eureka.roadmap.infrastructure.dto.RoadmapDTO;
import com.example.eureka.roadmap.port.in.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService  roadmapService;

    @GetMapping
    public ResponseEntity<Page<Roadmap>> findAll(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(roadmapService.findAll(pageable));
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
