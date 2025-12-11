package com.example.eureka.general.port.out;

import com.example.eureka.general.domain.model.OpcionesParticipacionComunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOpcionesParticipacionComunidadRepository extends JpaRepository<OpcionesParticipacionComunidad,Integer> {
}
