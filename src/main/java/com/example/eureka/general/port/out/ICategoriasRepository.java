package com.example.eureka.general.port.out;

import com.example.eureka.general.domain.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoriasRepository extends JpaRepository<Categorias,Integer> {

    @Query("""
   SELECT c FROM Categorias c
   LEFT JOIN FETCH c.multimedia
""")
    List<Categorias> findAll();

}
