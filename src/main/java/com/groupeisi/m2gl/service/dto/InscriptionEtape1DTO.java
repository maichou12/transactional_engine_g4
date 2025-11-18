package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'étape 1 de l'inscription : validation du numéro de téléphone.
 */
@Schema(description = "DTO pour l'étape 1 de l'inscription - Envoi du code OTP")
public class InscriptionEtape1DTO {

    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "+221771234567", required = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(min = 8, max = 20, message = "Le numéro de téléphone doit contenir entre 8 et 20 caractères")
    @Pattern(regexp = "^[0-9+\\-() ]+$", message = "Le numéro de téléphone n'est pas valide")
    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
