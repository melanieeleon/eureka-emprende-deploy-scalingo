package com.example.eureka.event.infrastructure.scheduler;

import com.example.eureka.event.port.out.IEventosRepository;
import com.example.eureka.shared.enums.EstadoEvento;
import com.example.eureka.event.domain.model.Eventos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoScheduler {

    private final IEventosRepository eventosRepository;

    /**
     * Ejecuta cada minuto para verificar eventos que ya pasaron su fecha
     * y actualiza su estado a TERMINADO
     */
    @Scheduled(fixedRate = 60000) // Ejecuta cada 60000ms (1 minuto)
    @Transactional
    public void actualizarEventosTerminados() {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Crear specification para buscar eventos programados que ya pasaron
            Specification<Eventos> spec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("estadoEvento"), EstadoEvento.programado),
                            criteriaBuilder.lessThan(root.get("fechaEvento"), ahora)
                    );

            List<Eventos> eventosVencidos = eventosRepository.findAll(spec);

            if (!eventosVencidos.isEmpty()) {
                for (Eventos evento : eventosVencidos) {
                    evento.setEstadoEvento(EstadoEvento.terminado);
                    evento.setFechaModificacion(ahora);
                }

                eventosRepository.saveAll(eventosVencidos);

                log.info("Se actualizaron {} eventos a estado TERMINADO", eventosVencidos.size());
            }

        } catch (Exception e) {
            log.error("Error al actualizar eventos terminados: {}", e.getMessage(), e);
        }
    }
}