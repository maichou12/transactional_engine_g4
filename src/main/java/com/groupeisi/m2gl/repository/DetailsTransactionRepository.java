package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.DetailsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link DetailsTransaction} entity.
 */
@Repository
public interface DetailsTransactionRepository extends JpaRepository<DetailsTransaction, Long> {}
