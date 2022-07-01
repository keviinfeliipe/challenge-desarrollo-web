package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.domain.carro.values.CarroId;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.CarroKilometraje;
import co.com.sofka.cargame.usecase.model.Score;
import co.com.sofka.cargame.usecase.services.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoreQueryService implements ScoreService {

    private final MongoTemplate mongoTemplate;
    private final CarroKilometrajeQueryService carroKilometrajeQueryService;
    private final InformacionDeJuegoQueryService idQueryService;
    private final CarroQueryService carroQueryService;


    @Autowired
    public ScoreQueryService(MongoTemplate mongoTemplate, CarroKilometrajeQueryService carroKilometrajeQueryService, InformacionDeJuegoQueryService idQueryService, CarroQueryService carroQueryService) {
        this.mongoTemplate = mongoTemplate;
        this.carroKilometrajeQueryService = carroKilometrajeQueryService;
        this.idQueryService = idQueryService;
        this.carroQueryService = carroQueryService;
    }

    @Override
    public List<Score> getScoreGame() {
        List<Score> scores = new ArrayList<>();
        idQueryService
                .obtenerId()
                .forEach(id -> {
                    var carroId = CarroId.of(id.getCarroId());
                    var juegoId = JuegoId.of(id.getJuegoId());
                    var carrilId = CarrilId.of(id.getCarrilId());
                    var fechaInicio = carroKilometrajeQueryService
                            .getCarroKilometraje(juegoId,carrilId)
                            .stream()
                            .findFirst()
                            .get()
                            .getWhen();
                    var fechaFin = carroKilometrajeQueryService
                            .getCarroKilometraje(juegoId,carrilId)
                            .stream().sorted(Comparator.comparing(CarroKilometraje::getWhen).reversed())
                            .findFirst()
                            .get()
                            .getWhen();
                    var nombre = carroQueryService.getNombreConductorPorId(carroId);
                    var score = new Score(id.getJuegoId(),id.getCarrilId(),id.getCarroId(),nombre,fechaInicio,fechaFin, id.getMetros());
                    scores.add(score);
                });

        return scores;
    }

}
