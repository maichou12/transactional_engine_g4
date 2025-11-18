package com.groupeisi.m2gl.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la validation du code OTP.
 */
@Schema(description = "DTO pour la validation du code OTP")
public class ValidationOtpDTO {

    @Schema(description = "Numéro de téléphone", example = "+221771234567", required = true)
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String telephone;

    @Schema(description = "Code OTP de 4 chiffres reçu par SMS", example = "1234", required = true)
    @NotBlank(message = "Le code OTP est obligatoire")
    @Size(min = 4, max = 4, message = "Le code OTP doit contenir 4 chiffres")
    @Pattern(regexp = "^[0-9]{4}$", message = "Le code OTP doit contenir uniquement des chiffres")
    private String codeOtp;

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
}
