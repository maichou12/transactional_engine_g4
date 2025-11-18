package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour la réponse d'envoi d'OTP.
 */
@Schema(description = "Réponse pour les opérations OTP (envoi et validation)")
public class OtpResponseDTO {

    @Schema(description = "Message de réponse", example = "Code OTP envoyé avec succès")
    private String message;

    @Schema(description = "Indique si l'opération a réussi", example = "true")
    private boolean success;

    public OtpResponseDTO() {
        // Constructeur par défaut
    }

    public OtpResponseDTO(String message, boolean success) {
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
