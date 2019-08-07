package Proyecto;

import Geometria.Coordenada;
import Geometria.Operaciones;
import Geometria.Poligono;
import Herramientas.DataFrame;
import Herramientas.util;
import Herramientas.BufferArrayList;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by ecarreno on 06-07-2016.
 */
public class HiloDeposito implements Callable {
    String ppu;
    DataFrame pulsos_ppu;
    DataFrame coordenadasDeposito;
    String id_fec_dia;
    BufferArrayList salida_mov_deposito;
    BufferArrayList salida_ultimo_deposito;
    String cliente;
    String Resultado;
    Double porcentaje;
    DataFrame ESD;

    public HiloDeposito(DataFrame gps, DataFrame coordenadasDeposito, String nombre , String id_fec_dia,BufferArrayList mov_deposito,BufferArrayList ultimo_deposito,String cliente,Double porcentaje, DataFrame ESD){
        this.pulsos_ppu = gps;
        this.ppu = nombre;
        this.coordenadasDeposito = coordenadasDeposito;
        this.id_fec_dia = id_fec_dia;
        this.salida_mov_deposito = mov_deposito;
        this.salida_ultimo_deposito = ultimo_deposito;
        this.cliente = cliente;
        this.porcentaje = porcentaje;
        this.ESD = ESD;

        Resultado= "ok";
    }

    @Override
    public Object call() throws Exception {

        util.print("Comienza: " + pulsos_ppu.getString("id_veh_vehiculo",0) + " - " +  porcentaje + " %");

        try{

            Poligono poligono;
            Boolean esta=false;
            Coordenada coordenadaGPS;

            String id_ultimo_deposito="";

            DataFrame data = new DataFrame();
            data.columnas.add("id_veh_vehiculo");
            data.columnas.add("id_dpt_deposito");
            data.columnas.add("de_fx_hora");

            pulsos_ppu.OrdernarFechaAscendente("de_fx_hora");

            boolean partio_en_deposito= false;
            Integer primer_deposito= -2;

            DataFrame tempESD;

            for (int i =0;i< coordenadasDeposito.size();i++) {


                poligono = coordenadasDeposito.getPoligono("poligono", i);
                //  if (deposito.getInt("id_dpt_deposito",i)!=9){continue;}

                //Busca Data de entrada salida depósito del día anterior.
                if (ESD.size() > 0) {
                    tempESD = ESD.filtrar("id_dpt_deposito", coordenadasDeposito.getString("id_dpt_deposito", i), true);
                } else {
                    tempESD = new DataFrame();
                }


                //Si existe ESD anterior: considera el ultimo registro para setear el estado inicial del vehiculo
                if (tempESD.size() > 0) {

                   tempESD.OrdernarFechaAscendente("de_fx_hora");

                    if (tempESD.getString("de_tipo_movimiento", tempESD.size() - 1).equalsIgnoreCase("ENTRADA")) {
                        esta = true;
                        partio_en_deposito = true;
                        primer_deposito = coordenadasDeposito.getInt("id_dpt_deposito", i);
                    }
                    else {
                        esta = false;
                    }
                }

                //Considera el estado del primer pulso del día
                else {

                    coordenadaGPS = new Coordenada(pulsos_ppu.getDouble("f_lat_latitud", 0),
                            pulsos_ppu.getDouble("f_lon_longitud", 0), true);

                    if (poligono.Contiene(coordenadaGPS)) {
                        esta = true;
                        partio_en_deposito = true;
                        primer_deposito = coordenadasDeposito.getInt("id_dpt_deposito", i);
                    } else {
                        esta = false;
                    }
                }


                 //Busqueda de entradas y salidas
                 for (int p = 0; p <pulsos_ppu.size(); p++) {

                        coordenadaGPS = new Coordenada(pulsos_ppu.getDouble("f_lat_latitud", p),
                               pulsos_ppu.getDouble("f_lon_longitud", p), true);


                        if (Operaciones.distancia_haversine(poligono.set_puntos.get(0),coordenadaGPS)>2000 && !esta){continue;}

                        //util.p(poligono.Contiene(coordenadaGPS) +"  "+ pulsos_ppu.getFila(p)+"   " + esta);

                        if (poligono.Contiene(coordenadaGPS)){
                            id_ultimo_deposito=coordenadasDeposito.getString("id_dpt_deposito", i);}


                        //Busca entradas
                        if (poligono.Contiene(coordenadaGPS) && !esta) {

                            String fila =
                                    cliente + ","
                                            + pulsos_ppu.getString("id_veh_vehiculo", p) + ","
                                            + coordenadasDeposito.getString("id_dpt_deposito", i) + ","
                                            + util.printDate(pulsos_ppu.getDate("de_fx_hora", p)) + ","
                                            + util.get_id_fec_dia(util.printDate(pulsos_ppu.getDate("de_fx_hora", p))) + ","
                                            + "ENTRADA";

                            //Agrega los datos a un DataFrame para luego encontrar el ultimo deposito
                            ArrayList a = new ArrayList();
                            a.add(pulsos_ppu.getString("id_veh_vehiculo", p) );
                            a.add(coordenadasDeposito.getString("id_dpt_deposito", i));
                            a.add(pulsos_ppu.getDate("de_fx_hora", p));
                            data.add(a);

                            esta = true;
                            salida_mov_deposito.poner(fila);
                        }

                        //Busca una salida
                        if (!poligono.Contiene(coordenadaGPS) && esta == true ) {
                            String fila =
                                    cliente+","
                                            +pulsos_ppu.getString("id_veh_vehiculo",p)+ ","
                                            +coordenadasDeposito.getString("id_dpt_deposito",i) +","
                                            +util.printDate(pulsos_ppu.getDate("de_fx_hora",p)) +","
                                            +util.get_id_fec_dia( util.printDate( pulsos_ppu.getDate("de_fx_hora",p))) +","
                                    +"SALIDA";

                            salida_mov_deposito.poner(fila);

                            //Agrega los datos a un DataFrame para luego encontrar el ultimo deposito
                            ArrayList a = new ArrayList();
                            a.add(pulsos_ppu.getString("id_veh_vehiculo", p) );
                            a.add(coordenadasDeposito.getString("id_dpt_deposito", i));
                            a.add(pulsos_ppu.getDate("de_fx_hora", p));
                            data.add(a);
                            esta=false;
                        }
                }
            }

            data.OrdernarFechaAscendente("de_fx_hora");

            //Determina ultimo deposito
            if (data.size()>=1 ){
                String fila = id_fec_dia +","
                +cliente+","
                +data.getString("id_veh_vehiculo",data.size()-1)+","
                +data.getString("id_dpt_deposito",data.size()-1);
                salida_ultimo_deposito.poner(fila);
            }


            else if (partio_en_deposito ){
                String fila = id_fec_dia +","
                +cliente+","
                +pulsos_ppu.getInt("id_veh_vehiculo",0)+","
                +primer_deposito;
                salida_ultimo_deposito.poner(fila);
            }
    }

    catch (Exception e){
        Resultado = "nok";
        throw new RuntimeException("Hubo un problema al ejecutar logica de negocios en ppu:  " + ppu,e);
    }


    return Resultado;
    }

}
