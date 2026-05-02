package com.kidcare.acceso_service.controller;

import com.kidcare.acceso_service.dto.AccesoRequestDTO;
import com.kidcare.acceso_service.model.Acceso;
import com.kidcare.acceso_service.service.AccesoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador que expone los endpoints de gestión de accesos de delegados
@RestController
@RequestMapping("/api/acceso")
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    // POST /api/acceso — crea un acceso de delegado sobre un menor
    @PostMapping
    public ResponseEntity<Acceso> crear(@Valid @RequestBody AccesoRequestDTO dto,
            Authentication authentication) {
        Integer idTutor = Integer.parseInt(authentication.getName());
        return ResponseEntity.ok(accesoService.crearAcceso(dto, idTutor));
    }

    // DELETE /api/acceso/{id} — revoca el acceso de un delegado
    @DeleteMapping("/{id}")
    public ResponseEntity<String> revocar(@PathVariable Integer id,
            Authentication authentication) {
        Integer idTutor = Integer.parseInt(authentication.getName());
        accesoService.revocarAcceso(id, idTutor);
        return ResponseEntity.ok("Acceso revocado correctamente");
    }

    // GET /api/acceso — obtiene todos los accesos del tutor autenticado
    @GetMapping
    public ResponseEntity<List<Acceso>> listar(Authentication authentication) {
        Integer idTutor = Integer.parseInt(authentication.getName());
        return ResponseEntity.ok(accesoService.obtenerAccesosPorTutor(idTutor));
    }
}