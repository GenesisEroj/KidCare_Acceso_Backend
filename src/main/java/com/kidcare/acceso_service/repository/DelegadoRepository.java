package com.kidcare.acceso_service.repository;

import com.kidcare.acceso_service.model.Delegado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repositorio que maneja el acceso a datos de la entidad Delegado
@Repository
public interface DelegadoRepository extends JpaRepository<Delegado, Integer> {

    @Query("SELECT d FROM Delegado d WHERE d.acceso.idAcceso = :idAcceso")
    List<Delegado> findByAccesoIdAcceso(@Param("idAcceso") Integer idAcceso);

    Optional<Delegado> findByIdUsuarioDelegado(Integer idUsuarioDelegado);
}