package com.condominium.service;

import com.condominium.model.LogEmail;
import com.condominium.model.LogEmail.TipoEmail;
import com.condominium.repository.LogEmailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LogEmailService {

    private final LogEmailRepository logEmailRepository;

    public LogEmailService(LogEmailRepository logEmailRepository) {
        this.logEmailRepository = logEmailRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(TipoEmail tipo, String destinatario, String destinatarioNome,
                          Long boletoId, String descricao) {
        logEmailRepository.save(new LogEmail(
                tipo,
                destinatario,
                destinatarioNome,
                LocalDateTime.now(),
                boletoId,
                descricao
        ));
    }
}