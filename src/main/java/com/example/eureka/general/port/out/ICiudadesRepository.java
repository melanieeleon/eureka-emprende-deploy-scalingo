package com.example.eureka.general.port.out;

import com.example.eureka.general.domain.model.Ciudades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICiudadesRepository extends JpaRepository<Ciudades, Integer> {

    Optional<Ciudades> findById(Integer id);

    List<Ciudades> findByProvincias_Id(Integer provinciasId);
}
