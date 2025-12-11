package com.example.eureka.general.port.out;

import com.example.eureka.auth.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRolesRepository extends JpaRepository<Roles, Integer> {
}
