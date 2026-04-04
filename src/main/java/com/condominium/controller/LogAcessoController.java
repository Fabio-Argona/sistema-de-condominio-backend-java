package com.condominium.controller;

import com.condominium.model.LogAcesso;
import com.condominium.repository.LogAcessoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/log-acessos")
@CrossOrigin(origins = "*")
public class LogAcessoController {

    private final LogAcessoRepository logAcessoRepository;

    public LogAcessoController(LogAcessoRepository logAcessoRepository) {
        this.logAcessoRepository = logAcessoRepository;
    }

    @GetMapping
    public ResponseEntity<List<LogAcesso>> listarTodos() {
        return ResponseEntity.ok(logAcessoRepository.findAllByOrderByDataHoraDesc());
    }
}
