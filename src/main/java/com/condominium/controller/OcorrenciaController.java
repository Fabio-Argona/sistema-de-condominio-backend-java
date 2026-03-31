package com.condominium.controller;

import com.condominium.dto.OcorrenciaDTO;
import com.condominium.model.Ocorrencia;
import com.condominium.model.Usuario;
import com.condominium.repository.OcorrenciaRepository;
import com.condominium.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public OcorrenciaController(OcorrenciaRepository ocorrenciaRepository, UsuarioRepository usuarioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<OcorrenciaDTO> listarTodas() {
        return ocorrenciaRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(OcorrenciaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/morador/{moradorId}")
    public List<OcorrenciaDTO> listarPorMorador(@PathVariable Long moradorId) {
        return ocorrenciaRepository.findByMoradorIdOrderByDataCriacaoDesc(moradorId).stream()
                .map(OcorrenciaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/morador/{moradorId}")
    public ResponseEntity<OcorrenciaDTO> criar(@PathVariable Long moradorId, @RequestBody Ocorrencia ocorrencia) {
        return usuarioRepository.findById(moradorId).map(morador -> {
            ocorrencia.setMorador(morador);
            // Setup defaults incase they are null from JSON payload
            if (ocorrencia.getStatus() == null) ocorrencia.setStatus(Ocorrencia.StatusOcorrencia.ABERTA);
            if (ocorrencia.getPrioridade() == null) ocorrencia.setPrioridade(Ocorrencia.PrioridadeOcorrencia.BAIXA);
            
            Ocorrencia salva = ocorrenciaRepository.save(ocorrencia);
            return ResponseEntity.ok(OcorrenciaDTO.fromEntity(salva));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OcorrenciaDTO> atualizarStatus(@PathVariable Long id, @RequestBody Ocorrencia dadosAtualizacao) {
        return ocorrenciaRepository.findById(id).map(ocorrencia -> {
            if (dadosAtualizacao.getStatus() != null) {
                ocorrencia.setStatus(dadosAtualizacao.getStatus());
            }
            if (dadosAtualizacao.getRespostasSindico() != null && !dadosAtualizacao.getRespostasSindico().isEmpty()) {
                // Adiciona novas respostas presevando as antigas
                ocorrencia.getRespostasSindico().addAll(dadosAtualizacao.getRespostasSindico());
            }
            Ocorrencia salva = ocorrenciaRepository.save(ocorrencia);
            return ResponseEntity.ok(OcorrenciaDTO.fromEntity(salva));
        }).orElse(ResponseEntity.notFound().build());
    }
}
