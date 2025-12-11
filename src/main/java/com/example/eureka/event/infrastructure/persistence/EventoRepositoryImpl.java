package com.example.eureka.event.infrastructure.persistence;

import com.example.eureka.shared.enums.EstadoEvento;
import com.example.eureka.shared.enums.TipoEvento;
import com.example.eureka.event.domain.model.Eventos;
import com.example.eureka.event.infrastructure.specification.EventoSpecification;
import com.example.eureka.event.port.out.IEventosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class EventoRepositoryImpl implements IEventosRepository {

    private final EventoJpaRepository jpaRepository;

    public EventoRepositoryImpl(EventoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<Eventos> findAll(Specification<Eventos> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable);
    }

    // ✅ AGREGAR ESTE MÉTODO
    @Override
    public List<Eventos> findAll(Specification<Eventos> spec) {
        return jpaRepository.findAll(spec);
    }

    @Override
    public List<Eventos> findWithFilters(
            String titulo,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            EstadoEvento estado,
            TipoEvento tipoEvento,
            Integer mes,
            Integer idEmprendimiento) {

        Specification<Eventos> spec = EventoSpecification.conFiltros(
                titulo, fechaInicio, fechaFin, estado,
                tipoEvento, mes, idEmprendimiento
        );

        return jpaRepository.findAll(spec);
    }

    @Override
    public List<Eventos> findByUsuarioEmprendedor(Integer idUsuario) {
        Specification<Eventos> spec = EventoSpecification.porUsuarioEmprendedor(idUsuario);
        return jpaRepository.findAll(spec);
    }

    @Override
    public Eventos save(Eventos evento) {
        return jpaRepository.save(evento);
    }

    // ✅ AGREGAR ESTE MÉTODO
    @Override
    public List<Eventos> saveAll(List<Eventos> eventos) {
        return jpaRepository.saveAll(eventos);
    }

    @Override
    public Optional<Eventos> findById(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}