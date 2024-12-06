package commer.mmr.instock;

import android.widget.Toast;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

public class EnviarEmail {
    // Credenciais do e-mail de envio
    private final String emailRemetente = "math13thurow@gmail.com";
    private final String senhaRemetente = "jarrwgkxdpecbtbb";

    // Método para enviar o e-mail
    public boolean enviar(String emailDestinatario, String assunto, String mensagem) {
        // Configurações do servidor SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");                       // Habilitar autenticação
        props.put("mail.smtp.starttls.enable", "true");            // Habilitar STARTTLS
        props.put("mail.smtp.host", "smtp.gmail.com");             // Servidor SMTP do Gmail
        props.put("mail.smtp.port", "587");                        // Porta SMTP do Gmail

        // Sessão com autenticação
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailRemetente, senhaRemetente);
            }
        });

        try {
            // Criar mensagem de e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailRemetente));                 // Remetente
            message.setRecipients(Message.RecipientType.TO,                       // Destinatário
                    InternetAddress.parse(emailDestinatario));
            message.setSubject(assunto);                                          // Assunto
            message.setText(mensagem);                                            // Corpo do e-mail

            // Enviar e-mail
            Transport.send(message);
            return true; // E-mail enviado com sucesso
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Erro ao enviar o e-mail
        }
    }
}
