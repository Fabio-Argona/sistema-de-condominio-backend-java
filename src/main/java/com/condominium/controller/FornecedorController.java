package com.condominium.controller;

import com.condominium.dto.FornecedorDTO;
import com.condominium.model.Fornecedor;
import com.condominium.repository.FornecedorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorController(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @GetMapping
    public List<FornecedorDTO> listarTodos() {
        return fornecedorRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(FornecedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/morador/{moradorId}")
    public List<FornecedorDTO> listarPorMorador(@PathVariable Long moradorId) {
        return fornecedorRepository.findByMoradorIdOrderByNomeAsc(moradorId)
                .stream()
                .map(FornecedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<FornecedorDTO> criar(@RequestBody Fornecedor fornecedor) {
        Fornecedor salvo = fornecedorRepository.save(fornecedor);
        return ResponseEntity.ok(FornecedorDTO.fromEntity(salvo));
    }

    @PostMapping("/morador/{moradorId}")
    public ResponseEntity<FornecedorDTO> criarPorMorador(@PathVariable Long moradorId, @RequestBody Fornecedor fornecedor) {
        fornecedor.setMoradorId(moradorId);
        Fornecedor salvo = fornecedorRepository.save(fornecedor);
        return ResponseEntity.ok(FornecedorDTO.fromEntity(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorDTO> atualizar(@PathVariable Long id, @RequestBody Fornecedor dados) {
        return fornecedorRepository.findById(id).map(f -> {
            f.setNome(dados.getNome());
            f.setComentario(dados.getComentario());
            f.setVigencia(dados.getVigencia());
            f.setContato(dados.getContato());
            f.setValor(dados.getValor());
            return ResponseEntity.ok(FornecedorDTO.fromEntity(fornecedorRepository.save(f)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/morador/{moradorId}/{id}")
    public ResponseEntity<FornecedorDTO> atualizarPorMorador(@PathVariable Long moradorId, @PathVariable Long id, @RequestBody Fornecedor dados) {
        return fornecedorRepository.findById(id).map(f -> {
            if (!moradorId.equals(f.getMoradorId())) {
                return ResponseEntity.status(403).<FornecedorDTO>build();
            }
            f.setNome(dados.getNome());
            f.setComentario(dados.getComentario());
            f.setVigencia(dados.getVigencia());
            f.setContato(dados.getContato());
            f.setValor(dados.getValor());
            return ResponseEntity.ok(FornecedorDTO.fromEntity(fornecedorRepository.save(f)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!fornecedorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fornecedorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/morador/{moradorId}/{id}")
    public ResponseEntity<Void> deletarPorMorador(@PathVariable Long moradorId, @PathVariable Long id) {
        return fornecedorRepository.findById(id).map(f -> {
            if (!moradorId.equals(f.getMoradorId())) {
                return ResponseEntity.status(403).<Void>build();
            }
            fornecedorRepository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
