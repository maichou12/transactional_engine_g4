package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'étape 3 de l'inscription : définition du mot de passe.
 */
@Schema(description = "DTO pour l'étape 3 de l'inscription - Définition du mot de passe")
public class InscriptionEtape3DTO {

    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @Schema(description = "Mot de passe (6 chiffres)", example = "123456", required = true)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 6, message = "Le mot de passe doit contenir exactement 6 chiffres")
    @Pattern(regexp = "^[0-9]{6}$", message = "Le mot de passe doit contenir uniquement des chiffres")
    private String password;

    @Schema(description = "Confirmation du mot de passe (doit être identique au mot de passe)", example = "123456", required = true)
    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    @Size(min = 6, max = 6, message = "La confirmation du mot de passe doit contenir exactement 6 chiffres")
    @Pattern(regexp = "^[0-9]{6}$", message = "La confirmation du mot de passe doit contenir uniquement des chiffres")
    private String confirmPassword;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
