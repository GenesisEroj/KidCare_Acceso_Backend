package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.TokenMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad TokenMedico
@Repository
public interface TokenMedicoRepository extends JpaRepository<TokenMedico, Integer> {

    // Busca un token por su valor único
    Optional<TokenMedico> findByToken(String token);

    // Obtiene todos los tokens activos de un acceso
    List<TokenMedico> findByAccesoIdAccesoAndEstadoToken(Integer idAcceso, String estadoToken);
}