package com.example.eureka.general.port.out;

import com.example.eureka.general.domain.model.Descripciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDescripcionesRepository extends JpaRepository<Descripciones,Integer> {
}
