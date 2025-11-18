package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la réponse de déconnexion.
 */
@Schema(description = "Réponse de déconnexion")
public class LogoutResponseDTO {

    @Schema(description = "Message de confirmation", example = "Déconnexion réussie")
    private String message;

    @Schema(description = "Indique si la déconnexion a réussi", example = "true")
    private boolean success;

    public LogoutResponseDTO() {
        // Constructeur par défaut
    }

    public LogoutResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
