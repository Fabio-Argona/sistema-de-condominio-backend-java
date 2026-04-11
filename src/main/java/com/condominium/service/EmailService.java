package com.condominium.service;

import com.condominium.model.Boleto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarNovaSenha(String destinatario, String nomeUsuario, String novaSenha) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatario);
            helper.setSubject("🔑 Residencial Oceano - Recuperação de Senha");
            helper.setText(buildHtmlEmail(nomeUsuario, novaSenha), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    @Async
    public void enviarEmailNovaReserva(String destinatarioSindico, String nomeMorador, String areaNome, String data, String horario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatarioSindico);
            helper.setSubject("🔔 Nova Solicitação de Reserva - " + areaNome);
            helper.setText(buildNovaReservaHtml(nomeMorador, areaNome, data, horario), true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao enviar aviso de reserva: " + e.getMessage());
        }
    }

    @Async
    public void enviarEmailStatusReserva(String destinatarioMorador, String areaNome, String data, String horario, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String statusIcon = "APROVADA".equalsIgnoreCase(status) ? "✅" : "❌";
            helper.setFrom("***REMOVED***");
            helper.setTo(destinatarioMorador);
            helper.setSubject(statusIcon + " Atualização de Reserva - " + areaNome);
            helper.setText(buildStatusReservaHtml(areaNome, data, horario, status), true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao enviar status de reserva: " + e.getMessage());
        }
    }

    @Async
    public void enviarConvite(String destinatario, String nomeUsuario, String senhaTemporaria, String apartamento, String bloco) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatario);
            helper.setSubject("🏠 Residencial Oceano - Bem-vindo(a) ao Condomínio!");
            helper.setText(buildConviteHtml(nomeUsuario, destinatario, senhaTemporaria, apartamento, bloco), true);

            mailSender.send(message);
            System.out.println("[EmailService] Convite enviado com sucesso para: " + destinatario);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de convite: " + e.getMessage(), e);
        }
    }

    private String buildConviteHtml(String nomeUsuario, String email, String senhaTemporaria, String apartamento, String bloco) {
        String unidade = (apartamento != null && bloco != null) 
            ? "Casa/Apt " + apartamento + " - Bloco " + bloco 
            : "Não informada";
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f1f5f9; padding:40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0" style="background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #059669 0%%, #10b981 50%%, #047857 100%%); padding:36px 40px; text-align:center;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td align="center">
                                                         <div style="display:inline-block; border-radius:2px; padding:2px; margin-bottom:2px;">
                                                            <img src="https://sistema-de-condominio-frontend-next.vercel.app/oceano-logo.png" alt="Logo Oceano" width="180" style="display:block; border-radius:8px; padding:2px; object-fit:contain;" />
                                                        </div>
                                                    <p style="margin:0 0 -2px 0; font-size:11px; font-weight:700; letter-spacing:3px; color:rgba(255,255,255,0.7); text-transform:uppercase;">Residencial</p>
                                                    <h1 style="margin:0; font-size:32px; font-weight:900; color:#ffffff; letter-spacing:1px;">OCEANO</h1>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding:40px;">
                                        <h2 style="margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1e293b;">🎉 Bem-vindo(a) ao Residencial Oceano!</h2>
                                        <p style="margin:0 0 28px 0; font-size:15px; color:#64748b; line-height:1.6;">
                                            Olá, <strong style="color:#1e293b;">%s</strong>! Seu cadastro foi realizado com sucesso pela administração do condomínio. Agora você tem acesso ao nosso sistema de gestão.
                                        </p>
                                        <!-- Unidade Box -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:20px;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #f0fdf4 0%%, #dcfce7 100%%); border:2px solid #86efac; border-radius:12px; padding:20px; text-align:center;">
                                                    <p style="margin:0 0 4px 0; font-size:12px; font-weight:600; color:#16a34a; text-transform:uppercase; letter-spacing:2px;">Sua Unidade</p>
                                                    <p style="margin:0; font-size:20px; font-weight:800; color:#15803d;">%s</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Credentials Box -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:20px;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #eff6ff 0%%, #e0f2fe 100%%); border:2px solid #bfdbfe; border-radius:12px; padding:24px;">
                                                    <p style="margin:0 0 16px 0; font-size:14px; font-weight:700; color:#1e40af; text-align:center;">Seus dados de acesso:</p>
                                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td style="padding:8px 0;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:1px;">E-mail</p>
                                                                <p style="margin:4px 0 0 0; font-size:16px; font-weight:600; color:#1e293b;">%s</p>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:8px 0; border-top:1px solid #bfdbfe;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:1px;">Senha temporária</p>
                                                                <p style="margin:4px 0 0 0; font-size:28px; font-weight:900; color:#1e40af; letter-spacing:6px; font-family:'Courier New',monospace; text-align:center;">%s</p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Info -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td style="background-color:#fffbeb; border-left:4px solid #f59e0b; border-radius:0 8px 8px 0; padding:16px 20px;">
                                                    <p style="margin:0; font-size:13px; color:#92400e; line-height:1.6;">
                                                        <strong>⚠️ Importante:</strong> Ao fazer seu primeiro login, recomendamos alterar sua senha temporária por uma de sua preferência, através do seu perfil no sistema.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Button -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td align="center">
                                                    <a href="https://sistema-de-condominio-frontend-next.vercel.app/login" target="_blank" style="background-color: #10b981; color: #ffffff; padding: 18px 36px; text-decoration: none; border-radius: 12px; font-weight: 700; font-size: 16px; display: inline-block; border: 1px solid #059669;">Acessar Sistema do Condomínio</a>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin:0; font-size:14px; color:#94a3b8; line-height:1.6; text-align:center;">
                                            Caso tenha dúvidas, entre em contato com a administração do condomínio.
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color:#f8fafc; border-top:1px solid #e2e8f0; padding:24px 40px; text-align:center;">
                                        <p style="margin:0 0 4px 0; font-size:13px; font-weight:600; color:#475569;">Administração - Residencial Oceano</p>
                                        <p style="margin:0; font-size:12px; color:#94a3b8;">Este é um e-mail automático, por favor não responda.</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- Sub-footer -->
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td style="padding:20px 40px; text-align:center;">
                                        <p style="margin:0; font-size:11px; color:#94a3b8;">© 2026 Residencial Oceano. Todos os direitos reservados.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(nomeUsuario, unidade, email, senhaTemporaria);
    }

    private String buildHtmlEmail(String nomeUsuario, String novaSenha) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f1f5f9; padding:40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0" style="background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1e3a5f 0%%, #2563eb 50%%, #1e40af 100%%); padding:36px 40px; text-align:center;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td align="center">
                                                        <div style="display:inline-block; border-radius:2px; padding:2px; margin-bottom:2px;">
                                                            <img src="https://sistema-de-condominio-frontend-next.vercel.app/oceano-logo.png" alt="Logo Oceano" width="180" style="display:block; border-radius:8px; padding:2px; object-fit:contain;" />
                                                        </div>
                                                    <p style="margin:0 0 -2px 0; font-size:11px; font-weight:700; letter-spacing:3px; color:rgba(255,255,255,0.7); text-transform:uppercase;">Residencial</p>
                                                    <h1 style="margin:0; font-size:32px; font-weight:900; color:#ffffff; letter-spacing:1px;">OCEANO</h1>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding:40px;">
                                        <h2 style="margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1e293b;">Recuperação de Senha</h2>
                                        <p style="margin:0 0 28px 0; font-size:15px; color:#64748b; line-height:1.6;">
                                            Olá, <strong style="color:#1e293b;">%s</strong>! Recebemos uma solicitação de recuperação de senha para a sua conta.
                                        </p>
                                        <!-- Password Box -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #eff6ff 0%%, #e0f2fe 100%%); border:2px solid #bfdbfe; border-radius:12px; padding:24px; text-align:center;">
                                                    <p style="margin:0 0 8px 0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:2px;">Sua nova senha temporária</p>
                                                    <p style="margin:0; font-size:36px; font-weight:900; color:#1e40af; letter-spacing:6px; font-family:'Courier New',monospace;">%s</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Info -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td style="background-color:#fffbeb; border-left:4px solid #f59e0b; border-radius:0 8px 8px 0; padding:16px 20px;">
                                                    <p style="margin:0; font-size:13px; color:#92400e; line-height:1.6;">
                                                        <strong>⚠️ Importante:</strong> Use esta senha para fazer login e, por segurança, altere-a assim que possível através do seu perfil no sistema.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin:0; font-size:14px; color:#94a3b8; line-height:1.6;">
                                            Se você <strong>não solicitou</strong> esta recuperação, entre em contato com a administração do condomínio imediatamente.
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color:#f8fafc; border-top:1px solid #e2e8f0; padding:24px 40px; text-align:center;">
                                        <p style="margin:0 0 4px 0; font-size:13px; font-weight:600; color:#475569;">Administração - Residencial Oceano</p>
                                        <p style="margin:0; font-size:12px; color:#94a3b8;">Este é um e-mail automático, por favor não responda.</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- Sub-footer -->
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td style="padding:20px 40px; text-align:center;">
                                        <p style="margin:0; font-size:11px; color:#94a3b8;">© 2026 Residencial Oceano. Todos os direitos reservados.</p>
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

    private String buildNovaReservaHtml(String nomeMorador, String areaNome, String data, String horario) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:sans-serif; background-color:#f1f5f9; padding:20px;">
                <div style="max-width:560px; margin:0 auto; background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
                    <div style="background:linear-gradient(135deg, #1e3a5f 0%%, #2563eb 100%%); padding:30px; text-align:center; color:white;">
                        <h1 style="margin:0; font-size:24px;">🔔 Nova Solicitação</h1>
                    </div>
                    <div style="padding:30px;">
                        <p>Olá, <strong>Síndico</strong>!</p>
                        <p>O morador <strong>%s</strong> acaba de solicitar uma reserva para a área: <strong>%s</strong>.</p>
                        <div style="background-color:#f8fafc; padding:20px; border-radius:12px; margin:20px 0;">
                            <p style="margin:5px 0;"><strong>Data:</strong> %s</p>
                            <p style="margin:5px 0;"><strong>Horário:</strong> %s</p>
                        </div>
                        <p>Por favor, acesse o painel administrativo para aprovar ou rejeitar esta solicitação.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nomeMorador, areaNome, data, horario);
    }

    private String buildStatusReservaHtml(String areaNome, String data, String horario, String status) {
        String corStatus = "APROVADA".equalsIgnoreCase(status) ? "#10b981" : "#ef4444";
        String textoStatus = "APROVADA".equalsIgnoreCase(status) ? "Confirmada" : "Rejeitada";
        
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:sans-serif; background-color:#f1f5f9; padding:20px;">
                <div style="max-width:560px; margin:0 auto; background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
                    <div style="background:linear-gradient(135deg, #1e3a5f 0%%, #2563eb 100%%); padding:30px; text-align:center; color:white;">
                        <h1 style="margin:0; font-size:24px;">📅 Atualização de Reserva</h1>
                    </div>
                    <div style="padding:30px;">
                        <p>Olá!</p>
                        <p>Sua solicitação de reserva para <strong>%s</strong> foi processada:</p>
                        <div style="background-color:#f8fafc; padding:20px; border-radius:12px; margin:20px 0; text-align:center;">
                            <p style="margin:0; font-size:20px; font-weight:bold; color:%s;">%s</p>
                            <hr style="border:0; border-top:1px solid #e2e8f0; margin:15px 0;">
                            <p style="margin:5px 0;"><strong>Data:</strong> %s</p>
                            <p style="margin:5px 0;"><strong>Horário:</strong> %s</p>
                        </div>
                        <p style="font-size:14px; color:#64748b;">Se sua reserva foi aprovada, aproveite o espaço! Caso tenha sido rejeitada, entre em contato com a administração para mais detalhes.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(areaNome, corStatus, textoStatus, data, horario);
    }

    @Async
    public void enviarEmailCobranca(String destinatario, String nomeMorador, Boleto boleto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatario);
            helper.setSubject("⚠️ IMPORTANTE: Residencial Oceano - Boleto Vencido em Aberto");

            String dataVenc = boleto.getDataVencimento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String valor = String.format("R$ %,.2f", boleto.getValor());

            helper.setText(buildCobrancaHtml(nomeMorador, boleto.getDescricao(), valor, dataVenc), true);

            mailSender.send(message);
            System.out.println("[EmailService] E-mail de cobrança enviado para: " + destinatario);
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao enviar e-mail de cobrança: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de cobrança: " + e.getMessage(), e);
        }
    }

    private String buildCobrancaHtml(String nome, String descricao, String valor, String vencimento) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0; padding:0; background-color:#fef2f2; font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#fef2f2; padding:40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0" style="background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #7f1d1d 0%%, #dc2626 50%%, #b91c1c 100%%); padding:36px 40px; text-align:center;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td align="center">
                                                    <div style="display:inline-block; border-radius:2px; padding:2px; margin-bottom:2px;">
                                                        <img src="https://sistema-de-condominio-frontend-next.vercel.app/oceano-logo.png" alt="Logo Oceano" width="180" style="display:block; border-radius:8px; padding:2px; object-fit:contain;" />
                                                    </div>
                                                    <p style="margin:0 0 -2px 0; font-size:11px; font-weight:700; letter-spacing:3px; color:rgba(255,255,255,0.7); text-transform:uppercase;">Residencial</p>
                                                    <h1 style="margin:0; font-size:32px; font-weight:900; color:#ffffff; letter-spacing:1px;">OCEANO</h1>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Alert Banner -->
                                <tr>
                                    <td style="background-color:#fef2f2; border-bottom:2px solid #fecaca; padding:16px 40px; text-align:center;">
                                        <p style="margin:0; font-size:15px; font-weight:700; color:#dc2626; letter-spacing:1px;">⚠️ AVISO DE COBRANÇA — BOLETO VENCIDO</p>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding:40px;">
                                        <h2 style="margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1e293b;">Boleto em aberto identificado</h2>
                                        <p style="margin:0 0 28px 0; font-size:15px; color:#64748b; line-height:1.6;">
                                            Olá, <strong style="color:#1e293b;">%s</strong>! Identificamos um boleto vencido em seu nome. Regularize sua situação o quanto antes para evitar juros e encargos adicionais.
                                        </p>
                                        <!-- Boleto Details Box -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:20px;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #fff1f2 0%%, #fee2e2 100%%); border:2px solid #fecaca; border-radius:12px; padding:24px;">
                                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td style="padding:8px 0;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#dc2626; text-transform:uppercase; letter-spacing:1px;">Descrição</p>
                                                                <p style="margin:4px 0 0 0; font-size:16px; font-weight:600; color:#1e293b;">%s</p>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:8px 0; border-top:1px solid #fecaca;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#dc2626; text-transform:uppercase; letter-spacing:1px;">Valor</p>
                                                                <p style="margin:4px 0 0 0; font-size:28px; font-weight:900; color:#b91c1c; letter-spacing:2px;">%s</p>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:8px 0; border-top:1px solid #fecaca;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#dc2626; text-transform:uppercase; letter-spacing:1px;">Vencido em</p>
                                                                <p style="margin:4px 0 0 0; font-size:20px; font-weight:800; color:#dc2626;">%s</p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Warning Note -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td style="background-color:#fef2f2; border-left:4px solid #dc2626; border-radius:0 8px 8px 0; padding:16px 20px;">
                                                    <p style="margin:0; font-size:13px; color:#7f1d1d; line-height:1.6;"><strong>⚠️ Importante:</strong> Acesse o portal do condomínio para regularizar o pagamento e baixar o boleto atualizado.</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Button -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td align="center">
                                                    <a href="https://sistema-de-condominio-frontend-next.vercel.app/login" target="_blank" style="background-color:#dc2626; color:#ffffff; padding:18px 36px; text-decoration:none; border-radius:12px; font-weight:700; font-size:16px; display:inline-block; border:1px solid #b91c1c;">Acessar Portal e Regularizar</a>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin:0; font-size:14px; color:#94a3b8; line-height:1.6; text-align:center;">
                                            Em caso de dúvidas, entre em contato com a administração do condomínio.
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color:#f8fafc; border-top:1px solid #e2e8f0; padding:24px 40px; text-align:center;">
                                        <p style="margin:0 0 4px 0; font-size:13px; font-weight:600; color:#475569;">Administração - Residencial Oceano</p>
                                        <p style="margin:0; font-size:12px; color:#94a3b8;">Este é um e-mail automático, por favor não responda.</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- Sub-footer -->
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td style="padding:20px 40px; text-align:center;">
                                        <p style="margin:0; font-size:11px; color:#94a3b8;">© 2026 Residencial Oceano. Todos os direitos reservados.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(nome, descricao, valor, vencimento);
    }

    public void enviarEmailBoleto(String destinatario, String nomeMorador, Boleto boleto) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("***REMOVED***");
            helper.setTo(destinatario);
            helper.setSubject("💰 Residencial Oceano - Boleto Disponível");

            String dataVenc = boleto.getDataVencimento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String valor = String.format("R$ %,.2f", boleto.getValor());

            boolean temPdf = boleto.getPdfBase64() != null && !boleto.getPdfBase64().isBlank();
            helper.setText(buildBoletoHtml(nomeMorador, boleto.getDescricao(), valor, dataVenc, temPdf), true);

            if (temPdf) {
                String raw = boleto.getPdfBase64();
                if (raw.contains(",")) {
                    raw = raw.substring(raw.indexOf(',') + 1);
                }
                byte[] pdfBytes = Base64.getDecoder().decode(raw);
                helper.addAttachment("boleto.pdf", new ByteArrayResource(pdfBytes), "application/pdf");
            }

            mailSender.send(message);
            System.out.println("[EmailService] E-mail de boleto enviado para: " + destinatario);
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao enviar e-mail de boleto: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar e-mail de boleto: " + e.getMessage(), e);
        }
    }

    private String buildBoletoHtml(String nome, String descricao, String valor, String vencimento, boolean temPdf) {
        String notaPdf = temPdf
            ? "<strong>📎 PDF em anexo:</strong> O boleto em PDF está anexado a este e-mail. Você também pode acessá-lo pelo portal do condomínio."
            : "Acesse o portal do condomínio para visualizar e baixar o PDF do boleto.";
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0; padding:0; background-color:#f1f5f9; font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#f1f5f9; padding:40px 20px;">
                    <tr>
                        <td align="center">
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0" style="background-color:#ffffff; border-radius:16px; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                                <!-- Header -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #1e3a5f 0%%, #2563eb 50%%, #1e40af 100%%); padding:36px 40px; text-align:center;">
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td align="center">
                                                    <div style="display:inline-block; border-radius:2px; padding:2px; margin-bottom:2px;">
                                                        <img src="https://sistema-de-condominio-frontend-next.vercel.app/oceano-logo.png" alt="Logo Oceano" width="180" style="display:block; border-radius:8px; padding:2px; object-fit:contain;" />
                                                    </div>
                                                    <p style="margin:0 0 -2px 0; font-size:11px; font-weight:700; letter-spacing:3px; color:rgba(255,255,255,0.7); text-transform:uppercase;">Residencial</p>
                                                    <h1 style="margin:0; font-size:32px; font-weight:900; color:#ffffff; letter-spacing:1px;">OCEANO</h1>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <!-- Content -->
                                <tr>
                                    <td style="padding:40px;">
                                        <h2 style="margin:0 0 8px 0; font-size:22px; font-weight:700; color:#1e293b;">💰 Boleto Disponível</h2>
                                        <p style="margin:0 0 28px 0; font-size:15px; color:#64748b; line-height:1.6;">
                                            Olá, <strong style="color:#1e293b;">%s</strong>! Um boleto foi emitido para você. Confira os detalhes abaixo:
                                        </p>
                                        <!-- Boleto Details Box -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:20px;">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #eff6ff 0%%, #e0f2fe 100%%); border:2px solid #bfdbfe; border-radius:12px; padding:24px;">
                                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                                        <tr>
                                                            <td style="padding:8px 0;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:1px;">Descrição</p>
                                                                <p style="margin:4px 0 0 0; font-size:16px; font-weight:600; color:#1e293b;">%s</p>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:8px 0; border-top:1px solid #bfdbfe;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:1px;">Valor</p>
                                                                <p style="margin:4px 0 0 0; font-size:28px; font-weight:900; color:#1e40af; letter-spacing:2px;">%s</p>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:8px 0; border-top:1px solid #bfdbfe;">
                                                                <p style="margin:0; font-size:12px; font-weight:600; color:#3b82f6; text-transform:uppercase; letter-spacing:1px;">Vencimento</p>
                                                                <p style="margin:4px 0 0 0; font-size:20px; font-weight:800; color:#dc2626;">%s</p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- PDF Note -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td style="background-color:#fffbeb; border-left:4px solid #f59e0b; border-radius:0 8px 8px 0; padding:16px 20px;">
                                                    <p style="margin:0; font-size:13px; color:#92400e; line-height:1.6;">%s</p>
                                                </td>
                                            </tr>
                                        </table>
                                        <!-- Button -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin-bottom:28px;">
                                            <tr>
                                                <td align="center">
                                                    <a href="https://sistema-de-condominio-frontend-next.vercel.app/login" target="_blank" style="background-color:#1e40af; color:#ffffff; padding:18px 36px; text-decoration:none; border-radius:12px; font-weight:700; font-size:16px; display:inline-block; border:1px solid #1e3a5f;">Acessar Portal do Condomínio</a>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin:0; font-size:14px; color:#94a3b8; line-height:1.6; text-align:center;">
                                            Em caso de dúvidas, entre em contato com a administração do condomínio.
                                        </p>
                                    </td>
                                </tr>
                                <!-- Footer -->
                                <tr>
                                    <td style="background-color:#f8fafc; border-top:1px solid #e2e8f0; padding:24px 40px; text-align:center;">
                                        <p style="margin:0 0 4px 0; font-size:13px; font-weight:600; color:#475569;">Administração - Residencial Oceano</p>
                                        <p style="margin:0; font-size:12px; color:#94a3b8;">Este é um e-mail automático, por favor não responda.</p>
                                    </td>
                                </tr>
                            </table>
                            <!-- Sub-footer -->
                            <table role="presentation" width="560" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td style="padding:20px 40px; text-align:center;">
                                        <p style="margin:0; font-size:11px; color:#94a3b8;">© 2026 Residencial Oceano. Todos os direitos reservados.</p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(nome, descricao, valor, vencimento, notaPdf);
    }
}
