package com.kidcare.acceso_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO que recibe los datos para generar un enlace temporal para el médico
@Data
public class TokenMedicoRequestDTO {

    // ID del menor cuya bitácora se compartirá
    @NotNull(message = "El id del menor es obligatorio")
    private Integer idMenor;

    // Nombre del médico destinatario (obligatorio)
    @NotBlank(message = "El nombre del médico es obligatorio")
    private String nombreMedico;

    // RUT del médico (opcional)
    private String rutMedico;

    // Latitud del tutor al momento de generar el enlace
    @NotBlank(message = "La latitud es obligatoria")
    private String latitudPadre;

    // Longitud del tutor al momento de generar el enlace
    @NotBlank(message = "La longitud es obligatoria")
    private String longitudPadre;
}