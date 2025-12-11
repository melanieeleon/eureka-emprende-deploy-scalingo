package com.example.eureka.general.port.out;

import com.example.eureka.general.domain.model.Provincias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProvinciaRepository extends JpaRepository<Provincias,Integer> {
}
