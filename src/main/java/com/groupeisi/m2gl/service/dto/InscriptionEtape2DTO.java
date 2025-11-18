package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO pour l'étape 2 de l'inscription : informations personnelles.
 */
@Schema(description = "DTO pour l'étape 2 de l'inscription - Informations personnelles")
public class InscriptionEtape2DTO {

    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @Schema(description = "Nom de famille", example = "DIOP", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @Schema(description = "Prénom", example = "Amadou", required = true)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @Schema(description = "Numéro d'identification national (NIN)", example = "1234567890123", required = true)
    @NotBlank(message = "Le NIN est obligatoire")
    @Size(max = 50, message = "Le NIN ne doit pas dépasser 50 caractères")
    private String nin;

    @Schema(description = "Date de naissance", example = "1990-01-15", required = true)
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}
