package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Transfert} entity.
 */
@Repository
public interface TransfertRepository extends JpaRepository<Transfert, Long> {}
