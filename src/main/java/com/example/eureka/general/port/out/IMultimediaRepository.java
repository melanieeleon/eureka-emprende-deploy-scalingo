package com.example.eureka.general.port.out;

import com.example.eureka.entrepreneurship.domain.model.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMultimediaRepository extends JpaRepository<Multimedia,Integer> {
}
