package co.com.sofka.cargame.usecase.model;

import java.time.LocalDate;
import java.util.Date;

public class CarroKilometraje {
    private Integer distancia;
    private Date when;

    public CarroKilometraje() {
    }


    public Integer getDistancia() {
        return distancia;
    }

    public Date getWhen() {
        return when;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public void setWhen(Date when) {
        this.when = when;
    }
}
