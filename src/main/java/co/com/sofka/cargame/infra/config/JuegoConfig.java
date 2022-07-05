package co.com.sofka.cargame.infra.config;

import co.com.sofka.business.generic.ServiceBuilder;
import co.com.sofka.business.generic.UseCase;
import co.com.sofka.cargame.infra.services.CarrilCarroQueryService;
import co.com.sofka.cargame.infra.services.CarroQueryService;
import co.com.sofka.cargame.infra.services.JuegoQueryService;
import co.com.sofka.cargame.infra.services.MoverCarroCommandService;
import co.com.sofka.cargame.usecase.listeners.*;
import co.com.sofka.infraestructure.asyn.SubscriberEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.net.URI;
import java.util.Set;

@Configuration
public class JuegoConfig {
    public static final String EXCHANGE = "gameextraction";

    @Bean
    public SubscriberEvent subscriberEvent(EventStoreRepository eventStoreRepository, EventBus eventBus) {
        return new SubscriberEvent(eventStoreRepository, eventBus);
    }

    @Bean
    public ServiceBuilder serviceBuilder(
            CarrilCarroQueryService carrilCarroService,
            CarroQueryService carroQueryService,
            JuegoQueryService juegoQueryService,
            MoverCarroCommandService moverCarroCommandService
    ) {
        ServiceBuilder serviceBuilder = new ServiceBuilder();
        serviceBuilder.addService(carrilCarroService);
        serviceBuilder.addService(carroQueryService);
        serviceBuilder.addService(juegoQueryService);
        serviceBuilder.addService(moverCarroCommandService);
        return serviceBuilder;
    }

    @Bean
    public Set<UseCase.UseCaseWrap> listUseCasesForListener(
            AsinarAPodioUseCase asinarAPodioUseCase,
            CrearCarrilUseCase crearCarrilUseCase,
            CrearCarroUseCase crearCarroUseCase,
            MotorJuegoUseCase motorJuegoUseCase,
            MoverCarroEnCarrilUseCase moverCarroEnCarrilUseCase,
            NotificarGanadoresUseCase notificarGanadoresUseCase
    ) {
        return Set.of(
                new UseCase.UseCaseWrap("carril.CarroFinalizoSuRecorrido", (UseCase) asinarAPodioUseCase),
                new UseCase.UseCaseWrap("carro.CarroCreado", (UseCase) crearCarrilUseCase),
                new UseCase.UseCaseWrap("juego.JugadorCreado", (UseCase) crearCarroUseCase),
                new UseCase.UseCaseWrap("juego.JuegoIniciado", (UseCase) motorJuegoUseCase),
                new UseCase.UseCaseWrap("carro.KilometrajeCambiado", (UseCase) moverCarroEnCarrilUseCase),
                new UseCase.UseCaseWrap("juego.JuegoFinalizado", (UseCase) notificarGanadoresUseCase)
        );
    }

    @Bean
    public RabbitAdmin rabbitmqAdmin(RabbitTemplate rabbitmqTemplate) {
        var admin = new RabbitAdmin(rabbitmqTemplate);
        admin.declareExchange(new TopicExchange(EXCHANGE));
        return admin;
    }

    /*
    @Bean
    public MongoTemplate mongoTemplate(@Value("${spring.data.mongodb.uri}") String uri){
        ConnectionString connectionString = new ConnectionString(uri);
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(connectionString));
    }
     */

}
