package com.kidcare.acceso_service.service;

import com.kidcare.acceso_service.dto.VerificarAccesoRequestDTO;
import com.kidcare.acceso_service.model.Acceso;
import com.kidcare.acceso_service.model.TokenMedico;
import com.kidcare.acceso_service.repository.AccesoRepository;
import com.kidcare.acceso_service.repository.LogAccesoMedicoRepository;
import com.kidcare.acceso_service.repository.TokenMedicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenMedicoServiceTest {

    @Mock TokenMedicoRepository tokenMedicoRepository;
    @Mock AccesoRepository accesoRepository;
    @Mock LogAccesoMedicoRepository logAccesoMedicoRepository;
    @InjectMocks TokenMedicoService tokenMedicoService;

    private TokenMedico tokenActivo(String latPadre, String lonPadre, int idTutor) {
        Acceso acceso = new Acceso();
        acceso.setIdAcceso(10);
        acceso.setIdMenor(1);
        acceso.setIdUsuario(idTutor);

        TokenMedico token = new TokenMedico();
        token.setToken("tok123");
        token.setEstadoToken("activo");
        token.setFechaCreacion(LocalDateTime.now());
        token.setLatitudPadre(latPadre);
        token.setLongitudPadre(lonPadre);
        token.setNombreMedico("Dr. García");
        token.setAcceso(acceso);
        return token;
    }

    private VerificarAccesoRequestDTO request(String token, String lat, String lon) {
        VerificarAccesoRequestDTO dto = new VerificarAccesoRequestDTO();
        dto.setToken(token);
        dto.setLatitudMedico(lat);
        dto.setLongitudMedico(lon);
        return dto;
    }

    // ─── verificarProximidad ──────────────────────────────────────────────────

    @Test
    void verificarProximidad_medico_misma_ubicacion_retornaTrue() {
        when(tokenMedicoRepository.findByToken("tok123"))
                .thenReturn(Optional.of(tokenActivo("37.0", "-4.0", 99)));

        boolean resultado = tokenMedicoService.verificarProximidad(request("tok123", "37.0", "-4.0"));

        assertThat(resultado).isTrue();
        verify(logAccesoMedicoRepository).save(any());
    }

    @Test
    void verificarProximidad_medico_dentro_de_50_metros_retornaTrue() {
        // 0.0004 grados lat ≈ 44 metros
        when(tokenMedicoRepository.findByToken("tok123"))
                .thenReturn(Optional.of(tokenActivo("37.0000", "-4.0000", 99)));

        boolean resultado = tokenMedicoService.verificarProximidad(request("tok123", "37.0004", "-4.0000"));

        assertThat(resultado).isTrue();
    }

    @Test
    void verificarProximidad_medico_fuera_del_radio_lanzaExcepcion() {
        // 0.005 grados lat ≈ 555 metros
        when(tokenMedicoRepository.findByToken("tok123"))
                .thenReturn(Optional.of(tokenActivo("37.0", "-4.0", 99)));

        assertThatThrownBy(() -> tokenMedicoService.verificarProximidad(request("tok123", "37.005", "-4.0")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("radio");
    }

    @Test
    void verificarProximidad_token_expirado_lanzaExcepcion() {
        TokenMedico expirado = tokenActivo("37.0", "-4.0", 99);
        expirado.setFechaCreacion(LocalDateTime.now().minusMinutes(25));

        when(tokenMedicoRepository.findByToken("tok123")).thenReturn(Optional.of(expirado));

        assertThatThrownBy(() -> tokenMedicoService.verificarProximidad(request("tok123", "37.0", "-4.0")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expirado");
    }

    @Test
    void verificarProximidad_token_revocado_lanzaExcepcion() {
        TokenMedico revocado = tokenActivo("37.0", "-4.0", 99);
        revocado.setEstadoToken("revocado");

        when(tokenMedicoRepository.findByToken("tok123")).thenReturn(Optional.of(revocado));

        assertThatThrownBy(() -> tokenMedicoService.verificarProximidad(request("tok123", "37.0", "-4.0")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("activo");
    }

    @Test
    void verificarProximidad_token_no_existe_lanzaExcepcion() {
        when(tokenMedicoRepository.findByToken("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenMedicoService.verificarProximidad(request("inexistente", "37.0", "-4.0")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    // ─── revocarToken ─────────────────────────────────────────────────────────

    @Test
    void revocarToken_tutor_correcto_cambia_estado_a_revocado() {
        TokenMedico token = tokenActivo("37.0", "-4.0", 99);
        when(tokenMedicoRepository.findByToken("tok123")).thenReturn(Optional.of(token));

        tokenMedicoService.revocarToken("tok123", 99);

        assertThat(token.getEstadoToken()).isEqualTo("revocado");
        verify(tokenMedicoRepository).save(token);
    }

    @Test
    void revocarToken_tutor_incorrecto_lanzaExcepcion() {
        when(tokenMedicoRepository.findByToken("tok123"))
                .thenReturn(Optional.of(tokenActivo("37.0", "-4.0", 99)));

        assertThatThrownBy(() -> tokenMedicoService.revocarToken("tok123", 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("permiso");
    }

    @Test
    void revocarToken_no_existe_lanzaExcepcion() {
        when(tokenMedicoRepository.findByToken("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenMedicoService.revocarToken("ghost", 99))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }
}
