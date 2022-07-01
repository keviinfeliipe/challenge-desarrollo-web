package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.carril.values.CarrilId;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.CarroKilometraje;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CarroKilometrajeQueryService {
    private final MongoTemplate mongoTemplate;
    @Autowired
    public CarroKilometrajeQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CarroKilometraje> getCarroKilometraje(JuegoId juegoId, CarrilId carrilId){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        var aggregation = Aggregation.newAggregation(
                Aggregation.match(where("juegoId.uuid").is(juegoId.value())),
                Aggregation.match(where("carrilId.uuid").is(carrilId.value()))
        );

        return mongoTemplate.aggregate(aggregation, "carro.KilometrajeCambiado", String.class)
                .getMappedResults().stream()
                .map(body -> new Gson().fromJson(body,CarroKilometrajeRecord.class))
                .map(carroKilometrajeRecord -> {
                   var record = new CarroKilometraje();
                   record.setDistancia(carroKilometrajeRecord.getDistancia());
                   try {
                        Date date = sdf.parse(carroKilometrajeRecord.getWhen().get$date());
                        record.setWhen(date);
                   } catch (ParseException e) {
                        e.printStackTrace();
                   }
                    return record;
                }).collect(Collectors.toList());

    }

    public static class CarroKilometrajeRecord{
        private Integer distancia;
        private When when;

        public Integer getDistancia() {
            return distancia;
        }

        public void setDistancia(Integer distancia) {
            this.distancia = distancia;
        }

        public When getWhen() {
            return when;
        }

        public void setWhen(When when) {
            this.when = when;
        }

        public static class When{
            private String $date;

            public String get$date() {
                return $date;
            }

            public void set$date(String $date) {
                this.$date = $date;
            }
        }

    }



}
