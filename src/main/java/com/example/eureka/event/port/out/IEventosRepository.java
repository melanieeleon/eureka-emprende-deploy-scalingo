package com.example.eureka.event.port.out;

import com.example.eureka.shared.enums.EstadoEvento;
import com.example.eureka.shared.enums.TipoEvento;
import com.example.eureka.event.domain.model.Eventos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;  // ✅ CORRECTO
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IEventosRepository {

    List<Eventos> findWithFilters(
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoEvento estado,
            TipoEvento tipoEvento,
            Integer mes,
            Integer idEmprendimiento
    );

    List<Eventos> findByUsuarioEmprendedor(Integer idUsuario);

    Eventos save(Eventos evento);

    Optional<Eventos> findById(Integer id);
    List<Eventos> saveAll(List<Eventos> eventos);
    List<Eventos> findAll(Specification<Eventos> spec);

    Page<Eventos> findAll(Specification<Eventos> spec, Pageable pageable);

    void deleteById(Integer id);
}