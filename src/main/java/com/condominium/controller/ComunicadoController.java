package com.condominium.controller;

import com.condominium.dto.ComunicadoDTO;
import com.condominium.model.Comunicado;
import com.condominium.repository.ComunicadoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comunicados")
public class ComunicadoController {

    private final ComunicadoRepository comunicadoRepository;

    public ComunicadoController(ComunicadoRepository comunicadoRepository) {
        this.comunicadoRepository = comunicadoRepository;
    }

    @GetMapping
    public List<ComunicadoDTO> listarTodos() {
        return comunicadoRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(ComunicadoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ComunicadoDTO> criar(@RequestBody Comunicado comunicado) {
        comunicado.setDataCriacao(LocalDateTime.now());
        if (comunicado.getAutor() == null) {
            comunicado.setAutor("Administração"); // Default fallback
        }
        Comunicado salvo = comunicadoRepository.save(comunicado);
        return ResponseEntity.ok(ComunicadoDTO.fromEntity(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoDTO> atualizar(@PathVariable Long id, @RequestBody Comunicado comunicadoAtualizado) {
        return comunicadoRepository.findById(id).map(comunicado -> {
            comunicado.setTitulo(comunicadoAtualizado.getTitulo());
            comunicado.setConteudo(comunicadoAtualizado.getConteudo());
            comunicado.setCategoria(comunicadoAtualizado.getCategoria());
            comunicado.setImportante(comunicadoAtualizado.isImportante());
            if (comunicadoAtualizado.getAutor() != null) {
                comunicado.setAutor(comunicadoAtualizado.getAutor());
            }
            Comunicado salvo = comunicadoRepository.save(comunicado);
            return ResponseEntity.ok(ComunicadoDTO.fromEntity(salvo));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (comunicadoRepository.existsById(id)) {
            comunicadoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
