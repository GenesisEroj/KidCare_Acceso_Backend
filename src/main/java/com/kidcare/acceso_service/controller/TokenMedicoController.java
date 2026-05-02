package com.kidcare.acceso_service.controller;

import com.kidcare.acceso_service.dto.TokenMedicoRequestDTO;
import com.kidcare.acceso_service.dto.TokenMedicoResponseDTO;
import com.kidcare.acceso_service.dto.VerificarAccesoRequestDTO;
import com.kidcare.acceso_service.service.TokenMedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Controlador que expone los endpoints de gestión de tokens médicos
@RestController
@RequestMapping("/api/acceso/medico")
public class TokenMedicoController {

    @Autowired
    private TokenMedicoService tokenMedicoService;

    // POST /api/acceso/medico/generar — genera un enlace temporal para el médico
    @PostMapping("/generar")
    public ResponseEntity<TokenMedicoResponseDTO> generar(@Valid @RequestBody TokenMedicoRequestDTO dto,
            Authentication authentication) {
        Integer idTutor = Integer.parseInt(authentication.getName());
        return ResponseEntity.ok(tokenMedicoService.generarToken(dto, idTutor));
    }

    // POST /api/acceso/medico/verificar — verifica proximidad geográfica del médico
    @PostMapping("/verificar")
    public ResponseEntity<String> verificar(@Valid @RequestBody VerificarAccesoRequestDTO dto) {
        tokenMedicoService.verificarProximidad(dto);
        return ResponseEntity.ok("Acceso verificado correctamente");
    }

    // DELETE /api/acceso/medico/revocar/{token} — revoca manualmente un token
    // activo
    @DeleteMapping("/revocar/{token}")
    public ResponseEntity<String> revocar(@PathVariable String token,
            Authentication authentication) {
        Integer idTutor = Integer.parseInt(authentication.getName());
        tokenMedicoService.revocarToken(token, idTutor);
        return ResponseEntity.ok("Token revocado correctamente");
    }
}