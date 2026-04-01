package com.condominium.controller;

import com.condominium.model.AreaComum;
import com.condominium.repository.AreaComumRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-comuns")
public class AreaComumController {

    private final AreaComumRepository areaComumRepository;

    public AreaComumController(AreaComumRepository areaComumRepository) {
        this.areaComumRepository = areaComumRepository;
    }

    @GetMapping
    public List<AreaComum> listarTodas() {
        return areaComumRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<AreaComum> criar(@RequestBody AreaComum area) {
        AreaComum salva = areaComumRepository.save(area);
        return ResponseEntity.ok(salva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaComum> atualizar(@PathVariable Long id, @RequestBody AreaComum areaDados) {
        return areaComumRepository.findById(id).map(area -> {
            area.setNome(areaDados.getNome());
            area.setDescricao(areaDados.getDescricao());
            area.setCapacidade(areaDados.getCapacidade());
            area.setValorReserva(areaDados.getValorReserva());
            area.setHorarioAbertura(areaDados.getHorarioAbertura());
            area.setHorarioFechamento(areaDados.getHorarioFechamento());
            area.setDisponivel(areaDados.getDisponivel());
            area.setRegras(areaDados.getRegras());
            
            AreaComum salva = areaComumRepository.save(area);
            return ResponseEntity.ok(salva);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (areaComumRepository.existsById(id)) {
            areaComumRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
