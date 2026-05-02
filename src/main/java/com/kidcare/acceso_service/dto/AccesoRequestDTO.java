package com.kidcare.acceso_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

// DTO que recibe los datos para crear un acceso de delegado
@Data
public class AccesoRequestDTO {

    // ID del menor sobre el que se otorga el acceso
    @NotNull(message = "El id del menor es obligatorio")
    private Integer idMenor;

    // ID del usuario delegado
    @NotNull(message = "El id del delegado es obligatorio")
    private Integer idUsuarioDelegado;

    // Fecha de expiración del acceso (null = indefinido)
    private LocalDate fechaExpiracion;
}