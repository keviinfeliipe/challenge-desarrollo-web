package co.com.sofka.cargame;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.cargame.domain.juego.command.CrearJuegoCommand;
import co.com.sofka.cargame.domain.juego.command.InicarJuegoCommand;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.infra.services.*;
import co.com.sofka.cargame.usecase.CrearJuegoUseCase;
import co.com.sofka.cargame.usecase.InicarJuegoUseCase;
import co.com.sofka.cargame.usecase.model.Score;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.asyn.SubscriberEvent;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class JuegoController {

    private SubscriberEvent subscriberEvent;
    private EventStoreRepository eventStoreRepository;
    private CrearJuegoUseCase crearJuegoUseCase;
    private InicarJuegoUseCase inicarJuegoUseCase;
    private ScoreQueryService scoreQueryService;

    public JuegoController(SubscriberEvent subscriberEvent, EventStoreRepository eventStoreRepository, CrearJuegoUseCase crearJuegoUseCase, InicarJuegoUseCase inicarJuegoUseCase, ScoreQueryService scoreQueryService) {
        this.subscriberEvent = subscriberEvent;
        this.eventStoreRepository = eventStoreRepository;
        this.crearJuegoUseCase = crearJuegoUseCase;
        this.inicarJuegoUseCase = inicarJuegoUseCase;
        this.scoreQueryService = scoreQueryService;
    }

    @PostMapping("/crearJuego")
    public String crearJuego(@RequestBody CrearJuegoCommand command) {
        crearJuegoUseCase.addRepository(domainEventRepository());
        UseCaseHandler.getInstance()
                .asyncExecutor(crearJuegoUseCase, new RequestCommand<>(command))
                .subscribe(subscriberEvent);
        return command.getJuegoId();
    }

    @PostMapping("/iniciarJuego")
    public String iniciarJuego(@RequestBody InicarJuegoCommand command) {
        inicarJuegoUseCase.addRepository(domainEventRepository());
        UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getJuegoId())
                .asyncExecutor(inicarJuegoUseCase, new RequestCommand<>(command))
                .subscribe(subscriberEvent);
        return command.getJuegoId();
    }

    @GetMapping("/score/{id}")
    public List<Score> obtenerScore(@PathVariable String id){
        return scoreQueryService
                .getScoreGame(JuegoId.of(id))
                .stream()
                .sorted(Comparator.comparing(Score::getTiempoRecorrido))
                .collect(Collectors.toList());
    }

    private DomainEventRepository domainEventRepository() {
        return new DomainEventRepository() {
            @Override
            public List<DomainEvent> getEventsBy(String aggregateId) {
                return eventStoreRepository.getEventsBy("juego", aggregateId);
            }

            @Override
            public List<DomainEvent> getEventsBy(String aggregateName, String aggregateRootId) {
                return eventStoreRepository.getEventsBy(aggregateName, aggregateRootId);
            }
        };
    }
}
