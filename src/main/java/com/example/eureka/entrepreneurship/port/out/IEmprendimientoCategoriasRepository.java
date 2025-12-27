package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoCategorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoCategoriasRepository extends JpaRepository<EmprendimientoCategorias, Integer> {

    @Query("SELECT ec FROM EmprendimientoCategorias ec " +
            "WHERE ec.categoria.id = :categoriaId")
    List<EmprendimientoCategorias> findByCategoriaId(Integer categoriaId);

    List<EmprendimientoCategorias> findByEmprendimientoId(Integer emprendimientoId);
    List<EmprendimientoCategorias> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);
    void deleteEmprendimientoCategoriasByEmprendimientoId(Integer emprendimientoId);

    @Query("SELECT ec FROM EmprendimientoCategorias ec " +
            "JOIN FETCH ec.categoria c " +
            "JOIN FETCH c.multimedia " +
            "WHERE ec.emprendimiento.id = :emprendimientoId")
    List<EmprendimientoCategorias> findByEmprendimientoIdWithCategoria(@Param("emprendimientoId") Integer emprendimientoId);

}
