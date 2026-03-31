package com.condominium.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarNovaSenha(String destinatario, String nomeUsuario, String novaSenha) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatario);
            helper.setSubject("🔑 Residencial Oceano - Recuperação de Senha");
            helper.setText(buildHtmlEmail(nomeUsuario, novaSenha), true);

            // Tenta anexar imagem como inline (CID), mas não lança exceção se falhar
            try {
                java.io.File logoFile = new java.io.File("src/main/resources/static/img/oceano-logo.png");
                if (logoFile.exists()) {
                    helper.addInline("oceano-logo", logoFile);
                }
            } catch (Exception imgEx) {
                System.err.println("[EmailService] Falha ao anexar logo: " + imgEx.getMessage());
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    private String buildHtmlEmail(String nomeUsuario, String novaSenha) {
        return """
            <!DOCTYPE html>
            <html lang=\"pt-BR\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
            </head>
            <body style=\"margin:0; padding:0; background-color:#f1f5f9; font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;\">
                <table role=\"presentation\" width=\"100%%\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#f1f5f9; padding:40px 20px;\">
                    <tr>
                        <td align=\"center\">
                            <table role=\"presentation\" width=\"560\" cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);\">
                                <!-- Header -->
                                <tr>
                                    <td style=\"background: linear-gradient(135deg, #1e3a5f 0%%, #2563eb 50%%, #1e40af 100%%); padding:36px 40px; text-align:center;\">
                                        <table role=\"presentation\" width=\"100%%\" cellspacing=\"0\" cellpadding=\"0\">
                                            <tr>
                                                <td align=\"center\">
                                                        <div style=\"display:inline-block; background:rgba(255,255,255,0.15); border-radius:2px; padding:2px; margin-bottom:2px;\">
                                                            <img src=\"cid:oceano-logo\" alt=\"Logo Oceano\" width=\"200\" height=\"200\" style=\"display:block; background:#fff; border-radius:8px; padding:2px; object-fit:contain;\" />
                                                        </div>
                                                    <p style=\"margin:0 0 -2px 0; font-size:11px; font-weight:700; letter-spacing:3px; color:rgba(255,255,255,0.7); text-transform:uppercase;\">Residencial</p>
                                                    <h1 style=\"margin:0; font-size:32px; font-weight:900; color:#ffffff; letter-spacing:1px;\">OCEANO</h1>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style=\"padding:40px;\">
                                        <h2 style=\"margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1e293b;\">Recuperação de Senha</h2>
                                        <p style=\"margin:0 0 28px 0; font-size:15px; color:#64748b; line-height:1.6;\">
                                            Olá, <strong style=\"color:#1e293b;\">%s</strong>! Recebemos uma solicitação de recuperação de senha para a sua conta.
                                        </p>
                                        <!-- Password Box -->
                                        <table role=\"presentation\" width=\"100%%\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin-bottom:28px;\">
                                            <tr>
                                                <td style=\"background: linear-gradient(135deg, #eff6ff 0%%, #e0f2fe 100%%); border:2px solid #bfdbfe; border-radius:12px; padding:24px; text-align:center;\">
                                                    <p style=\"margin:0 0 8px 0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:2px;\">Sua nova senha temporária</p>
                                                    <p style=\"margin:0; font-size:36px; font-weight:900; color:#1e40af; letter-spacing:6px; font-family:'Courier New',monospace;\">%s</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Info -->
                                        <table role=\"presentation\" width=\"100%%\" cellspacing=\"0\" cellpadding=\"0\" style=\"margin-bottom:28px;\">
                                            <tr>
                                                <td style=\"background-color:#fffbeb; border-left:4px solid #f59e0b; border-radius:0 8px 8px 0; padding:16px 20px;\">
                                                    <p style=\"margin:0; font-size:13px; color:#92400e; line-height:1.6;\">
                                                        <strong>⚠️ Importante:</strong> Use esta senha para fazer login e, por segurança, altere-a assim que possível através do seu perfil no sistema.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style=\"margin:0; font-size:14px; color:#94a3b8; line-height:1.6;\">
                                            Se você <strong>não solicitou</strong> esta recuperação, entre em contato com a administração do condomínio imediatamente.
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style=\"background-color:#f8fafc; border-top:1px solid #e2e8f0; padding:24px 40px; text-align:center;\">
                                        <p style=\"margin:0 0 4px 0; font-size:13px; font-weight:600; color:#475569;\">Administração - Residencial Oceano</p>
                                        <p style=\"margin:0; font-size:12px; color:#94a3b8;\">Este é um e-mail automático, por favor não responda.</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- Sub-footer -->
                            <table role=\"presentation\" width=\"560\" cellspacing=\"0\" cellpadding=\"0\">
                                <tr>
                                    <td style=\"padding:20px 40px; text-align:center;\">
                                        <p style=\"margin:0; font-size:11px; color:#94a3b8;\">© 2026 Residencial Oceano. Todos os direitos reservados.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(nomeUsuario, novaSenha);
    }
}
