package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.LogAccesoMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorio que maneja el acceso a datos de la entidad LogAccesoMedico
@Repository
public interface LogAccesoMedicoRepository extends JpaRepository<LogAccesoMedico, Integer> {

    @Query("SELECT l FROM LogAccesoMedico l WHERE l.tokenMedico.idToken = :idToken")
    List<LogAccesoMedico> findByTokenMedicoIdToken(@Param("idToken") Integer idToken);
}