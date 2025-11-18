package com.groupeisi.m2gl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Entité associative Details_Transaction liant un Transfert à deux Comptes
 * (compte émetteur et compte récepteur).
 */
@Entity
@Table(name = "details_transaction")
public class DetailsTransaction extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "compte_emetteur_id", nullable = false)
    @JsonIgnoreProperties(value = { "user", "detailsTransactions" }, allowSetters = true)
    private Compte compteEmetteur;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "compte_recepteur_id", nullable = false)
    @JsonIgnoreProperties(value = { "user", "detailsTransactions" }, allowSetters = true)
    private Compte compteRecepteur;

    @ManyToOne(optional = false)
    @NotNull
    @JoinColumn(name = "transfert_id", nullable = false)
    @JsonIgnoreProperties(value = { "detailsTransactions" }, allowSetters = true)
    private Transfert transfert;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compte getCompteEmetteur() {
        return compteEmetteur;
    }

    public void setCompteEmetteur(Compte compteEmetteur) {
        this.compteEmetteur = compteEmetteur;
    }

    public Compte getCompteRecepteur() {
        return compteRecepteur;
    }

    public void setCompteRecepteur(Compte compteRecepteur) {
        this.compteRecepteur = compteRecepteur;
    }

    public Transfert getTransfert() {
        return transfert;
    }

    public void setTransfert(Transfert transfert) {
        this.transfert = transfert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailsTransaction)) {
            return false;
        }
        return id != null && id.equals(((DetailsTransaction) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "DetailsTransaction{" +
            "id=" +
            id +
            ", compteEmetteur=" +
            (compteEmetteur != null ? compteEmetteur.getId() : null) +
            ", compteRecepteur=" +
            (compteRecepteur != null ? compteRecepteur.getId() : null) +
            ", transfert=" +
            (transfert != null ? transfert.getId() : null) +
            '}'
        );
    }
}
