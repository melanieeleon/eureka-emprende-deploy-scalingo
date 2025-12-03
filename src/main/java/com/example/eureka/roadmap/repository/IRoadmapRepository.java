package com.example.eureka.roadmap.repository;

import com.example.eureka.domain.model.Emprendimientos;
import com.example.eureka.domain.model.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoadmapRepository extends JpaRepository<Roadmap, Integer> {

    Roadmap findByEmprendimiento(Emprendimientos emprendimiento);

    boolean existsByEmprendimiento(Emprendimientos emprendimiento);

}
