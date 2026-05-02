package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.Delegado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad Delegado
@Repository
public interface DelegadoRepository extends JpaRepository<Delegado, Integer> {

    // Obtiene todos los delegados de un acceso
    List<Delegado> findByAccesoIdAcceso(Integer idAcceso);

    // Busca un delegado por su id de usuario
    Optional<Delegado> findByIdUsuarioDelegado(Integer idUsuarioDelegado);
}