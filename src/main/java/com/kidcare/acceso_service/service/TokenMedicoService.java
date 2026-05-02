package com.kidcare.acceso_service.service;

import com.kidcare.acceso_service.dto.TokenMedicoRequestDTO;
import com.kidcare.acceso_service.dto.TokenMedicoResponseDTO;
import com.kidcare.acceso_service.dto.VerificarAccesoRequestDTO;
import com.kidcare.acceso_service.model.Acceso;
import com.kidcare.acceso_service.model.LogAccesoMedico;
import com.kidcare.acceso_service.model.TokenMedico;
import com.kidcare.acceso_service.repository.AccesoRepository;
import com.kidcare.acceso_service.repository.LogAccesoMedicoRepository;
import com.kidcare.acceso_service.repository.TokenMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

// Servicio que maneja la generación y validación de enlaces temporales para médicos
@Service
public class TokenMedicoService {

    @Autowired
    private TokenMedicoRepository tokenMedicoRepository;

    @Autowired
    private AccesoRepository accesoRepository;

    @Autowired
    private LogAccesoMedicoRepository logAccesoMedicoRepository;

    // URL base de la app web donde el médico verá la bitácora
    private static final String URL_BASE = "https://kidcare.vercel.app/medico/";

    // Genera un enlace temporal único para que el médico acceda a la bitácora
    public TokenMedicoResponseDTO generarToken(TokenMedicoRequestDTO dto, Integer idUsuarioTutor) {

        // Busca el acceso del tutor sobre el menor
        List<Acceso> accesos = accesoRepository.findByIdUsuario(idUsuarioTutor);
        Acceso acceso = accesos.stream()
                .filter(a -> a.getIdMenor().equals(dto.getIdMenor()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tienes acceso sobre este menor"));

        // Invalida el token activo anterior si existe
        List<TokenMedico> tokensActivos = tokenMedicoRepository
                .findByAccesoIdAccesoAndEstadoToken(acceso.getIdAcceso(), "activo");
        tokensActivos.forEach(t -> {
            t.setEstadoToken("revocado");
            tokenMedicoRepository.save(t);
        });

        // Genera token único con SecureRandom
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        // Crea el token médico
        TokenMedico tokenMedico = new TokenMedico();
        tokenMedico.setAcceso(acceso);
        tokenMedico.setToken(tokenValue);
        tokenMedico.setUrlAcceso(URL_BASE + tokenValue);
        tokenMedico.setEstadoToken("activo");
        tokenMedico.setLatitudPadre(dto.getLatitudPadre());
        tokenMedico.setLongitudPadre(dto.getLongitudPadre());
        tokenMedico.setNombreMedico(dto.getNombreMedico());
        tokenMedico.setRutMedico(dto.getRutMedico());
        tokenMedicoRepository.save(tokenMedico);

        // Registra el evento de creación en el log
        registrarLog(tokenMedico, "CREACION", null);

        // Retorna el DTO con los datos del enlace
        TokenMedicoResponseDTO response = new TokenMedicoResponseDTO();
        response.setToken(tokenValue);
        response.setUrlAcceso(URL_BASE + tokenValue);
        response.setNombreMedico(dto.getNombreMedico());
        response.setEstadoToken("activo");
        return response;
    }

    // Verifica que el médico esté dentro del radio de 100 metros del tutor
    public boolean verificarProximidad(VerificarAccesoRequestDTO dto) {

        TokenMedico tokenMedico = tokenMedicoRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));

        // Verifica que el token esté activo
        if (!tokenMedico.getEstadoToken().equals("activo")) {
            registrarLog(tokenMedico, "ERROR", null);
            throw new RuntimeException("El enlace no está activo");
        }

        // Calcula la distancia entre el médico y el tutor
        double distancia = calcularDistancia(
                Double.parseDouble(tokenMedico.getLatitudPadre()),
                Double.parseDouble(tokenMedico.getLongitudPadre()),
                Double.parseDouble(dto.getLatitudMedico()),
                Double.parseDouble(dto.getLongitudMedico()));

        if (distancia > 100) {
            registrarLog(tokenMedico, "ERROR", null);
            throw new RuntimeException("El médico no está dentro del radio permitido");
        }

        // Registra el uso exitoso
        registrarLog(tokenMedico, "USO", null);
        return true;
    }

    // Revoca manualmente un token activo
    public void revocarToken(String tokenValue, Integer idUsuarioTutor) {

        TokenMedico tokenMedico = tokenMedicoRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Token no encontrado"));

        // Verifica que el token pertenezca al tutor
        if (!tokenMedico.getAcceso().getIdUsuario().equals(idUsuarioTutor)) {
            throw new RuntimeException("No tienes permiso para revocar este token");
        }

        tokenMedico.setEstadoToken("revocado");
        tokenMedicoRepository.save(tokenMedico);
        registrarLog(tokenMedico, "EXPIRADO", null);
    }

    // Calcula la distancia en metros entre dos coordenadas usando la fórmula de
    // Haversine
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA * c;
    }

    // Registra un evento en el log de acceso médico
    private void registrarLog(TokenMedico tokenMedico, String tipoEvento, String ip) {
        LogAccesoMedico log = new LogAccesoMedico();
        log.setTokenMedico(tokenMedico);
        log.setFechaEvento(LocalDate.now());
        log.setTipoEvento(tipoEvento);
        log.setIpOpcional(ip);
        logAccesoMedicoRepository.save(log);
    }
}