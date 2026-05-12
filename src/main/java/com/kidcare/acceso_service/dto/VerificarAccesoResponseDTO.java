package com.kidcare.acceso_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class VerificarAccesoResponseDTO {
    private String estado;
    private Integer idMenor;
    private String nombreMedico;
    private String resumen;
    private String tipo;
    private String expiracion;
    // null = el tutor autorizó todas; lista = solo estas observaciones
    private List<String> observacionIds;
}
