package Geometria;

public  class Linea{

        Coordenada inicio;
        Coordenada fin;
        Double distancia;
        boolean vertical;
        Double m;
        Double b;
        Double a;

        public Linea(Coordenada inicio, Coordenada fin){
                this.inicio = inicio;
                this.fin=fin;
                this.distancia = Operaciones.distancia_haversine(inicio,fin);

               Double diferencia = fin.longitud - inicio.longitud;

                if(diferencia !=0.00) {
                        this.m = (fin.latitud - inicio.latitud) / (fin.longitud - inicio.longitud);
                        Double d = (fin.longitud - inicio.longitud);
                        this.b = inicio.latitud - m * inicio.longitud;
                        vertical = false;
                }

                else{vertical = true;}



        }


        public boolean Contiene(Coordenada punto){
                Double dist = Operaciones.distancia_haversine(inicio,punto);
                Double dist1 = Operaciones.distancia_haversine(fin,punto);

                if(dist <= distancia && dist1 <= distancia){return true;}
                else{return false;}
        }








}
