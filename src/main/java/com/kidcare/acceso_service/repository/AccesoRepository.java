package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repositorio que maneja el acceso a datos de la entidad Acceso
@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Integer> {

    // Obtiene todos los accesos de un tutor sobre sus menores
    List<Acceso> findByIdUsuario(Integer idUsuario);

    // Obtiene todos los accesos sobre un menor específico
    List<Acceso> findByIdMenor(Integer idMenor);
}