package com.condominium.repository;

import com.condominium.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findAllByOrderByDataReservaDescHoraInicioDesc();
    List<Reserva> findByMoradorIdOrderByDataReservaDescHoraInicioDesc(Long moradorId);
    void deleteByMoradorId(Long moradorId);

    @Query("SELECT r FROM Reserva r WHERE r.areaComum.id = :areaId " +
           "AND r.dataReserva = :data " +
           "AND r.status IN (com.condominium.model.Reserva.StatusReserva.PENDENTE, com.condominium.model.Reserva.StatusReserva.APROVADA) " +
           "AND (r.horaInicio < :fim AND r.horaFim > :inicio)")
    List<Reserva> findSobreposicoes(@Param("areaId") Long areaId, 
                                   @Param("data") LocalDate data, 
                                   @Param("inicio") String inicio, 
                                   @Param("fim") String fim);
}
