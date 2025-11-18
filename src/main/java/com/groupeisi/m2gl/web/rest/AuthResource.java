package com.groupeisi.m2gl.web.rest;

import com.groupeisi.m2gl.service.AuthService;
import com.groupeisi.m2gl.service.OtpService;
import com.groupeisi.m2gl.service.dto.*;
import com.groupeisi.m2gl.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller pour l'authentification mobile.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API d'authentification pour l'application mobile")
public class AuthResource {

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);
    private static final String ENTITY_NAME = "auth";

    private final AuthService authService;
    private final OtpService otpService;

    public AuthResource(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    /**
     * POST /api/auth/inscription/etape1 : Envoie un code OTP pour l'inscription.
     *
     * @param dto le DTO contenant le numéro de téléphone
     * @return le résultat de l'envoi
     */
    @Operation(
        summary = "Étape 1 - Envoi du code OTP",
        description = "Envoie un code OTP de 4 chiffres au numéro de téléphone fourni pour démarrer le processus d'inscription"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Code OTP envoyé avec succès",
                content = @Content(schema = @Schema(implementation = OtpResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
        }
    )
    @PostMapping("/inscription/etape1")
    public ResponseEntity<OtpResponseDTO> inscriptionEtape1(@Valid @RequestBody InscriptionEtape1DTO dto) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("📱 [INSCRIPTION ETAPE 1] Requête reçue");
        log.info("📱 [INSCRIPTION ETAPE 1] Téléphone reçu : '{}'", dto.getTelephone());
        log.info("📱 [INSCRIPTION ETAPE 1] Longueur : {}", dto.getTelephone() != null ? dto.getTelephone().length() : 0);
        log.info("📱 [INSCRIPTION ETAPE 1] Commence par '+' : {}", dto.getTelephone() != null && dto.getTelephone().startsWith("+"));
        log.info("═══════════════════════════════════════════════════════════");

        try {
            otpService.generateAndSendOtp(dto.getTelephone());
            log.info("✅ [INSCRIPTION ETAPE 1] Code OTP généré et envoyé pour : {}", dto.getTelephone());
            // En production, ne pas retourner le code OTP
            return ResponseEntity.ok(new OtpResponseDTO("Code OTP envoyé avec succès", true));
        } catch (Exception e) {
            log.error("❌ [INSCRIPTION ETAPE 1] Erreur lors de l'envoi du code OTP", e);
            throw new BadRequestAlertException("Erreur lors de l'envoi du code OTP", ENTITY_NAME, "otpsendfailed");
        }
    }

    /**
     * POST /api/auth/inscription/etape1/validation : Valide le code OTP pour l'inscription.
     *
     * @param dto le DTO contenant le téléphone et le code OTP
     * @return le résultat de la validation
     */
    @Operation(
        summary = "Étape 1 - Validation du code OTP",
        description = "Valide le code OTP reçu par SMS. Le code expire après 5 minutes."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Code OTP validé avec succès",
                content = @Content(schema = @Schema(implementation = OtpResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Code OTP invalide ou expiré"),
        }
    )
    @PostMapping("/inscription/etape1/validation")
    public ResponseEntity<OtpResponseDTO> validationOtpInscription(@Valid @RequestBody ValidationOtpDTO dto) {
        log.debug("REST request pour valider un code OTP : {}", dto.getTelephone());

        try {
            boolean isValid = authService.validateOtpForInscription(dto.getTelephone(), dto.getCodeOtp());
            if (isValid) {
                return ResponseEntity.ok(new OtpResponseDTO("Code OTP validé avec succès", true));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OtpResponseDTO("Code OTP invalide ou expiré", false));
            }
        } catch (Exception e) {
            log.error("Erreur lors de la validation du code OTP", e);
            throw new BadRequestAlertException("Erreur lors de la validation du code OTP", ENTITY_NAME, "otpvalidationfailed");
        }
    }

    /**
     * POST /api/auth/inscription/etape2 : Enregistre les informations personnelles.
     *
     * @param dto le DTO contenant les informations personnelles
     * @return le résultat de l'enregistrement
     */
    @Operation(
        summary = "Étape 2 - Informations personnelles",
        description = "Enregistre les informations personnelles de l'utilisateur (nom, prénom, NIN, date de naissance). " +
        "Ces informations seront utilisées lors de la création du compte à l'étape 3."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Informations personnelles enregistrées avec succès",
                content = @Content(schema = @Schema(implementation = OtpResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur/NIN déjà existant"),
        }
    )
    @PostMapping("/inscription/etape2")
    public ResponseEntity<OtpResponseDTO> inscriptionEtape2(@Valid @RequestBody InscriptionEtape2DTO dto) {
        log.debug("REST request pour enregistrer les informations personnelles : {}", dto.getTelephone());

        try {
            authService.savePersonalInfo(dto);
            return ResponseEntity.ok(new OtpResponseDTO("Informations personnelles enregistrées avec succès", true));
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'enregistrement des informations personnelles", e);
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "personalinfofailed");
        }
    }

    /**
     * POST /api/auth/inscription/etape3 : Finalise l'inscription et crée l'utilisateur avec son compte.
     *
     * @param dto le DTO contenant le mot de passe
     * @return la réponse d'authentification avec le token
     */
    @Operation(
        summary = "Étape 3 - Finalisation de l'inscription",
        description = "Finalise l'inscription en créant l'utilisateur et son compte bancaire. " +
        "Le mot de passe doit contenir exactement 6 chiffres. " +
        "Un compte bancaire est automatiquement créé avec un solde initial de 0. " +
        "Un token JWT est retourné pour authentifier l'utilisateur."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Inscription réussie, utilisateur et compte créés",
                content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la finalisation (mots de passe non identiques, etc.)"),
        }
    )
    @PostMapping("/inscription/etape3")
    public ResponseEntity<AuthResponseDTO> inscriptionEtape3(@Valid @RequestBody InscriptionEtape3DTO dto) {
        log.debug("REST request pour finaliser l'inscription : {}", dto.getTelephone());

        try {
            AuthResponseDTO response = authService.completeInscription(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la finalisation de l'inscription", e);
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "inscriptionfailed");
        }
    }

    /**
     * POST /api/auth/connexion/otp : Envoie un code OTP pour la connexion.
     *
     * @param dto le DTO contenant le numéro de téléphone
     * @return le résultat de l'envoi
     */
    @Operation(
        summary = "Connexion - Envoi du code OTP",
        description = "Envoie un code OTP de 4 chiffres au numéro de téléphone pour permettre la connexion"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Code OTP envoyé avec succès",
                content = @Content(schema = @Schema(implementation = OtpResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
        }
    )
    @PostMapping("/connexion/otp")
    public ResponseEntity<OtpResponseDTO> connexionOtp(@Valid @RequestBody InscriptionEtape1DTO dto) {
        log.debug("REST request pour envoyer un code OTP de connexion : {}", dto.getTelephone());

        try {
            otpService.generateAndSendOtp(dto.getTelephone());
            return ResponseEntity.ok(new OtpResponseDTO("Code OTP envoyé avec succès", true));
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du code OTP", e);
            throw new BadRequestAlertException("Erreur lors de l'envoi du code OTP", ENTITY_NAME, "otpsendfailed");
        }
    }

    /**
     * POST /api/auth/connexion : Authentifie un utilisateur.
     *
     * @param dto le DTO contenant le téléphone, le code OTP et le mot de passe
     * @return la réponse d'authentification avec le token
     */
    @Operation(
        summary = "Connexion",
        description = "Authentifie un utilisateur avec son numéro de téléphone, le code OTP (4 chiffres) reçu par SMS et son mot de passe (6 chiffres). " +
        "Retourne un token JWT valide 24 heures pour accéder aux autres endpoints."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Authentification réussie",
                content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Code OTP invalide, mot de passe incorrect ou utilisateur non trouvé"),
        }
    )
    @PostMapping("/connexion")
    public ResponseEntity<AuthResponseDTO> connexion(@Valid @RequestBody ConnexionDTO dto) {
        log.debug("REST request pour authentifier un utilisateur : {}", dto.getTelephone());

        try {
            AuthResponseDTO response = authService.authenticate(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'authentification", e);
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "authenticationfailed");
        }
    }

    /**
     * POST /api/auth/logout : Déconnecte un utilisateur.
     *
     * @param userId l'identifiant de l'utilisateur (optionnel, peut être extrait du token)
     * @return le résultat de la déconnexion
     */
    @Operation(
        summary = "Déconnexion",
        description = "Déconnecte un utilisateur. Le token JWT reste techniquement valide jusqu'à son expiration, " +
        "mais cette méthode permet de logger la déconnexion et au client de nettoyer ses données locales."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Déconnexion réussie",
                content = @Content(schema = @Schema(implementation = com.groupeisi.m2gl.service.dto.LogoutResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<com.groupeisi.m2gl.service.dto.LogoutResponseDTO> logout(@RequestParam(required = false) String userId) {
        log.info("REST request pour déconnecter un utilisateur : {}", userId);

        try {
            if (userId != null && !userId.isEmpty()) {
                authService.logout(userId);
            }
            return ResponseEntity.ok(new com.groupeisi.m2gl.service.dto.LogoutResponseDTO("Déconnexion réussie", true));
        } catch (Exception e) {
            log.error("Erreur lors de la déconnexion", e);
            return ResponseEntity.ok(new com.groupeisi.m2gl.service.dto.LogoutResponseDTO("Déconnexion effectuée", true));
        }
    }
}
