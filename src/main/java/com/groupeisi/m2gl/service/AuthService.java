package com.groupeisi.m2gl.service;

import com.groupeisi.m2gl.domain.Authority;
import com.groupeisi.m2gl.domain.Compte;
import com.groupeisi.m2gl.domain.User;
import com.groupeisi.m2gl.repository.AuthorityRepository;
import com.groupeisi.m2gl.repository.CompteRepository;
import com.groupeisi.m2gl.repository.UserRepository;
import com.groupeisi.m2gl.service.dto.AuthResponseDTO;
import com.groupeisi.m2gl.service.dto.ConnexionDTO;
import com.groupeisi.m2gl.service.dto.InscriptionEtape2DTO;
import com.groupeisi.m2gl.service.dto.InscriptionEtape3DTO;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service pour la gestion de l'authentification mobile.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final CompteRepository compteRepository;
    private final AuthorityRepository authorityRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
        UserRepository userRepository,
        CompteRepository compteRepository,
        AuthorityRepository authorityRepository,
        OtpService otpService,
        PasswordEncoder passwordEncoder,
        TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.compteRepository = compteRepository;
        this.authorityRepository = authorityRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * Valide le code OTP pour l'inscription.
     */
    public boolean validateOtpForInscription(String telephone, String codeOtp) {
        return otpService.verifyOtp(telephone, codeOtp);
    }

    // Stockage temporaire des informations personnelles (en production, utiliser Redis)
    private final Map<String, InscriptionEtape2DTO> personalInfoStore = new ConcurrentHashMap<>();

    /**
     * Enregistre les informations personnelles (étape 2 de l'inscription).
     * Vérifie que le téléphone a été validé et que l'utilisateur n'existe pas déjà.
     */
    public void savePersonalInfo(InscriptionEtape2DTO dto) {
        // Vérifier que l'utilisateur n'existe pas déjà
        Optional<User> existingUser = userRepository.findOneByTelephone(dto.getTelephone());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un utilisateur avec ce numéro de téléphone existe déjà");
        }

        // Vérifier que le NIN n'est pas déjà utilisé
        Optional<User> existingUserByNin = userRepository.findOneByNin(dto.getNin());
        if (existingUserByNin.isPresent()) {
            throw new RuntimeException("Un utilisateur avec ce NIN existe déjà");
        }

        // Stocker temporairement les informations pour l'étape 3
        personalInfoStore.put(dto.getTelephone(), dto);
        log.info("Informations personnelles enregistrées pour le téléphone {}", dto.getTelephone());
    }

    /**
     * Finalise l'inscription (étape 3) : crée l'utilisateur et son compte.
     */
    public AuthResponseDTO completeInscription(InscriptionEtape3DTO dto) {
        // Vérifier que l'utilisateur n'existe pas déjà
        Optional<User> existingUser = userRepository.findOneByTelephone(dto.getTelephone());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Un utilisateur avec ce numéro de téléphone existe déjà");
        }

        // Vérifier que les mots de passe correspondent
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        // Récupérer les informations personnelles de l'étape 2
        InscriptionEtape2DTO personalInfo = personalInfoStore.get(dto.getTelephone());
        if (personalInfo == null) {
            throw new RuntimeException("Les informations personnelles n'ont pas été enregistrées. Veuillez recommencer l'inscription.");
        }

        // Créer l'utilisateur
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        // Créer un login valide à partir du téléphone (enlever le + et autres caractères non autorisés)
        String login = dto.getTelephone().replaceAll("[^a-zA-Z0-9_.@-]", "").toLowerCase();
        if (login.isEmpty() || !login.matches("^[_.@A-Za-z0-9-]+$")) {
            // Si le login est vide ou invalide, utiliser un format par défaut
            login = "user_" + dto.getTelephone().replaceAll("[^0-9]", "");
        }
        user.setLogin(login);
        user.setTelephone(dto.getTelephone());
        user.setLastName(personalInfo.getNom());
        user.setFirstName(personalInfo.getPrenom());
        user.setNin(personalInfo.getNin());
        user.setDateNaissance(personalInfo.getDateNaissance());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setActivated(true);
        user.setLangKey("fr");

        // Ajouter le rôle USER
        Authority userAuthority = authorityRepository
            .findById(ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Le rôle USER n'existe pas"));
        Set<Authority> authorities = new HashSet<>();
        authorities.add(userAuthority);
        user.setAuthorities(authorities);

        user = userRepository.save(user);

        // Créer le compte associé
        Compte compte = new Compte();
        compte.setUser(user);
        compte.setSolde(java.math.BigDecimal.ZERO);
        compte.setDateCreation(LocalDate.now());
        compte.setNumCompte(generateNumCompte());
        compte = compteRepository.save(compte);

        user.setCompte(compte);
        user = userRepository.save(user);

        // Générer le token JWT
        String token = tokenService.generateToken(user);

        // Supprimer les informations temporaires
        personalInfoStore.remove(dto.getTelephone());

        log.info("Utilisateur créé avec succès : {}", user.getTelephone());

        return new AuthResponseDTO(
            token,
            user.getId(),
            user.getTelephone(),
            user.getLastName(),
            user.getFirstName(),
            compte.getId(),
            compte.getNumCompte()
        );
    }

    /**
     * Authentifie un utilisateur (connexion).
     */
    public AuthResponseDTO authenticate(ConnexionDTO dto) {
        // Vérifier le code OTP
        if (!otpService.verifyOtp(dto.getTelephone(), dto.getCodeOtp())) {
            throw new RuntimeException("Code OTP invalide ou expiré");
        }

        // Trouver l'utilisateur
        User user = userRepository.findOneByTelephone(dto.getTelephone()).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // Vérifier que l'utilisateur est activé
        if (!user.isActivated()) {
            throw new RuntimeException("Compte utilisateur désactivé");
        }

        // Récupérer le compte
        Compte compte = compteRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        // Générer le token JWT
        String token = tokenService.generateToken(user);

        log.info("Utilisateur authentifié avec succès : {}", user.getTelephone());

        return new AuthResponseDTO(
            token,
            user.getId(),
            user.getTelephone(),
            user.getLastName(),
            user.getFirstName(),
            compte.getId(),
            compte.getNumCompte()
        );
    }

    /**
     * Déconnecte un utilisateur.
     * Note: Avec JWT stateless, il n'y a pas de session à invalider côté serveur.
     * Le token reste valide jusqu'à son expiration. Cette méthode sert principalement
     * à logger la déconnexion et à permettre au client de nettoyer ses données locales.
     *
     * @param userId l'identifiant de l'utilisateur
     */
    public void logout(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.info("Utilisateur déconnecté : {} ({})", user.getTelephone(), userId);
        } else {
            log.warn("Tentative de déconnexion pour un utilisateur inexistant : {}", userId);
        }
    }

    /**
     * Génère un numéro de compte unique.
     */
    private String generateNumCompte() {
        String numCompte;
        do {
            numCompte = "ACC" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        } while (compteRepository.findByNumCompte(numCompte).isPresent());
        return numCompte;
    }
}
