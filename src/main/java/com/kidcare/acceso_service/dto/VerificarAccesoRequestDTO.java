package com.kidcare.acceso_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO que recibe la ubicación del médico para verificar proximidad geográfica
@Data
public class VerificarAccesoRequestDTO {

    // Token del enlace temporal
    @NotBlank(message = "El token es obligatorio")
    private String token;

    // Latitud del médico en tiempo real
    @NotBlank(message = "La latitud es obligatoria")
    private String latitudMedico;

    // Longitud del médico en tiempo real
    @NotBlank(message = "La longitud es obligatoria")
    private String longitudMedico;
}