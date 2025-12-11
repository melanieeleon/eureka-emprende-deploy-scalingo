package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.InformacionRepresentante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepresentanteInformacionRepository extends JpaRepository<InformacionRepresentante,Integer> {
    List<InformacionRepresentante> findByEmprendimientoId(Integer emprendimientoId);
    List<InformacionRepresentante> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);
    InformacionRepresentante findFirstByEmprendimientoId(Integer emprendimientoId);
}
