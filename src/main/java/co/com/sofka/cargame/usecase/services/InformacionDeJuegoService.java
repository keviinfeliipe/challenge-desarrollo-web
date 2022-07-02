package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.InformacionDeJuego;

import java.util.List;

public interface InformacionDeJuegoService {
    List<InformacionDeJuego> obtenerInformacionDeJuego(JuegoId juegoId);
}
