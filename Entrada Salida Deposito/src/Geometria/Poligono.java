package Geometria;

import Herramientas.DataFrame;

import java.util.ArrayList;

/**
 * Created by ecarreno on 14-12-2016.
 */
public class Poligono {

    public ArrayList<Coordenada> set_puntos = new ArrayList();
    public ArrayList set_lineas = new ArrayList();
    BoundigBox cuadro = new BoundigBox();
    public DataFrame cuerpo_poligono = new DataFrame();

    public void AddCoordenada(Coordenada c ){
        set_puntos.add(c);
        cuadro.AddCoordenada(c);
    }

    public void Construir(){

        Coordenada c = (Coordenada) set_puntos.get(0);
        Coordenada c1;
        Linea l;

        for (int i =0;i< set_puntos.size();i++){
            c1 = (Coordenada) set_puntos.get(i);
            l = new Linea(c,c1);

            set_lineas.add(l);
            c= c1;

        }
        c= (Coordenada) set_puntos.get(0);
        c1 = (Coordenada) set_puntos.get(set_puntos.size()-1);
        l=new Linea(c1,c);
        set_lineas.add(l);

        cuerpo_poligono.columnas.add("origen");
        cuerpo_poligono.columnas.add("anterior");
        cuerpo_poligono.columnas.add("siguiente");
        cuerpo_poligono.columnas.add("angulo");

        for ( int i =0;i< set_puntos.size();i++){

            ArrayList f= new ArrayList();
            f.add(set_puntos.get(i));
            if(i==0){
                f.add(set_puntos.get(set_puntos.size()-1));
            }

            else{f.add(set_puntos.get(i-1));}

            if (i==set_puntos.size()-1){

                f.add(set_puntos.get(0));
            }
            else{f.add(set_puntos.get(i+1));}

            Double x = Operaciones.distancia_haversine((Coordenada) f.get(0),(Coordenada)f.get(1)) ;
            Double y = Operaciones.distancia_haversine((Coordenada) f.get(0), (Coordenada)f.get(2)  );

            Double angulo = Math.atan( y/x);

            f.add(angulo);
                cuerpo_poligono.add(f);



        }
        cuerpo_poligono = cuerpo_poligono.filtrar("angulo","0.0",false);


    }

    public boolean Contiene(Coordenada p){
        Double epsilon =( Math.abs(cuadro.xmax) - Math.abs(cuadro.xmin)) ;

      //  Coordenada referencia = new Coordenada(cuadro.ymin - epsilon,cuadro.xmin - epsilon,true);

        Coordenada referencia = new Coordenada(-33.416000, -70.600757,true  );
        Linea rayo = new Linea(referencia,p);
        int numero_intersecciones =0;


        Linea l;
        for (int j =0;j< set_lineas.size();j++){
            l = (Linea) set_lineas.get(j);
            Coordenada interseccion = Operaciones.interseccion(rayo,l);

            if (!(interseccion == null)) {
                if(l.Contiene(interseccion) == true &&  rayo.Contiene(interseccion)){
                numero_intersecciones = numero_intersecciones +1;}
            }
        }
     //   util.p(numero_intersecciones);
        if(numero_intersecciones % 2 ==1){return true;}
        else{return false;}

    }



}
