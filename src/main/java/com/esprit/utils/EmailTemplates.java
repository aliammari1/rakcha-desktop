package com.esprit.utils;

/**
 * Utility class for generating professional HTML email templates with cinematic styling.
 * Provides pre-designed templates for common email scenarios with RAKCHA branding.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 2.0.0
 */
public class EmailTemplates {

    private static final String COLOR_PRIMARY = "#c41e3a";      // Deep cinema red
    private static final String COLOR_DARK = "#0d0d0d";         // Deep black
    private static final String COLOR_LIGHT = "#e0e0e0";        // Light gray
    private static final String COLOR_ACCENT = "#8b0000";       // Dark red accent
    private static final String FONT_FAMILY = "'Outfit', 'Segoe UI', Arial, sans-serif";

    /**
     * Generates a verification code email template.
     *
     * @param recipientName    the recipient's name
     * @param verificationCode the verification code
     * @param expiryMinutes    the code expiry time in minutes
     * @return HTML email template
     */
    public static String verificationCodeTemplate(final String recipientName, final String verificationCode, final int expiryMinutes) {
        return buildEmailTemplate(
            "RAKCHA - Verification Code",
            String.format(
                "<h1 style=\"color: %s; margin-top: 0;\">Your Verification Code</h1>\n" +
                    "<p style=\"font-size: 16px; line-height: 1.6;\">Hello %s,</p>\n" +
                    "<p style=\"font-size: 14px; color: #999;\">Your secure verification code is:</p>\n" +
                    "<div style=\"background: linear-gradient(135deg, %s, %s); padding: 20px; border-radius: 8px; " +
                    "text-align: center; margin: 20px 0; box-shadow: 0 4px 15px rgba(196, 30, 58, 0.4);\">\n" +
                    "  <p style=\"font-size: 36px; font-weight: bold; color: white; margin: 0; letter-spacing: 5px;\">%s</p>\n" +
                    "</div>\n" +
                    "<p style=\"font-size: 14px; color: %s; font-weight: bold;\">⏱ This code expires in %d minutes</p>\n" +
                    "<p style=\"font-size: 13px; color: #999; margin-top: 20px;\">If you didn't request this code, please ignore this email.</p>",
                COLOR_PRIMARY,
                recipientName.isEmpty() ? "User" : recipientName,
                COLOR_PRIMARY,
                COLOR_ACCENT,
                verificationCode,
                COLOR_PRIMARY,
                expiryMinutes
            )
        );
    }

    /**
     * Generates a welcome/notification email template.
     *
     * @param subject       the email subject
     * @param recipientName the recipient's name
     * @param message       the main message content
     * @return HTML email template
     */
    public static String welcomeTemplate(final String subject, final String recipientName, final String message) {
        return buildEmailTemplate(
            subject,
            String.format(
                "<h1 style=\"color: %s; margin-top: 0;\">%s</h1>\n" +
                    "<p style=\"font-size: 16px; line-height: 1.8; color: %s;\">Hello %s,</p>\n" +
                    "<div style=\"margin: 20px 0; padding: 15px; border-left: 4px solid %s; background: rgba(196, 30, 58, 0.08);\">\n" +
                    "  <p style=\"font-size: 14px; line-height: 1.8; color: %s; margin: 0;\">%s</p>\n" +
                    "</div>",
                COLOR_PRIMARY,
                subject,
                COLOR_LIGHT,
                recipientName.isEmpty() ? "User" : recipientName,
                COLOR_PRIMARY,
                COLOR_LIGHT,
                message
            )
        );
    }

    /**
     * Generates a professional notification email template.
     *
     * @param subject    the email subject
     * @param title      the main title
     * @param content    the email content
     * @param actionUrl  optional action URL
     * @param actionText optional action button text
     * @return HTML email template
     */
    public static String notificationTemplate(final String subject, final String title, final String content,
                                              final String actionUrl, final String actionText) {
        StringBuilder html = new StringBuilder();
        html.append(String.format(
            "<h1 style=\"color: %s; margin-top: 0;\">%s</h1>\n" +
                "<div style=\"margin: 20px 0; padding: 20px; background: rgba(196, 30, 58, 0.08); " +
                "border-radius: 8px; border-top: 3px solid %s;\">\n" +
                "  <p style=\"font-size: 15px; line-height: 1.8; color: %s; margin: 0;\">%s</p>\n" +
                "</div>",
            COLOR_PRIMARY,
            title,
            COLOR_PRIMARY,
            COLOR_LIGHT,
            content
        ));

        if (actionUrl != null && !actionUrl.isEmpty() && actionText != null && !actionText.isEmpty()) {
            html.append(String.format(
                "\n<div style=\"text-align: center; margin-top: 30px;\">\n" +
                    "  <a href=\"%s\" style=\"display: inline-block; background: linear-gradient(135deg, %s, %s); " +
                    "color: white; padding: 12px 40px; border-radius: 5px; text-decoration: none; " +
                    "font-weight: bold; font-size: 14px;\">%s</a>\n" +
                    "</div>",
                actionUrl,
                COLOR_PRIMARY,
                COLOR_ACCENT,
                actionText
            ));
        }

        return buildEmailTemplate(subject, html.toString());
    }

    /**
     * Generates a password reset email template.
     *
     * @param recipientName the recipient's name
     * @param resetLink     the password reset link
     * @param expiryHours   the link expiry time in hours
     * @return HTML email template
     */
    public static String passwordResetTemplate(final String recipientName, final String resetLink, final int expiryHours) {
        return buildEmailTemplate(
            "RAKCHA - Reset Your Password",
            String.format(
                "<h1 style=\"color: %s; margin-top: 0;\">Password Reset Request</h1>\n" +
                    "<p style=\"font-size: 16px; line-height: 1.6; color: %s;\">Hello %s,</p>\n" +
                    "<p style=\"font-size: 14px; color: #999; margin: 20px 0;\">We received a request to reset your password. " +
                    "Click the button below to create a new password:</p>\n" +
                    "<div style=\"text-align: center; margin: 30px 0;\">\n" +
                    "  <a href=\"%s\" style=\"display: inline-block; background: linear-gradient(135deg, %s, %s); " +
                    "color: white; padding: 14px 40px; border-radius: 5px; text-decoration: none; " +
                    "font-weight: bold; font-size: 16; box-shadow: 0 4px 15px rgba(196, 30, 58, 0.4);\">Reset Password</a>\n" +
                    "</div>\n" +
                    "<p style=\"font-size: 13px; color: %s;\">⏱ This link expires in %d hours</p>\n" +
                    "<p style=\"font-size: 13px; color: #999; margin-top: 20px;\">If you didn't request this reset, please ignore this email.</p>",
                COLOR_PRIMARY,
                COLOR_LIGHT,
                recipientName.isEmpty() ? "User" : recipientName,
                resetLink,
                COLOR_PRIMARY,
                COLOR_ACCENT,
                COLOR_PRIMARY,
                expiryHours
            )
        );
    }

    /**
     * Generates a success/confirmation email template.
     *
     * @param title   the main title
     * @param message the success message
     * @param details optional additional details
     * @return HTML email template
     */
    public static String successTemplate(final String title, final String message, final String details) {
        StringBuilder html = new StringBuilder();
        html.append(String.format(
            "<div style=\"text-align: center; margin-bottom: 30px;\">\n" +
                "  <p style=\"font-size: 48px; margin: 0;\">✓</p>\n" +
                "</div>\n" +
                "<h1 style=\"color: %s; margin-top: 0;\">%s</h1>\n" +
                "<p style=\"font-size: 16px; line-height: 1.8; color: %s;\">%s</p>",
            COLOR_PRIMARY,
            title,
            COLOR_LIGHT,
            message
        ));

        if (details != null && !details.isEmpty()) {
            html.append(String.format(
                "\n<div style=\"margin-top: 20px; padding: 15px; background: rgba(196, 30, 58, 0.08); " +
                    "border-left: 4px solid %s; border-radius: 5px;\">\n" +
                    "  <p style=\"font-size: 13px; color: %s; margin: 0;\">%s</p>\n" +
                    "</div>",
                COLOR_PRIMARY,
                COLOR_LIGHT,
                details
            ));
        }

        return buildEmailTemplate("Success", html.toString());
    }

    /**
     * Builds a complete HTML email template with RAKCHA branding and styling.
     *
     * @param subject the email subject
     * @param content the main HTML content
     * @return complete HTML email document
     */
    private static String buildEmailTemplate(final String subject, final String content) {
        return String.format(
            "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>%s</title>\n" +
                "  <style>\n" +
                "    * { margin: 0; padding: 0; }\n" +
                "    body { font-family: %s; background-color: %s; color: %s; }\n" +
                "    .email-container { max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, rgba(20, 25, 55, 1), rgba(15, 18, 45, 1)); " +
                "border: 1px solid %s; border-radius: 10px; overflow: hidden; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5); }\n" +
                "    .email-header { background: linear-gradient(135deg, %s, %s); padding: 30px 20px; text-align: center; border-bottom: 3px solid %s; }\n" +
                "    .email-header h1 { color: white; font-size: 24px; margin: 0; }\n" +
                "    .email-body { padding: 40px 30px; }\n" +
                "    .email-footer { background-color: rgba(10, 14, 27, 0.8); padding: 20px 30px; text-align: center; " +
                "border-top: 1px solid %s; font-size: 12px; color: #999; }\n" +
                "    a { color: %s; text-decoration: none; }\n" +
                "    .divider { height: 1px; background: linear-gradient(90deg, transparent, %s, transparent); margin: 30px 0; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"email-container\">\n" +
                "    <div class=\"email-header\">\n" +
                "      <h1>🎬 RAKCHA</h1>\n" +
                "    </div>\n" +
                "    <div class=\"email-body\">\n" +
                "      %s\n" +
                "      <div class=\"divider\"></div>\n" +
                "      <p style=\"font-size: 13px; color: #999; margin-top: 30px;\">\n" +
                "        Best regards,<br>\n" +
                "        <strong>The RAKCHA Team</strong>\n" +
                "      </p>\n" +
                "    </div>\n" +
                "    <div class=\"email-footer\">\n" +
                "      <p>This is an automated message from RAKCHA. Please do not reply to this email.</p>\n" +
                "      <p style=\"margin-top: 10px;\">© 2025 RAKCHA. All rights reserved.</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>",
            subject,
            FONT_FAMILY,
            COLOR_DARK,
            COLOR_LIGHT,
            COLOR_PRIMARY,
            COLOR_PRIMARY,
            COLOR_ACCENT,
            COLOR_PRIMARY,
            COLOR_PRIMARY,
            COLOR_PRIMARY,
            COLOR_PRIMARY,
            content
        );
    }
}
