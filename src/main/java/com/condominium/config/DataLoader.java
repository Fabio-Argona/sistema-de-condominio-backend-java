package com.condominium.config;

import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "prod"}) // Ativa tanto em local quanto em prod temporariamente
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Garantir usuário mestre do condomínio (Oceano)
        String masterEmail = "***REMOVED***";
        String senhaMaster = "148106";
        
        usuarioRepository.findByEmail(masterEmail).ifPresentOrElse(
            usuario -> {
                usuario.setSenha(passwordEncoder.encode(senhaMaster));
                usuario.setRole(Usuario.Role.SINDICO);
                usuarioRepository.save(usuario);
                System.out.println("✅ Senha do administrador mestre resetada com sucesso!");
            },
            () -> {
                Usuario master = new Usuario("Condomínio Oceano Admin", masterEmail, 
                    passwordEncoder.encode(senhaMaster), Usuario.Role.SINDICO);
                master.setTelefone("(12) 98276-0898");
                usuarioRepository.save(master);
                System.out.println("✅ Administrador mestre criado com sucesso!");
            }
        );

        // Criar usuários de demonstração se for o primeiro boot (local)
        if (usuarioRepository.count() <= 1) {
            String senhaHash = passwordEncoder.encode("123456");
            // ... (restante dos usuários se desejar)
        }
    }
}
