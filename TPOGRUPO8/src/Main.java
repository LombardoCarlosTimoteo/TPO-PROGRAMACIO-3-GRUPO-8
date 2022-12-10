import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static ArrayList<String> readTrip(String direccion) throws Exception{ //LECTURA DEL ARCHIVO DE TRIPULACIONES, OBTENIENDO SUS IDs
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

    public static String readOrigen(String direccion) throws Exception{ //LECTURA DEL ARCHIVO DE TRIPULACIONES, OBTENIENDO SOLO EL ORIGEN
        File arch = new File(direccion);
        BufferedReader reader = new BufferedReader(new FileReader(arch));
        String linea;
        reader.readLine();
        linea = reader.readLine();
        String[] params = linea.split(",");
        reader.close();
        return params[1];
    }

    public static ArrayList<Vuelo> readVuelo(String direccion) throws Exception { //LECTURA DEL ARCHIVO DE VUELOS
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

    public static LocalDateTime formatting(String fecha) { //CAMBIAMOS EL FORMATO DE LA FECHA
        String[] FechaHora = fecha.split(" ");
        String[] Fechas = FechaHora[0].split("/");
        String[] Horas = FechaHora[1].split(":");
        LocalDateTime horafecha = LocalDateTime.of(Integer.parseInt(Fechas[2]), Integer.parseInt(Fechas[1]),
                Integer.parseInt(Fechas[0]), Integer.parseInt(Horas[0]), Integer.parseInt(Horas[1]));
        return horafecha;
    }

    public static void OrdenarListaVuelos(ArrayList<Vuelo> array) { //ORDENAMOS LA LISTA DE VUELOS MEDIANTE UN BUBBLE-SORT
        for (int i = array.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                long diferencia = Duration.between(array.get(j).fechaDespegue, array.get(j + 1).fechaDespegue).toMinutes();

                if (diferencia<0) {
                    swap(array, j, j+1);
                }
            }
        }
    }
    public static void swap(ArrayList<Vuelo> array, int a, int b) { //SWAPEO DEL BUBBLE-SORT, CREANDO UN VUELO AUXILIAR
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

    public static void ImprimirListaVuelosAsignados(ArrayList<Vuelo> ListaVuelos,ArrayList<String> ListaSolucion){ //IMPRIMIMOS CADA VUELO CON SU ORIGEN, DESTINO Y TRIPULACION ASIGNADA
        for (int i=0;i<ListaVuelos.size();i++){
            System.out.print("\nPara el vuelo de origen "+ListaVuelos.get(i).origen+" y destino "+ListaVuelos.get(i).destino+": "+ListaSolucion.get(i));
        }
        System.out.println();
    }


    public static boolean TripulacionesEnOrigen(ArrayList<Tripulacion> Lista, String origen, ArrayList<Vuelo> ListaVuelos, ArrayList<String> ListaSolucionParcial){ //VERIFICAMOS QUE CADA TRIPULACION ESTE EN EL ORIGEN
        boolean estan = true;

        for (Tripulacion t : Lista) {
            int pos = ObtenerIndexUltimoVuelo(t.id, ListaSolucionParcial);
            if (pos != -1) {
                if (!origen.equals(ListaVuelos.get(pos).destino)) estan = false;
            }
        }
        return estan;
    }


    public static ArrayList<String> AlgoritmoAsignaciones(ArrayList<Tripulacion> ListaTripulaciones, ArrayList<Vuelo> ListaVuelos,ArrayList<String> ListaSolucionParcial, ArrayList<String> ListaMejorSolucion,String origen,int MejorCosto, int Index){ //ASIGNAMOS LOS VUELOS MEDIANTE BACKTRACKING

        if (ListaVuelos.size() == Index) {
            if (TripulacionesEnOrigen(ListaTripulaciones, origen, ListaVuelos, ListaSolucionParcial)){
                int costoActual = CalcularCosto(ListaSolucionParcial, ListaTripulaciones, ListaVuelos);
                if (costoActual < MejorCosto) {
                    MejorCosto = costoActual;
                    for(int i=0;i<ListaMejorSolucion.size();i++) {
                        ListaMejorSolucion.set(i,ListaSolucionParcial.get(i));
                    }
                }
            }
        }

        else {

            boolean PrimerVuelo = true;

            for (Tripulacion t : ListaTripulaciones) {
                Vuelo VueloAAnalizar = ListaVuelos.get(Index);
                int pos = ObtenerIndexUltimoVuelo(t.id, ListaSolucionParcial);

                PrimerVuelo = true;

                if (pos != -1) {
                    PrimerVuelo = false;
                }

                if (VueloAAnalizar.origen.equals(t.PosActual)) {
                    if (PrimerVuelo) {
                        ArrayList<String> ListaNuevaSolucion = new ArrayList<String>();

                        ListaNuevaSolucion.addAll(ListaSolucionParcial);

                        ListaNuevaSolucion.set(Index, t.id);
                        t.PosActual=VueloAAnalizar.destino;
                        ListaSolucionParcial = AlgoritmoAsignaciones(ListaTripulaciones, ListaVuelos, ListaNuevaSolucion, ListaMejorSolucion, origen, MejorCosto, Index+1);

                    } else {
                        if (Duration.between(ListaVuelos.get(pos).fechaAterrizaje, VueloAAnalizar.fechaDespegue).toMinutes() >= 120) {
                            ArrayList<String> ListaNuevaSolucion = new ArrayList<String>();

                            ListaNuevaSolucion.addAll(ListaSolucionParcial);

                            ListaNuevaSolucion.set(Index, t.id);
                            t.PosActual=VueloAAnalizar.destino;
                            ListaSolucionParcial = AlgoritmoAsignaciones(ListaTripulaciones, ListaVuelos, ListaNuevaSolucion, ListaMejorSolucion, origen, MejorCosto, Index+1);
                        }
                    }
                }
            }
        }
        return ListaMejorSolucion;
    }



    public static int CalcularCosto(ArrayList<String> ListaSolucionParcial,ArrayList<Tripulacion> ListaTripulaciones,ArrayList<Vuelo> ListaVuelos){ //CALCULAMOS EL COSTO TOTAL, CALCULANDO PRIMERO LOS COSTOS DE CADA TRIPULACION POR SEPARADO, Y LOS VAMOS SUMANDO EN UNA VARIABLE ACUMULATIVA
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
                    costo += (Duration.between(Vuelo1.fechaAterrizaje,Vuelo2.fechaDespegue).toMinutes())-120;
                }
            }
            ListaVuelosASumar.clear();
        }
        return costo;
    }

    public static  int ObtenerIndexUltimoVuelo(String id, ArrayList<String> ListaSolucion){ //OBTENEMOS LA POSICION DEL ULTIMO VUELO QUE REALIZO LA TRIPULACION A ANALIZAR
        for(int i=ListaSolucion.size()-1; i>=0;i--){
            if (id.equals(ListaSolucion.get(i))) return i;
        }
        return -1;
    }



    public static void main(String[] args) throws Exception {

        long startTime = System.nanoTime();


        String rutaVuelo = "C:\\Users\\Admin\\Desktop\\Vuelos.csv"; //COLOCAR DIRECCION DEL ARCHIVO DE VUELOS
        String rutaTrip = "C:\\Users\\Admin\\Desktop\\Tripulaciones.csv"; //COLOCAR DIRECCION DEL ARCHIVO DE TRIPULACIONES

        ArrayList<Vuelo> ListaVuelos = readVuelo(rutaVuelo);
        OrdenarListaVuelos(ListaVuelos);

        ArrayList<String> TripulacionesIDs = readTrip(rutaTrip);
        String origen = readOrigen(rutaTrip);

        ArrayList<Tripulacion> ListaTripulaciones = new ArrayList<Tripulacion>() ;
        for (int i=0 ; i<TripulacionesIDs.size();i++){ //CREAMOS TODOS LOS OBJETOS TRIPULACION CON SUS IDs Y SUS POSICIONES INICIALES, Y LOS AGREGAMOS A UNA LISTA
            Tripulacion t = new Tripulacion();
            t.id = TripulacionesIDs.get(i);
            t.PosActual = origen;
            ListaTripulaciones.add(t);
        }

        ArrayList<String> ListaSolucion = new ArrayList<String>();
        for (int i = 0; i < ListaVuelos.size(); i++){ //CREAMOS UNA LISTA SOLUCION CON LA MISMA CANTIDAD DE ESPACIOS QUE VUELOS, DONDE IREMOS ASIGNANDO UNA TRIPULACION A CADA POSICION QUE CORRESPONDERA A UN VUELO EN PARTICULAR.
            ListaSolucion.add("");
        }
        int mejorCosto = Integer.MAX_VALUE;
        ArrayList<String> ListaMejorAsignacion = new ArrayList<String>();

        ListaMejorAsignacion = AlgoritmoAsignaciones(ListaTripulaciones, ListaVuelos,ListaSolucion, ListaSolucion,origen,mejorCosto,0);

        ImprimirListaVuelosAsignados(ListaVuelos,ListaMejorAsignacion);
        int costo = CalcularCosto(ListaMejorAsignacion,ListaTripulaciones,ListaVuelos);
        System.out.println("\nCosto total: " + costo + " USD");
        System.out.println();


        long endTime = System.nanoTime();
        System.out.println("Tiempo de Ejecucion: " + (endTime-startTime)/1e6 + " ms");

    }


}
