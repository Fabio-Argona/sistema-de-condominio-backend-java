package com.condominium.controller;

import com.condominium.dto.AreaComumDTO;
import com.condominium.model.AreaComum;
import com.condominium.repository.AreaComumRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/areas-comuns")
public class AreaComumController {

    private final AreaComumRepository areaComumRepository;

    public AreaComumController(AreaComumRepository areaComumRepository) {
        this.areaComumRepository = areaComumRepository;
    }

    @GetMapping
    public List<AreaComumDTO> listarTodas() {
        return areaComumRepository.findAll().stream()
                .map(AreaComumDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<AreaComumDTO> criar(@RequestBody AreaComum areaComum) {
        AreaComum salva = areaComumRepository.save(areaComum);
        return ResponseEntity.ok(AreaComumDTO.fromEntity(salva));
    }
}
