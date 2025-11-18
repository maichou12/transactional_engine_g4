package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.Compte;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Compte} entity.
 */
@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByNumCompte(String numCompte);

    Optional<Compte> findByUserId(String userId);
}
