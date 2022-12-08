
import java.time.LocalDateTime;
import java.util.Objects;

public class Vuelo {
    String origen;
    String destino;
    String ID;
    LocalDateTime fechaDespegue;
    LocalDateTime fechaAterrizaje;

    public Vuelo(String origen, String destino, String ID, LocalDateTime fechaDespegue, LocalDateTime fechaAterrizaje) {
        this.origen = origen;
        this.destino = destino;
        this.ID = ID;
        this.fechaDespegue = fechaDespegue;
        this.fechaAterrizaje = fechaAterrizaje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vuelo vuelo = (Vuelo) o;
        return Objects.equals(origen, vuelo.origen) && Objects.equals(destino, vuelo.destino) && Objects.equals(ID, vuelo.ID) && Objects.equals(fechaDespegue, vuelo.fechaDespegue) && Objects.equals(fechaAterrizaje, vuelo.fechaAterrizaje);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, destino, ID, fechaDespegue, fechaAterrizaje);
    }
}
