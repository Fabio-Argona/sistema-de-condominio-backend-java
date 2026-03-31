package com.condominium.service;

import com.condominium.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(Usuario usuario) {
        long now = System.currentTimeMillis();
        long exp = now + expiration;

        String header = base64Encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Encode(String.format(
            "{\"sub\":\"%s\",\"userId\":%d,\"nome\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
            usuario.getEmail(), usuario.getId(), usuario.getNome(),
            usuario.getRole().name(), now / 1000, exp / 1000
        ));

        String content = header + "." + payload;
        String signature = hmacSha256(content);

        return content + "." + signature;
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String content = parts[0] + "." + parts[1];
            String expectedSig = hmacSha256(content);
            if (!expectedSig.equals(parts[2])) return false;

            // Check expiration
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            long exp = Long.parseLong(payload.replaceAll(".*\"exp\":(\\d+).*", "$1"));
            return exp > System.currentTimeMillis() / 1000;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return payload.replaceAll(".*\"sub\":\"([^\"]+)\".*", "$1");
        } catch (Exception e) {
            return null;
        }
    }

    private String base64Encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar HMAC", e);
        }
    }
}
