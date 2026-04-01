package com.condominium.config;

import com.condominium.model.Usuario;
import com.condominium.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Criar usuários de demonstração se não existirem
        if (usuarioRepository.count() == 0) {
            String senhaHash = passwordEncoder.encode("123456");

            Usuario sindico = new Usuario("Admin Síndico", "sindico@condogest.com", senhaHash, Usuario.Role.SINDICO);
            sindico.setTelefone("(11) 99999-0001");
            usuarioRepository.save(sindico);

            Usuario morador = new Usuario("João Morador", "morador@condogest.com", senhaHash, Usuario.Role.MORADOR);
            morador.setApartamento("302");
            morador.setBloco("A");
            morador.setTelefone("(11) 99999-0002");
            morador.setCpf("123.456.789-00");
            usuarioRepository.save(morador);

            Usuario porteiro = new Usuario("José Porteiro", "porteiro@condogest.com", senhaHash, Usuario.Role.PORTEIRO);
            porteiro.setTelefone("(11) 99999-0003");
            usuarioRepository.save(porteiro);

            System.out.println("✅ Usuários de demonstração criados!");
            System.out.println("   Síndico:  sindico@condogest.com / 123456");
            System.out.println("   Morador:  morador@condogest.com / 123456");
            System.out.println("   Porteiro: porteiro@condogest.com / 123456");
        }
    }
}
