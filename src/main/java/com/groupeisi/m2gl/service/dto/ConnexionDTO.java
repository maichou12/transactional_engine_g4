package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la connexion.
 */
@Schema(description = "DTO pour la connexion d'un utilisateur")
public class ConnexionDTO {

    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @Schema(description = "Code OTP de 4 chiffres reçu par SMS", example = "1234", required = true)
    @NotBlank(message = "Le code OTP est obligatoire")
    @Size(min = 4, max = 4, message = "Le code OTP doit contenir 4 chiffres")
    @Pattern(regexp = "^[0-9]{4}$", message = "Le code OTP doit contenir uniquement des chiffres")
    private String codeOtp;

    @Schema(description = "Mot de passe (6 chiffres)", example = "123456", required = true)
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 6, message = "Le mot de passe doit contenir exactement 6 chiffres")
    @Pattern(regexp = "^[0-9]{6}$", message = "Le mot de passe doit contenir uniquement des chiffres")
    private String password;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCodeOtp() {
        return codeOtp;
    }

    public void setCodeOtp(String codeOtp) {
        this.codeOtp = codeOtp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
