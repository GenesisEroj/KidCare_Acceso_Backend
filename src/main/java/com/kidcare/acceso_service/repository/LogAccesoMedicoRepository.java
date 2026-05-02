package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.LogAccesoMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorio que maneja el acceso a datos de la entidad LogAccesoMedico
@Repository
public interface LogAccesoMedicoRepository extends JpaRepository<LogAccesoMedico, Integer> {

    // Obtiene todos los logs de un token médico
    List<LogAccesoMedico> findByTokenMedicoIdToken(Integer idToken);
}