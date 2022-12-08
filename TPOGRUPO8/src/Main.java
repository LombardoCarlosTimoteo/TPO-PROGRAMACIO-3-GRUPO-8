import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static ArrayList<String> readTrip(String direccion) throws Exception{
        File arch = new File(direccion);
        BufferedReader reader = new BufferedReader(new FileReader(arch));
        String linea;
        ArrayList<String> listaTrip = new ArrayList<>();

        reader.readLine();
        while ((linea = reader.readLine()) != null) {
            String[] params = linea.split(",");
            listaTrip.add(params[0]);
        }
        reader.close();
        return listaTrip;
    }

    public static String readOrigen(String direccion) throws Exception{
        File arch = new File(direccion);
        BufferedReader reader = new BufferedReader(new FileReader(arch));
        String linea;
        reader.readLine();
        linea = reader.readLine();
        String[] params = linea.split(",");
        reader.close();
        return params[1];
    }

    public static ArrayList<Vuelo> readVuelo(String direccion) throws Exception {
        File arch = new File(direccion);
        BufferedReader reader = new BufferedReader(new FileReader(arch));
        String linea;
        ArrayList<Vuelo> Vuelos = new ArrayList<Vuelo>();

        reader.readLine();
        while ((linea = reader.readLine()) != null) {
            String[] params = linea.split(",");
            LocalDateTime d1 = formatting(params[3]);
            LocalDateTime d2 = formatting(params[4]);
            Vuelo i = new Vuelo(params[1], params[2], params[0], d1, d2);
            i.toString();
            Vuelos.add(i);
        }
        reader.close();
        return Vuelos;
    }

    public static LocalDateTime formatting(String fecha) {
        String[] FechaHora = fecha.split(" ");
        String[] Fechas = FechaHora[0].split("/");
        String[] Horas = FechaHora[1].split(":");
        LocalDateTime horafecha = LocalDateTime.of(Integer.parseInt(Fechas[2]), Integer.parseInt(Fechas[1]),
                Integer.parseInt(Fechas[0]), Integer.parseInt(Horas[0]), Integer.parseInt(Horas[1]));
        return horafecha;
    }

    public static void OrdenarListaVuelos(ArrayList<Vuelo> array) {
        for (int i = array.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                long diferencia = Duration.between(array.get(j).fechaDespegue, array.get(j + 1).fechaDespegue).toMinutes();

                if (diferencia<0) {
                    swap(array, j, j+1);
                }
            }
        }
    }
    public static void swap(ArrayList<Vuelo> array, int a, int b) {
        Vuelo value = new Vuelo( array.get(b).origen, array.get(b).destino, array.get(b).ID,  array.get(b).fechaDespegue, array.get(b).fechaAterrizaje);

        array.get(b).fechaDespegue=array.get(a).fechaDespegue;
        array.get(b).fechaAterrizaje=array.get(a).fechaAterrizaje;
        array.get(b).origen=array.get(a).origen;
        array.get(b).destino=array.get(a).destino;
        array.get(b).ID=array.get(a).ID;

        array.get(a).fechaDespegue=value.fechaDespegue;
        array.get(a).fechaAterrizaje=value.fechaAterrizaje;
        array.get(a).origen=value.origen;
        array.get(a).destino=value.destino;
        array.get(a).ID=value.ID;
    }

    public static void ImprimirListaVuelos(ArrayList<Vuelo> Lista){
            for (Vuelo v:Lista){
                System.out.print(v.fechaDespegue+", ");
            }
        System.out.println();
    }

    public static ArrayList<String> AlgoritmoAsignaciones(ArrayList<Tripulacion> ListaTripulaciones, ArrayList<Vuelo> ListaVuelos, String PosicionActual,ArrayList<String> ListaSolucionParcial, ArrayList<String> ListaMejorSolucion,String origen,int MejorCosto){

        ArrayList<Vuelo> ListaVuelosAux = new ArrayList<Vuelo>();
        ListaVuelosAux.addAll(ListaVuelos);
        for(Tripulacion t: ListaTripulaciones){
            ListaVuelosAux.removeAll(t.ListaVuelosAsignados);
        }
        if (ListaVuelosAux.size()==0){
            for (Tripulacion t: ListaTripulaciones){
                if(t.PosActual.equals(origen)){
                    int costoActual = CalcularCosto(ListaSolucionParcial,ListaTripulaciones,ListaVuelos);
                    if(costoActual<MejorCosto) MejorCosto = costoActual;
                }
            }
        }
        else {
            ArrayList<Vuelo> ListaVuelosAdyacentes = new ArrayList<Vuelo>();
            for (Vuelo v : ListaVuelosAux ){
                if (v.origen.equals(PosicionActual)){
                    ListaVuelosAdyacentes.add(v);
                }
            }
            for(Vuelo v: ListaVuelosAdyacentes){
                for(Tripulacion t: ListaTripulaciones){
                    PosicionActual = v.destino;
                    ArrayList<String> ListaNuevaSolucion = new ArrayList<String>();
                    ListaNuevaSolucion.addAll(ListaSolucionParcial);
                    //Buscamos la posicion del vuelo a analizar en la lista de vuelos ordenadas
                    int IndexVuelo = 0;
                    for (Vuelo vPos: ListaVuelos){
                        if(vPos.equals(v)) IndexVuelo = ListaVuelos.indexOf(vPos);
                    }
                    int pos = ObtenerIndexUltimoVuelo(t.id,ListaSolucionParcial);
                    if (pos != -1){
                        if(ListaVuelos.get(pos).destino.equals(PosicionActual) && Duration.between(v.fechaDespegue,ListaVuelos.get(pos).fechaAterrizaje).toHours()>=2){
                            ListaNuevaSolucion.set(IndexVuelo, t.id);
                            ListaMejorSolucion = AlgoritmoAsignaciones( ListaTripulaciones,  ListaVuelos, PosicionActual, ListaNuevaSolucion, ListaMejorSolucion,origen,MejorCosto);
                        }
                    }
                    //Posible error
                    else{
                        if (origen.equals(PosicionActual)){
                            ListaNuevaSolucion.set(IndexVuelo,t.id);
                            ListaMejorSolucion = AlgoritmoAsignaciones( ListaTripulaciones,  ListaVuelos, PosicionActual, ListaNuevaSolucion, ListaMejorSolucion,origen,MejorCosto);
                        }
                    }

                }
            }
        }
        return ListaSolucionParcial;
    }


    public static int CalcularCosto(ArrayList<String> ListaSolucionParcial,ArrayList<Tripulacion> ListaTripulaciones,ArrayList<Vuelo> ListaVuelos){
        int costo =0;
        ArrayList<Vuelo> ListaVuelosASumar= new ArrayList<Vuelo>();
        for(Tripulacion t: ListaTripulaciones){
            for(int i=0; i<ListaSolucionParcial.size();i++){
                if(ListaSolucionParcial.get(i).equals(t.id)) ListaVuelosASumar.add(ListaVuelos.get(i));
            }
            for (int i=0; i<ListaVuelosASumar.size();i++){
                if(i+1<ListaVuelosASumar.size()){
                    Vuelo Vuelo1 = ListaVuelosASumar.get(i);
                    Vuelo Vuelo2 = ListaVuelosASumar.get(i+1);
                    costo += Duration.between(Vuelo1.fechaAterrizaje,Vuelo2.fechaDespegue).toMinutes()-120;
                }
            }
            ListaVuelosASumar.clear();
        }
        return costo;
    }

    public static  int ObtenerIndexUltimoVuelo(String id, ArrayList<String> ListaSolucion){
        for(int i=ListaSolucion.size()-1; i>=0;i--){
            if (id.equals(ListaSolucion.get(i))) return i;
        }
        return -1;
    }



    public static void main(String[] args) throws Exception {
        String rutaVuelo = "C:\\Users\\timoteo\\OneDrive\\_UADE\\2. Programacion 3\\TP\\TPO\\Vuelos.csv"; //COLOCAR DIRECCION DEL ARCHIVO DE VUELOS
        String rutaTrip = "C:\\Users\\timoteo\\OneDrive\\_UADE\\2. Programacion 3\\TP\\TPO\\Tripulaciones.csv"; //COLOCAR DIRECCION DEL ARCHIVO DE TRIPULACIONES

        ArrayList<Vuelo> ListaVuelos = readVuelo(rutaVuelo);
        OrdenarListaVuelos(ListaVuelos);

        ArrayList<String> TripulacionesIDs = readTrip(rutaTrip);
        String origen = readOrigen(rutaTrip);

        ArrayList<Tripulacion> ListaTripulaciones = new ArrayList<Tripulacion>() ;
        for (int i=0 ; i<TripulacionesIDs.size();i++){
            Tripulacion t = new Tripulacion();
            t.id = TripulacionesIDs.get(i);
            t.PosActual = origen;
            t.ListaVuelosAsignados= new ArrayList<Vuelo>();
            ListaTripulaciones.add(t);
        }

        ArrayList<String> ListaSolucion = new ArrayList<String>();
        for (int i=0; i<ListaVuelos.size();i++){
            ListaSolucion.add("");
        }
        int mejorCosto = Integer.MAX_VALUE;
        ArrayList<String> solucion = new ArrayList<String>();
        solucion=AlgoritmoAsignaciones(ListaTripulaciones,ListaVuelos,origen,ListaSolucion,ListaSolucion,origen,mejorCosto);
        // COMIENZA BACKTRACKING
        ImprimirListaVuelos(ListaVuelos);
        System.out.print(solucion);

    }

}