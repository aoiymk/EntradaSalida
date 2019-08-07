package Geometria;

import Herramientas.DataFrame;

/**
 * Created by ecarreno on 14-12-2016.
 */
public class Operaciones {

    public static Double distancia_haversine(Coordenada c1, Coordenada c2){

        Double lat1 = c1.latitud;
        Double lon1= c1.longitud;
        Double lat2 = c2.latitud;
        Double lon2= c2.longitud;

        Double R  = 6378.137;

        Double dLat  = (lat2 - lat1 )* Math.PI/180;
        Double dLong = (lon2 - lon1 )* Math.PI/180;

        Double a = Math.sin(dLat/2) *
                Math.sin(dLat/2) + Math.cos(lat1* Math.PI/180)
                * Math.cos(lat2* Math.PI /180)
                * Math.sin(dLong/2)
                * Math.sin(dLong/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c;
        return d*1000;
        
    }


    public static Double toRadianes(Double d){
        return d* Math.PI /180;
    }

    public static Double Sumakilometros(DataFrame input, String latitud, String longitud){
        double out =0;
        Coordenada c1;
        Coordenada c2;
        c1= new Coordenada( input.getDouble(latitud,0) ,input.getDouble(longitud,0) ,true);

        for(int i =1;i< input.size();i++){
            c2 = new Coordenada( input.getDouble(latitud,i) , input.getDouble(longitud,i),true);

            out = out + distancia_haversine(c1,c2)/1000;
            c1= new Coordenada(input.getDouble(latitud,i),input.getDouble(longitud,i) ,true);

        }



        return out;



    }

    public static Coordenada interseccion(Linea l1, Linea l2){

        if(l1.vertical==true && l2.vertical ==true){
            return null;

        }

        else if(l1.vertical==false && l2.vertical ==false){
            if(l1.m==l2.m){return  null;}

            else{
            Double x = (l2.b -l1.b) / (l1.m - l2.m);
            Double y =l2.m * x + l2.b;
            return new Coordenada(y,x,true);
            }

        }

        else if(l1.vertical==true && l2.vertical == false ){

            Double x =  l1.inicio.longitud;
            Double y = l2.m * x + l2.b;
            return new Coordenada(y,x,true);
        }

        else if(l1.vertical==false && l2.vertical == true ){

            Double x = l2.inicio.longitud;
            Double y = l1.m * x + l1.b;
            return new Coordenada(y,x,true);
        }

        return null;
    }







  


}
