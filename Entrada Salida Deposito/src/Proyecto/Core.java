package Proyecto;

import Geometria.Poligono;
import Herramientas.DataFrame;
import Herramientas.util;
import Herramientas.BufferArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Core {

    static String NombreAlgoritmo = "Entrada Salida Deposito";

    public static void  main(String[] args)  {

        String fecha = args[0];
        String cliente= args[1];

        util.print("Inicio Ejecucion Entrada Salida Depositos: " +fecha +" cliente: " +cliente);
        InputOutput InputOutput = new InputOutput(fecha,cliente);

        util.print("Cargando poligonos de deposito");
        DataFrame puntos_deposito = InputOutput.depositos();
        DataFrame depositos = PreProcesoDeposito(puntos_deposito);

        util.print("Cargando Entrada Salida Deposito");
        DataFrame EntradaSalidaDeposito = InputOutput.GetESD();

        util.print("Cargando datos de GPS");

        File f = new File(InputOutput.RutaGPS());
        File[] ficheros = f.listFiles();

        //Elementos Multihilo

        //Buffes que contienen resultados de hilo
        BufferArrayList mov_deposito = new BufferArrayList();
        BufferArrayList ultimo_deposito = new BufferArrayList();


        ExecutorService exec = Executors.newFixedThreadPool(InputOutput.numero_hilos());

        List<Callable<String>> callableTasks = new ArrayList<>();
        List<Future<String>> futures = null;
        try
        {
            futures = exec.invokeAll(callableTasks);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        String ppu;
        Double porcentaje=0.0;
        Integer ultimo_analisis =0;
        Future<String> future;

        DataFrame tempESD;

        for (int x=0;x<ficheros.length;x++){
            DataFrame pulsos_ppu = new DataFrame();

            //Obtiene pulsos GPS
            try {
              // pulsos_ppu.DataframeCSV("\\\\pegasus\\Files\\Output\\GPS\\Alsacia\\20180101\\5788_20180101.txt");
                pulsos_ppu.DataframeCSV(ficheros[x].toString());
                pulsos_ppu.StringtoDate("de_fx_hora", "yyyy-MM-dd HH:mm:ss");
                pulsos_ppu.OrdernarFechaAscendente("de_fx_hora");
            }

            catch (Exception e){
                throw new RuntimeException("Problemas al cargar datos desde: " + ficheros[x].toString() , e);
            }

            ppu = pulsos_ppu.getString("id_veh_vehiculo", 0);
            //if(!ppu.equalsIgnoreCase("5788")){
            //    continue;
            //}

            //Obtiene datos de ESD del día anteior, en caso de que existan.
            tempESD = EntradaSalidaDeposito.filtrar("id_veh_vehiculo",ppu,true);
            if(EntradaSalidaDeposito.size()>0)
            {
                tempESD = EntradaSalidaDeposito.filtrar("id_veh_vehiculo",ppu,true);
            }
            else
            {
                tempESD = new DataFrame();
            }

            porcentaje = porcentaje + (1.0/ficheros.length)*100;

            //Crea hilo
            HiloDeposito hilo_deposito = new HiloDeposito(pulsos_ppu, depositos, ppu, fecha, mov_deposito, ultimo_deposito, cliente,porcentaje, tempESD);

            callableTasks.add(hilo_deposito);

            futures.add(exec.submit(hilo_deposito));

            //Chequea que los hilos vayan terminando con exito
            if ( x>InputOutput.numero_hilos()+1) {
                for (int ff= ultimo_analisis;ff< futures.size();ff++){
                    future = futures.get(ff);
                    try {
                        String  result = future.get();
                        ultimo_analisis = ff-1;
                    }
                    catch (Exception e) {
                        exec.shutdownNow();
                        throw new RuntimeException(e);
                    }
                }
            }

        //   break;
        }

        util.print("Termina generacion de hilos");
        exec.shutdown();
        while (!exec.isTerminated()) {}


        //Construccion de Outputs.
        util.print("Configuracion de salida");

        //Escritura Encabezados
        ArrayList<String> columnas = new ArrayList<>();
        columnas.add("id_cln_cliente");
        columnas.add("id_veh_vehiculo");
        columnas.add("id_dpt_deposito");
        columnas.add("de_fx_hora");
        columnas.add("id_fec_dia");
        columnas.add("de_tipo_movimiento");
        InputOutput.WriteOutput(columnas,mov_deposito,"deposito");

        ArrayList<String> columnas1 = new ArrayList<>();
        columnas1.add("id_fec_dia");
        columnas1.add("id_cln_cliente");
        columnas1.add("id_veh_vehiculo");
        columnas1.add("id_dpt_deposito");
        InputOutput.WriteOutput(columnas1,ultimo_deposito,"udeposito");

        util.print("Finaliza Algoritmo Entrada Salida Deposito");

    }


    public static DataFrame PreProcesoDeposito(DataFrame puntos_deposito){
        DataFrame deposito_nuevo = new DataFrame();
        ArrayList<ArrayList> data = new ArrayList<>();
        ArrayList nombre_deposito = puntos_deposito.ObtieneNoDuplicados("id_dpt_deposito");

        Geometria.Coordenada c;
        for ( Object deposito :nombre_deposito){

        Herramientas.DataFrame pol;
        pol = puntos_deposito.filtrar("id_dpt_deposito",deposito.toString(),true);


        Poligono p = new Poligono();
        ArrayList<Object> fila = new ArrayList<>();

        for (int j = 0;j<pol.size();j++){
        c= new Geometria.Coordenada( pol.getDouble("f_lat_latitud",j) ,
        pol.getDouble("f_lon_longitud",j), true);
        p.AddCoordenada( c );
        }
        p.Construir();

        fila.add(deposito);
        fila.add(p);
        data.add(fila);

        }

        ArrayList<String > nombre = new ArrayList<>();
        nombre.add("id_dpt_deposito");
        nombre.add("poligono");
        deposito_nuevo.columnas=nombre;
        deposito_nuevo.datos=data;

        return deposito_nuevo;

    }





}
