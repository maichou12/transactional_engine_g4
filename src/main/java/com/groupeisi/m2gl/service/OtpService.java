package com.groupeisi.m2gl.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service pour la gestion des codes OTP (One-Time Password).
 * En production, ce service devrait intégrer un service SMS réel.
 */
@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 4;
    private static final long OTP_EXPIRY_TIME_MS = 5 * 60 * 1000; // 5 minutes

    // Stockage temporaire des codes OTP (en production, utiliser Redis ou une base de données)
    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * Génère et envoie un code OTP au numéro de téléphone.
     *
     * @param telephone le numéro de téléphone
     * @return le code OTP généré (en production, ne pas retourner le code)
     */
    public String generateAndSendOtp(String telephone) {
        String otp = generateOtp();
        long expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME_MS;

        otpStore.put(telephone, new OtpData(otp, expiryTime));

        // En production, envoyer le SMS via un service SMS (Twilio, AWS SNS, etc.)
        log.info("Code OTP généré pour le téléphone {} : {}", telephone, otp);

        return otp; // En production, retourner null ou un message de succès
    }

    /**
     * Vérifie si le code OTP est valide pour le numéro de téléphone.
     *
     * @param telephone le numéro de téléphone
     * @param otp le code OTP à vérifier
     * @return true si le code est valide, false sinon
     */
    public boolean verifyOtp(String telephone, String otp) {
        OtpData otpData = otpStore.get(telephone);

        if (otpData == null) {
            log.warn("Aucun code OTP trouvé pour le téléphone {}", telephone);
            return false;
        }

        if (System.currentTimeMillis() > otpData.expiryTime) {
            log.warn("Code OTP expiré pour le téléphone {}", telephone);
            otpStore.remove(telephone);
            return false;
        }

        if (!otpData.otp.equals(otp)) {
            log.warn("Code OTP invalide pour le téléphone {}", telephone);
            return false;
        }

        // Supprimer le code après vérification réussie
        otpStore.remove(telephone);
        return true;
    }

    /**
     * Génère un code OTP aléatoire de 4 chiffres.
     */
    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Classe interne pour stocker les données OTP.
     */
    private static class OtpData {

        final String otp;
        final long expiryTime;

        OtpData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}
