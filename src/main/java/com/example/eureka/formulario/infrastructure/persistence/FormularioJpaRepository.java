package com.example.eureka.formulario.infrastructure.persistence;

import com.example.eureka.formulario.domain.model.Formulario;
import com.example.eureka.formulario.domain.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

interface FormularioJpaRepository extends JpaRepository<Formulario, Long> {

    // PASO 1: Cargar formulario con tipo y preguntas (SIN opciones para evitar MultipleBagFetchException)
    @Query("SELECT DISTINCT f FROM Formulario f " +
            "JOIN FETCH f.tipoFormulario tf " +
            "LEFT JOIN FETCH f.formularioPreguntas fp " +
            "LEFT JOIN FETCH fp.pregunta p " +
            "WHERE tf.nombre = :tipoNombre " +
            "AND f.estado = 'ACTIVO'")
    Optional<Formulario> findByTipoFormularioNombre(@Param("tipoNombre") String tipoNombre);


    @Query("SELECT DISTINCT f FROM Formulario f " +
            "LEFT JOIN FETCH f.tipoFormulario tf " +
            "LEFT JOIN FETCH f.formularioPreguntas fp " +
            "LEFT JOIN FETCH fp.pregunta p " +
            "WHERE f.idFormulario = :id")
    Optional<Formulario> findById(@Param("id") Long id);
}