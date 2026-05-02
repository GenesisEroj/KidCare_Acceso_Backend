package com.kidcare.acceso_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// Entidad que registra los eventos del ciclo de vida de un token médico
@Data
@Entity
@Table(name = "LOG_ACCESO_MEDICO")
public class LogAccesoMedico {

    // Identificador único del registro
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer idLog;

    // Token al que corresponde el evento
    @ManyToOne
    @JoinColumn(name = "id_token", nullable = false)
    private TokenMedico tokenMedico;

    // Fecha y hora del evento registrado
    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    // Tipo de evento: CREACION, USO, ERROR, EXPIRADO
    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    // Dirección IP del dispositivo que generó el evento (opcional)
    @Column(name = "ip_opcional")
    private String ipOpcional;
}