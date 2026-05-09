package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.TokenMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad TokenMedico
@Repository
public interface TokenMedicoRepository extends JpaRepository<TokenMedico, Integer> {

    Optional<TokenMedico> findByToken(String token);

    @Query("SELECT t FROM TokenMedico t WHERE t.acceso.idAcceso = :idAcceso AND t.estadoToken = :estadoToken")
    List<TokenMedico> findByAccesoIdAccesoAndEstadoToken(@Param("idAcceso") Integer idAcceso, @Param("estadoToken") String estadoToken);
}