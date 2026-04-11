package com.condominium.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionClassesTest {

    @Test
    void resourceNotFoundException_withResourceAndId() {
        var ex = new ResourceNotFoundException("Boleto", 5L);
        assertThat(ex.getMessage()).contains("Boleto").contains("5");
    }

    @Test
    void resourceNotFoundException_withMessage() {
        var ex = new ResourceNotFoundException("mensagem direta");
        assertThat(ex.getMessage()).isEqualTo("mensagem direta");
    }

    @Test
    void businessException_storesMessage() {
        var ex = new BusinessException("negócio inválido");
        assertThat(ex.getMessage()).isEqualTo("negócio inválido");
    }

    @Test
    void forbiddenException_storesMessage() {
        var ex = new ForbiddenException("sem permissão");
        assertThat(ex.getMessage()).isEqualTo("sem permissão");
    }
}
