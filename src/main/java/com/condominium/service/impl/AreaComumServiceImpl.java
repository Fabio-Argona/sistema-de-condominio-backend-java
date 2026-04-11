package com.condominium.service.impl;

import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.AreaComum;
import com.condominium.repository.AreaComumRepository;
import com.condominium.service.IAreaComumService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaComumServiceImpl implements IAreaComumService {

    private final AreaComumRepository areaComumRepository;

    public AreaComumServiceImpl(AreaComumRepository areaComumRepository) {
        this.areaComumRepository = areaComumRepository;
    }

    @Override
    public List<AreaComum> listarTodas() {
        return areaComumRepository.findAll();
    }

    @Override
    public AreaComum criar(AreaComum area) {
        return areaComumRepository.save(area);
    }

    @Override
    public AreaComum atualizar(Long id, AreaComum areaDados) {
        var area = areaComumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área Comum", id));
        area.setNome(areaDados.getNome());
        area.setDescricao(areaDados.getDescricao());
        area.setCapacidade(areaDados.getCapacidade());
        area.setValorReserva(areaDados.getValorReserva());
        area.setHorarioAbertura(areaDados.getHorarioAbertura());
        area.setHorarioFechamento(areaDados.getHorarioFechamento());
        area.setDisponivel(areaDados.getDisponivel());
        area.setRegras(areaDados.getRegras());
        return areaComumRepository.save(area);
    }

    @Override
    public void deletar(Long id) {
        if (!areaComumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Área Comum", id);
        }
        areaComumRepository.deleteById(id);
    }
}
