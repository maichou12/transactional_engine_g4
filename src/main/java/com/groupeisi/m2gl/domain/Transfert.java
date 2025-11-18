package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Transfert représentant un transfert d'argent.
 * Relation many-to-many avec Compte via Details_Transaction.
 */
@Entity
@Table(name = "transfert")
public class Transfert extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "montant", nullable = false, precision = 21, scale = 2)
    private BigDecimal montant;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "transfert", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetailsTransaction> detailsTransactions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Set<DetailsTransaction> getDetailsTransactions() {
        return detailsTransactions;
    }

    public void setDetailsTransactions(Set<DetailsTransaction> detailsTransactions) {
        this.detailsTransactions = detailsTransactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transfert)) {
            return false;
        }
        return id != null && id.equals(((Transfert) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Transfert{" + "id=" + id + ", montant=" + montant + ", date=" + date + '}';
    }
}
