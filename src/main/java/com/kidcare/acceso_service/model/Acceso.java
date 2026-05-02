package com.kidcare.acceso_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// Entidad que representa el permiso otorgado por un tutor sobre un menor
@Data
@Entity
@Table(name = "ACCESO")
public class Acceso {

    // Identificador único del acceso
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Integer idAcceso;

    // Referencia lógica al menor en db_users
    @Column(name = "id_menor", nullable = false)
    private Integer idMenor;

    // Referencia lógica al tutor en db_users
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    // Fecha en que se otorgó el acceso
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    // Fecha de expiración. NULL indica acceso indefinido
    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;
}