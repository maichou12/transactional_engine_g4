package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la réponse d'authentification.
 */
@Schema(description = "Réponse d'authentification contenant le token JWT et les informations de l'utilisateur")
public class AuthResponseDTO {

    @Schema(description = "Token JWT pour l'authentification (valide 24 heures)", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Identifiant unique de l'utilisateur", example = "550e8400-e29b-41d4-a716-446655440000")
    private String userId;

    @Schema(description = "Numéro de téléphone", example = "+221771234567")
    private String telephone;

    @Schema(description = "Nom de famille", example = "DIOP")
    private String nom;

    @Schema(description = "Prénom", example = "Amadou")
    private String prenom;

    @Schema(description = "Identifiant du compte bancaire", example = "1")
    private Long compteId;

    @Schema(description = "Numéro de compte bancaire", example = "ACC1234567890")
    private String numCompte;

    public AuthResponseDTO() {
        // Constructeur par défaut
    }

    public AuthResponseDTO(String token, String userId, String telephone, String nom, String prenom, Long compteId, String numCompte) {
        this.token = token;
        this.userId = userId;
        this.telephone = telephone;
        this.nom = nom;
        this.prenom = prenom;
        this.compteId = compteId;
        this.numCompte = numCompte;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Long getCompteId() {
        return compteId;
    }

    public void setCompteId(Long compteId) {
        this.compteId = compteId;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }
}
