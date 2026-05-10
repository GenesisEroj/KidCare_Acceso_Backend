package com.kidcare.acceso_service.dto;

import lombok.Data;

@Data
public class VerificarAccesoResponseDTO {
    private String estado;
    private Integer idMenor;
    private String nombreMedico;
    private String resumen;
    private String tipo;
}
