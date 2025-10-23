package com.rentalplatform.service;

import com.rentalplatform.model.Rental;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@rentalplatform.com");
            helper.setTo(toEmail);
            helper.setSubject("RentalHub - Réinitialisation de votre mot de passe");

            String htmlContent = buildPasswordResetEmail(resetLink, userName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    public void sendPasswordChangedConfirmation(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@rentalplatform.com");
            helper.setTo(toEmail);
            helper.setSubject("RentalHub - Mot de passe modifié avec succès");

            String htmlContent = buildPasswordChangedEmail(userName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    private String buildPasswordResetEmail(String resetLink, String userName) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Réinitialisation de mot de passe</title>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; background-color: #f3f4f6;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr>" +
                "            <td style='padding: 40px 0;'>" +
                "                <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px; text-align: center; border-radius: 16px 16px 0 0;'>" +
                "                            <h1 style='margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;'>🏗️ RentalHub</h1>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Content -->" +
                "                    <tr>" +
                "                        <td style='padding: 40px;'>" +
                "                            <h2 style='margin: 0 0 20px; color: #1f2937; font-size: 24px; font-weight: 600;'>Réinitialisation de mot de passe</h2>" +
                "                            <p style='margin: 0 0 16px; color: #4b5563; font-size: 16px; line-height: 1.6;'>Bonjour <strong>" + userName + "</strong>,</p>" +
                "                            <p style='margin: 0 0 24px; color: #4b5563; font-size: 16px; line-height: 1.6;'>Vous avez demandé la réinitialisation de votre mot de passe. Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>" +
                "                            <!-- Button -->" +
                "                            <table role='presentation' style='margin: 32px 0;'>" +
                "                                <tr>" +
                "                                    <td style='text-align: center;'>" +
                "                                        <a href='" + resetLink + "' style='display: inline-block; padding: 16px 32px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px; box-shadow: 0 4px 6px rgba(102, 126, 234, 0.4);'>Réinitialiser mon mot de passe</a>" +
                "                                    </td>" +
                "                                </tr>" +
                "                            </table>" +
                "                            <!-- Info Box -->" +
                "                            <div style='background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 16px; border-radius: 8px; margin: 24px 0;'>" +
                "                                <p style='margin: 0; color: #92400e; font-size: 14px; line-height: 1.6;'>" +
                "                                    <strong>⏰ Important :</strong> Ce lien expirera dans <strong>1 heure</strong> pour des raisons de sécurité." +
                "                                </p>" +
                "                            </div>" +
                "                            <p style='margin: 24px 0 0; color: #6b7280; font-size: 14px; line-height: 1.6;'>Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :</p>" +
                "                            <p style='margin: 8px 0 0; word-break: break-all;'>" +
                "                                <a href='" + resetLink + "' style='color: #667eea; font-size: 14px;'>" + resetLink + "</a>" +
                "                            </p>" +
                "                            <hr style='border: none; border-top: 1px solid #e5e7eb; margin: 32px 0;'>" +
                "                            <p style='margin: 0; color: #6b7280; font-size: 14px; line-height: 1.6;'>Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email en toute sécurité.</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style='background-color: #f9fafb; padding: 24px; text-align: center; border-radius: 0 0 16px 16px;'>" +
                "                            <p style='margin: 0 0 8px; color: #6b7280; font-size: 14px;'>© 2025 RentalHub. Tous droits réservés.</p>" +
                "                            <p style='margin: 0; color: #9ca3af; font-size: 12px;'>Plateforme de location de matériel professionnel</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordChangedEmail(String userName) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Mot de passe modifié</title>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif; background-color: #f3f4f6;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr>" +
                "            <td style='padding: 40px 0;'>" +
                "                <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
                "                    <!-- Header -->" +
                "                    <tr>" +
                "                        <td style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); padding: 40px; text-align: center; border-radius: 16px 16px 0 0;'>" +
                "                            <h1 style='margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;'>🏗️ RentalHub</h1>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Content -->" +
                "                    <tr>" +
                "                        <td style='padding: 40px;'>" +
                "                            <div style='text-align: center; margin-bottom: 24px;'>" +
                "                                <div style='display: inline-block; width: 80px; height: 80px; background-color: #d1fae5; border-radius: 50%; display: flex; align-items: center; justify-content: center;'>" +
                "                                    <span style='font-size: 40px;'>✅</span>" +
                "                                </div>" +
                "                            </div>" +
                "                            <h2 style='margin: 0 0 20px; color: #1f2937; font-size: 24px; font-weight: 600; text-align: center;'>Mot de passe modifié avec succès</h2>" +
                "                            <p style='margin: 0 0 16px; color: #4b5563; font-size: 16px; line-height: 1.6; text-align: center;'>Bonjour <strong>" + userName + "</strong>,</p>" +
                "                            <p style='margin: 0 0 24px; color: #4b5563; font-size: 16px; line-height: 1.6; text-align: center;'>Votre mot de passe a été modifié avec succès.</p>" +
                "                            <!-- Security Alert -->" +
                "                            <div style='background-color: #fee2e2; border-left: 4px solid #ef4444; padding: 16px; border-radius: 8px; margin: 24px 0;'>" +
                "                                <p style='margin: 0; color: #991b1b; font-size: 14px; line-height: 1.6;'>" +
                "                                    <strong>🔒 Sécurité :</strong> Si vous n'êtes pas à l'origine de cette modification, veuillez contacter immédiatement notre support à <a href='mailto:support@rentalplatform.com' style='color: #991b1b; text-decoration: underline;'>support@rentalplatform.com</a>" +
                "                                </p>" +
                "                            </div>" +
                "                            <div style='text-align: center; margin-top: 32px;'>" +
                "                                <a href='http://localhost:4200/login' style='display: inline-block; padding: 16px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px; box-shadow: 0 4px 6px rgba(16, 185, 129, 0.4);'>Se connecter</a>" +
                "                            </div>" +
                "                        </td>" +
                "                    </tr>" +
                "                    <!-- Footer -->" +
                "                    <tr>" +
                "                        <td style='background-color: #f9fafb; padding: 24px; text-align: center; border-radius: 0 0 16px 16px;'>" +
                "                            <p style='margin: 0 0 8px; color: #6b7280; font-size: 14px;'>© 2025 RentalHub. Tous droits réservés.</p>" +
                "                            <p style='margin: 0; color: #9ca3af; font-size: 12px;'>Plateforme de location de matériel professionnel</p>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }

    public void sendRentalRequestToOwner(Rental rental) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@rentalplatform.com");
            helper.setTo(rental.getOwner().getEmail());
            helper.setSubject("RentalHub - Nouvelle demande de location");

            String htmlContent = buildRentalRequestEmail(rental);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    public void sendRentalAcceptedToClient(Rental rental) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@rentalplatform.com");
            helper.setTo(rental.getClient().getEmail());
            helper.setSubject("RentalHub - Votre demande de location a été acceptée");

            String htmlContent = buildRentalAcceptedEmail(rental);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    public void sendRentalRejectedToClient(Rental rental) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@rentalplatform.com");
            helper.setTo(rental.getClient().getEmail());
            helper.setSubject("RentalHub - Votre demande de location a été refusée");

            String htmlContent = buildRentalRejectedEmail(rental);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    private String buildRentalRequestEmail(Rental rental) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>" +
                "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #f3f4f6;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr><td style='padding: 40px 0;'>" +
                "            <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
                "                <tr><td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px; text-align: center; border-radius: 16px 16px 0 0;'>" +
                "                    <h1 style='margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;'>🏗️ RentalHub</h1>" +
                "                </td></tr>" +
                "                <tr><td style='padding: 40px;'>" +
                "                    <h2 style='margin: 0 0 20px; color: #1f2937; font-size: 24px; font-weight: 600;'>Nouvelle demande de location</h2>" +
                "                    <p style='margin: 0 0 16px; color: #4b5563; font-size: 16px;'>Bonjour <strong>" + rental.getOwner().getFullName() + "</strong>,</p>" +
                "                    <p style='margin: 0 0 24px; color: #4b5563; font-size: 16px;'>Vous avez reçu une nouvelle demande de location pour votre équipement :</p>" +
                "                    <div style='background-color: #f9fafb; border-radius: 8px; padding: 20px; margin: 24px 0;'>" +
                "                        <h3 style='margin: 0 0 12px; color: #1f2937; font-size: 18px;'>📦 " + rental.getProduct().getName() + "</h3>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Client:</strong> " + rental.getClient().getFullName() + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Email:</strong> " + rental.getClient().getEmail() + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Du:</strong> " + rental.getStartDate().format(formatter) + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Au:</strong> " + rental.getEndDate().format(formatter) + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Durée:</strong> " + rental.getNumberOfDays() + " jours</p>" +
                "                        <p style='margin: 8px 0; color: #1f2937; font-size: 18px;'><strong>Prix total:</strong> " + rental.getTotalPrice() + "€</p>" +
                (rental.getClientMessage() != null && !rental.getClientMessage().isEmpty() ?
                        "                        <div style='margin-top: 16px; padding: 12px; background-color: #e0e7ff; border-radius: 6px;'>" +
                                "                            <p style='margin: 0; color: #3730a3; font-size: 14px;'><strong>Message du client:</strong></p>" +
                                "                            <p style='margin: 8px 0 0; color: #4338ca;'>" + rental.getClientMessage() + "</p>" +
                                "                        </div>" : "") +
                "                    </div>" +
                "                    <table role='presentation' style='margin: 32px 0;'><tr>" +
                "                        <td style='padding-right: 8px;'><a href='http://localhost:4200/owner/rentals' style='display: inline-block; padding: 16px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600;'>Gérer mes locations</a></td>" +
                "                    </tr></table>" +
                "                </td></tr>" +
                "                <tr><td style='background-color: #f9fafb; padding: 24px; text-align: center; border-radius: 0 0 16px 16px;'>" +
                "                    <p style='margin: 0 0 8px; color: #6b7280; font-size: 14px;'>© 2025 RentalHub. Tous droits réservés.</p>" +
                "                </td></tr>" +
                "            </table>" +
                "        </td></tr>" +
                "    </table>" +
                "</body></html>";
    }

    private String buildRentalAcceptedEmail(Rental rental) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>" +
                "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #f3f4f6;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr><td style='padding: 40px 0;'>" +
                "            <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
                "                <tr><td style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); padding: 40px; text-align: center; border-radius: 16px 16px 0 0;'>" +
                "                    <h1 style='margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;'>🏗️ RentalHub</h1>" +
                "                </td></tr>" +
                "                <tr><td style='padding: 40px;'>" +
                "                    <div style='text-align: center; margin-bottom: 24px;'><span style='font-size: 60px;'>✅</span></div>" +
                "                    <h2 style='margin: 0 0 20px; color: #1f2937; font-size: 24px; font-weight: 600; text-align: center;'>Location acceptée !</h2>" +
                "                    <p style='margin: 0 0 16px; color: #4b5563; font-size: 16px; text-align: center;'>Bonjour <strong>" + rental.getClient().getFullName() + "</strong>,</p>" +
                "                    <p style='margin: 0 0 24px; color: #4b5563; font-size: 16px; text-align: center;'>Bonne nouvelle ! Votre demande de location a été acceptée.</p>" +
                "                    <div style='background-color: #f0fdf4; border-radius: 8px; padding: 20px; margin: 24px 0;'>" +
                "                        <h3 style='margin: 0 0 12px; color: #1f2937; font-size: 18px;'>📦 " + rental.getProduct().getName() + "</h3>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Propriétaire:</strong> " + rental.getOwner().getFullName() + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Du:</strong> " + rental.getStartDate().format(formatter) + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Au:</strong> " + rental.getEndDate().format(formatter) + "</p>" +
                "                        <p style='margin: 8px 0; color: #1f2937; font-size: 18px;'><strong>Prix total:</strong> " + rental.getTotalPrice() + "€</p>" +
                (rental.getOwnerResponse() != null && !rental.getOwnerResponse().isEmpty() ?
                        "                        <div style='margin-top: 16px; padding: 12px; background-color: #dbeafe; border-radius: 6px;'>" +
                                "                            <p style='margin: 0; color: #1e3a8a; font-size: 14px;'><strong>Message du propriétaire:</strong></p>" +
                                "                            <p style='margin: 8px 0 0; color: #1e40af;'>" + rental.getOwnerResponse() + "</p>" +
                                "                        </div>" : "") +
                "                    </div>" +
                "                    <table role='presentation' style='margin: 32px 0;'><tr>" +
                "                        <td style='text-align: center;'><a href='http://localhost:4200/client/reservations' style='display: inline-block; padding: 16px 32px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600;'>Voir mes réservations</a></td>" +
                "                    </tr></table>" +
                "                </td></tr>" +
                "                <tr><td style='background-color: #f9fafb; padding: 24px; text-align: center; border-radius: 0 0 16px 16px;'>" +
                "                    <p style='margin: 0 0 8px; color: #6b7280; font-size: 14px;'>© 2025 RentalHub. Tous droits réservés.</p>" +
                "                </td></tr>" +
                "            </table>" +
                "        </td></tr>" +
                "    </table>" +
                "</body></html>";
    }

    private String buildRentalRejectedEmail(Rental rental) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>" +
                "<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; background-color: #f3f4f6;'>" +
                "    <table role='presentation' style='width: 100%; border-collapse: collapse;'>" +
                "        <tr><td style='padding: 40px 0;'>" +
                "            <table role='presentation' style='width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>" +
                "                <tr><td style='background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); padding: 40px; text-align: center; border-radius: 16px 16px 0 0;'>" +
                "                    <h1 style='margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;'>🏗️ RentalHub</h1>" +
                "                </td></tr>" +
                "                <tr><td style='padding: 40px;'>" +
                "                    <div style='text-align: center; margin-bottom: 24px;'><span style='font-size: 60px;'>❌</span></div>" +
                "                    <h2 style='margin: 0 0 20px; color: #1f2937; font-size: 24px; font-weight: 600; text-align: center;'>Location refusée</h2>" +
                "                    <p style='margin: 0 0 16px; color: #4b5563; font-size: 16px; text-align: center;'>Bonjour <strong>" + rental.getClient().getFullName() + "</strong>,</p>" +
                "                    <p style='margin: 0 0 24px; color: #4b5563; font-size: 16px; text-align: center;'>Malheureusement, votre demande de location a été refusée.</p>" +
                "                    <div style='background-color: #fef2f2; border-radius: 8px; padding: 20px; margin: 24px 0;'>" +
                "                        <h3 style='margin: 0 0 12px; color: #1f2937; font-size: 18px;'>📦 " + rental.getProduct().getName() + "</h3>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Du:</strong> " + rental.getStartDate().format(formatter) + "</p>" +
                "                        <p style='margin: 8px 0; color: #4b5563;'><strong>Au:</strong> " + rental.getEndDate().format(formatter) + "</p>" +
                (rental.getOwnerResponse() != null && !rental.getOwnerResponse().isEmpty() ?
                        "                        <div style='margin-top: 16px; padding: 12px; background-color: #fee2e2; border-radius: 6px;'>" +
                                "                            <p style='margin: 0; color: #7f1d1d; font-size: 14px;'><strong>Raison:</strong></p>" +
                                "                            <p style='margin: 8px 0 0; color: #991b1b;'>" + rental.getOwnerResponse() + "</p>" +
                                "                        </div>" : "") +
                "                    </div>" +
                "                    <p style='margin: 24px 0; color: #4b5563; font-size: 14px; text-align: center;'>N'hésitez pas à explorer d'autres équipements disponibles sur notre plateforme.</p>" +
                "                    <table role='presentation' style='margin: 32px 0;'><tr>" +
                "                        <td style='text-align: center;'><a href='http://localhost:4200/equipment' style='display: inline-block; padding: 16px 32px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600;'>Parcourir les équipements</a></td>" +
                "                    </tr></table>" +
                "                </td></tr>" +
                "                <tr><td style='background-color: #f9fafb; padding: 24px; text-align: center; border-radius: 0 0 16px 16px;'>" +
                "                    <p style='margin: 0 0 8px; color: #6b7280; font-size: 14px;'>© 2025 RentalHub. Tous droits réservés.</p>" +
                "                </td></tr>" +
                "            </table>" +
                "        </td></tr>" +
                "    </table>" +
                "</body></html>";
    }
}