package com.kidcare.acceso_service.service;

import com.kidcare.acceso_service.dto.AccesoRequestDTO;
import com.kidcare.acceso_service.model.Acceso;
import com.kidcare.acceso_service.model.Delegado;
import com.kidcare.acceso_service.repository.AccesoRepository;
import com.kidcare.acceso_service.repository.DelegadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock AccesoRepository accesoRepository;
    @Mock DelegadoRepository delegadoRepository;
    @InjectMocks AccesoService accesoService;

    // ─── crearAcceso ──────────────────────────────────────────────────────────

    @Test
    void crearAcceso_persiste_acceso_y_delegado() {
        AccesoRequestDTO dto = new AccesoRequestDTO();
        dto.setIdMenor(1);
        dto.setIdUsuarioDelegado(5);
        dto.setFechaExpiracion(LocalDate.now().plusMonths(3));

        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> {
            Acceso a = inv.getArgument(0);
            a.setIdAcceso(10);
            return a;
        });
        when(delegadoRepository.save(any(Delegado.class))).thenAnswer(inv -> inv.getArgument(0));

        Acceso resultado = accesoService.crearAcceso(dto, 99);

        assertThat(resultado.getIdMenor()).isEqualTo(1);
        assertThat(resultado.getIdUsuario()).isEqualTo(99);
        assertThat(resultado.getFechaCreacion()).isEqualTo(LocalDate.now());
        verify(delegadoRepository).save(any(Delegado.class));
    }

    // ─── revocarAcceso ────────────────────────────────────────────────────────

    @Test
    void revocarAcceso_tutor_correcto_elimina_acceso() {
        Acceso acceso = new Acceso();
        acceso.setIdAcceso(10);
        acceso.setIdUsuario(99);

        when(accesoRepository.findById(10)).thenReturn(Optional.of(acceso));
        when(delegadoRepository.findByAccesoIdAcceso(10)).thenReturn(List.of());

        accesoService.revocarAcceso(10, 99);

        verify(accesoRepository).delete(acceso);
    }

    @Test
    void revocarAcceso_tutor_incorrecto_lanzaExcepcion() {
        Acceso acceso = new Acceso();
        acceso.setIdAcceso(10);
        acceso.setIdUsuario(99);

        when(accesoRepository.findById(10)).thenReturn(Optional.of(acceso));

        assertThatThrownBy(() -> accesoService.revocarAcceso(10, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    void revocarAcceso_no_encontrado_lanzaExcepcion() {
        when(accesoRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accesoService.revocarAcceso(999, 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ─── obtenerAccesosPorTutor ───────────────────────────────────────────────

    @Test
    void obtenerAccesosPorTutor_retorna_lista_del_tutor() {
        Acceso a1 = new Acceso();
        a1.setIdUsuario(99);
        a1.setIdMenor(1);

        Acceso a2 = new Acceso();
        a2.setIdUsuario(99);
        a2.setIdMenor(2);

        when(accesoRepository.findByIdUsuario(99)).thenReturn(List.of(a1, a2));

        List<Acceso> resultado = accesoService.obtenerAccesosPorTutor(99);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getIdMenor()).isEqualTo(1);
        assertThat(resultado.get(1).getIdMenor()).isEqualTo(2);
    }

    @Test
    void obtenerAccesosPorTutor_sin_accesos_retorna_lista_vacia() {
        when(accesoRepository.findByIdUsuario(1)).thenReturn(List.of());

        List<Acceso> resultado = accesoService.obtenerAccesosPorTutor(1);

        assertThat(resultado).isEmpty();
    }
}
