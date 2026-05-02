package com.kidcare.acceso_service.model;

import jakarta.persistence.*;
import lombok.Data;

// Entidad que vincula un acceso con un usuario delegado registrado
@Data
@Entity
@Table(name = "DELEGADO")
public class Delegado {

    // Identificador único del delegado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_delegado")
    private Integer idDelegado;

    // Acceso al que pertenece este delegado
    @ManyToOne
    @JoinColumn(name = "id_acceso", nullable = false)
    private Acceso acceso;

    // Referencia lógica al usuario delegado en db_users
    @Column(name = "id_usuario_delegado", nullable = false)
    private Integer idUsuarioDelegado;
}