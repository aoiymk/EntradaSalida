package Geometria;

/**
 * Created by ecarreno on 14-12-2016.
 */
public class BoundigBox {

    Double xmax= Double.MAX_VALUE *-1;
    Double ymax= Double.MAX_VALUE*-1;
    Double xmin= Double.MAX_VALUE ;
    Double ymin= Double.MAX_VALUE ;

    public void AddCoordenada(Coordenada c){
        if(c.longitud > xmax ){
       xmax = c.longitud; }
        if(c.longitud < xmin) {
            xmin = c.longitud;
        }
        if(c.latitud > ymax){
        ymax = c.latitud;}

        if(c.latitud < ymin) {
            ymin = c.latitud;
        }

    }



}
