package com.example.eureka.entrepreneurship.port.out;

import com.example.eureka.entrepreneurship.domain.model.EmprendimientoCategorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmprendimientoCategoriasRepository extends JpaRepository<EmprendimientoCategorias, Integer> {

    List<EmprendimientoCategorias> findByCategoriaId(Integer categoriaId);

    @Query("SELECT ec FROM EmprendimientoCategorias ec " +
            "WHERE ec.categoria.id = :categoriaId")
    List<EmprendimientoCategorias> findEmprendimientosPorCategoria(@Param("categoriaId") Integer categoriaId);
    List<EmprendimientoCategorias> findByEmprendimientoId(Integer emprendimientoId);
    List<EmprendimientoCategorias> findByEmprendimientoIdIn(List<Integer> emprendimientoIds);
    void deleteEmprendimientoCategoriasByEmprendimientoId(Integer emprendimientoId);
}
