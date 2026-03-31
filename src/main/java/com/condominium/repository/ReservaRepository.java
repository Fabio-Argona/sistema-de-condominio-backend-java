package com.condominium.repository;

import com.condominium.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findAllByOrderByDataReservaDescHoraInicioDesc();
    List<Reserva> findByMoradorIdOrderByDataReservaDescHoraInicioDesc(Long moradorId);
}
