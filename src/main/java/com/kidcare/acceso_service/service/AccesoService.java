package com.kidcare.acceso_service.service;

import com.kidcare.acceso_service.dto.AccesoRequestDTO;
import com.kidcare.acceso_service.model.Acceso;
import com.kidcare.acceso_service.model.Delegado;
import com.kidcare.acceso_service.repository.AccesoRepository;
import com.kidcare.acceso_service.repository.DelegadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Servicio que maneja la gestión de accesos de delegados
@Service
public class AccesoService {

    @Autowired
    private AccesoRepository accesoRepository;

    @Autowired
    private DelegadoRepository delegadoRepository;

    // Crea un acceso de delegado sobre un menor
    public Acceso crearAcceso(AccesoRequestDTO dto, Integer idUsuarioTutor) {

        // Crea el registro de acceso
        Acceso acceso = new Acceso();
        acceso.setIdMenor(dto.getIdMenor());
        acceso.setIdUsuario(idUsuarioTutor);
        acceso.setFechaCreacion(LocalDate.now());
        acceso.setFechaExpiracion(dto.getFechaExpiracion());
        accesoRepository.save(acceso);

        // Vincula el delegado al acceso
        Delegado delegado = new Delegado();
        delegado.setAcceso(acceso);
        delegado.setIdUsuarioDelegado(dto.getIdUsuarioDelegado());
        delegadoRepository.save(delegado);

        return acceso;
    }

    // Revoca el acceso de un delegado eliminando el registro
    public void revocarAcceso(Integer idAcceso, Integer idUsuarioTutor) {

        Acceso acceso = accesoRepository.findById(idAcceso)
                .orElseThrow(() -> new RuntimeException("Acceso no encontrado"));

        // Verifica que el acceso pertenezca al tutor
        if (!acceso.getIdUsuario().equals(idUsuarioTutor)) {
            throw new RuntimeException("No tienes permiso para revocar este acceso");
        }

        // Elimina los delegados vinculados y luego el acceso
        List<Delegado> delegados = delegadoRepository.findByAccesoIdAcceso(idAcceso);
        delegadoRepository.deleteAll(delegados);
        accesoRepository.delete(acceso);
    }

    // Obtiene todos los accesos de un tutor
    public List<Acceso> obtenerAccesosPorTutor(Integer idUsuarioTutor) {
        return accesoRepository.findByIdUsuario(idUsuarioTutor);
    }
}