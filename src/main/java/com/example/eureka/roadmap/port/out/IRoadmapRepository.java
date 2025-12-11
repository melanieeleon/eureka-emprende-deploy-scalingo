package com.example.eureka.roadmap.port.out;

import com.example.eureka.entrepreneurship.domain.model.Emprendimientos;
import com.example.eureka.roadmap.domain.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoadmapRepository extends JpaRepository<Roadmap, Integer> {

    Roadmap findByEmprendimiento(Emprendimientos emprendimiento);

    boolean existsByEmprendimiento(Emprendimientos emprendimiento);

}
