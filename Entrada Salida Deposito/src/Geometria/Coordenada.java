package Geometria;

/**
 * Created by ecarreno on 14-12-2016.
 */
public class Coordenada {
    Double latitud;
    Double longitud;
    Double x;
    Double y ;

    boolean estado;

    public Coordenada(Double coordenada1, Double coordenada2, boolean estado){
        if (estado ==true){
            this.latitud = coordenada1;
            this.longitud = coordenada2;

        }

        else{
            this.x = coordenada1;
            this.y = coordenada2;

        }

        this.estado = estado;


    }

    public String toString(){
        return this.latitud + "," + this.longitud;
    }


}

