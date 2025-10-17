package com.rentalplatform.service;

import com.rentalplatform.model.PasswordResetToken;
import com.rentalplatform.model.User;
import com.rentalplatform.repository.PasswordResetTokenRepository;
import com.rentalplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int EXPIRATION_HOURS = 1;

    @Transactional
    public void createPasswordResetToken(String email) {
        // Trouver l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec cet email"));

        // Vérifier si l'utilisateur est actif
        if (!user.getIsActive()) {
            throw new RuntimeException("Ce compte est désactivé");
        }

        // Supprimer les anciens tokens non utilisés
        tokenRepository.deleteByUser(user);

        // Créer un nouveau token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        // Envoyer l'email avec le lien de réinitialisation
        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink, user.getFullName());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Trouver le token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // Vérifier si le token a déjà été utilisé
        if (resetToken.getUsed()) {
            throw new RuntimeException("Ce lien a déjà été utilisé");
        }

        // Vérifier si le token a expiré
        if (resetToken.isExpired()) {
            throw new RuntimeException("Ce lien a expiré. Veuillez demander un nouveau lien");
        }

        // Récupérer l'utilisateur
        User user = resetToken.getUser();

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Envoyer un email de confirmation
        emailService.sendPasswordChangedConfirmation(user.getEmail(), user.getFullName());
    }

    public void validateResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.getUsed()) {
            throw new RuntimeException("Ce lien a déjà été utilisé");
        }

        if (resetToken.isExpired()) {
            throw new RuntimeException("Ce lien a expiré");
        }
    }
}