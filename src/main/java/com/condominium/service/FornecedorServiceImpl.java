package com.condominium.service;

import com.condominium.dto.FornecedorDTO;
import com.condominium.exception.ForbiddenException;
import com.condominium.exception.ResourceNotFoundException;
import com.condominium.model.Fornecedor;
import com.condominium.repository.FornecedorRepository;
import com.condominium.service.impl.IFornecedorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorServiceImpl implements IFornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorServiceImpl(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public List<FornecedorDTO> listarTodos() {
        return fornecedorRepository.findAllByOrderByNomeAsc().stream()
                .map(FornecedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<FornecedorDTO> listarPorMorador(Long moradorId) {
        return fornecedorRepository.findByMoradorIdOrderByNomeAsc(moradorId).stream()
                .map(FornecedorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FornecedorDTO criar(Fornecedor fornecedor) {
        return FornecedorDTO.fromEntity(fornecedorRepository.save(fornecedor));
    }

    @Override
    public FornecedorDTO criarParaMorador(Long moradorId, Fornecedor fornecedor) {
        fornecedor.setMoradorId(moradorId);
        return FornecedorDTO.fromEntity(fornecedorRepository.save(fornecedor));
    }

    @Override
    public FornecedorDTO atualizar(Long id, Fornecedor dados) {
        var fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
        applyUpdates(fornecedor, dados);
        return FornecedorDTO.fromEntity(fornecedorRepository.save(fornecedor));
    }

    @Override
    public FornecedorDTO atualizarParaMorador(Long moradorId, Long id, Fornecedor dados) {
        var fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
        if (!moradorId.equals(fornecedor.getMoradorId())) {
            throw new ForbiddenException("Você não tem permissão para editar este fornecedor.");
        }
        applyUpdates(fornecedor, dados);
        return FornecedorDTO.fromEntity(fornecedorRepository.save(fornecedor));
    }

    @Override
    public void deletar(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fornecedor", id);
        }
        fornecedorRepository.deleteById(id);
    }

    @Override
    public void deletarParaMorador(Long moradorId, Long id) {
        var fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
        if (!moradorId.equals(fornecedor.getMoradorId())) {
            throw new ForbiddenException("Você não tem permissão para excluir este fornecedor.");
        }
        fornecedorRepository.deleteById(id);
    }

    private void applyUpdates(Fornecedor target, Fornecedor source) {
        target.setNome(source.getNome());
        target.setComentario(source.getComentario());
        target.setVigencia(source.getVigencia());
        target.setContato(source.getContato());
        target.setValor(source.getValor());
    }
}
