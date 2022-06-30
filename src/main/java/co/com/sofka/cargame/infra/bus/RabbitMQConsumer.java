package co.com.sofka.cargame.infra.bus;

import co.com.sofka.cargame.SocketController;
import co.com.sofka.infraestructure.bus.serialize.SuccessNotificationSerializer;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RabbitMQConsumer {
    private static final Logger logger = Logger.getLogger(RabbitMQConsumer.class.getName());
    @Autowired
    public RabbitMQConsumer() {

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "juego.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "juego.#"
    ))
    public void recievedMessageJuego(Message<String> m) throws IOException, InterruptedException{
        var message = new String(m.getPayload());
        var notification = SuccessNotificationSerializer.instance().deserialize(message);
        var event = notification.deserializeEvent();


    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "carro.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "carro.#"
    ))
    public void recievedMessageCarro(Message<String> m, EventListenerSubscriber eventSubscriber, SocketController socketController) throws IOException, InterruptedException{

    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "carril.handles", durable = "true"),
            exchange = @Exchange(value = "gameextraction", type = "topic"),
            key = "carril.#"
    ))
    public void recievedMessageCarril(Message<String> m, EventListenerSubscriber eventSubscriber, SocketController socketController) throws IOException, InterruptedException{

    }
}
