package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.Score;

import java.util.List;
import java.util.Set;

public interface ScoreService {
    List<Score> getScoreGame(JuegoId juegoId);
}
