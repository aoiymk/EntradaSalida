package Herramientas;

import java.util.ArrayList;

/**
 * Created by ecarreno on 19-07-2016.
 */
public class BufferArrayList {

    public ArrayList<String> contenido = new ArrayList();
    private boolean trabajo_terminado = false;

    public synchronized String recoger(int index){
        while( contenido.size() == 0 ) {try {wait();} catch( InterruptedException e ) {}}

        return contenido.get(index);
    }


    public  synchronized void poner(String valor){
        contenido.add(valor);
        notify();

    }


    public synchronized int size(){return contenido.size();}

    public synchronized void IniciaTrabajo(){trabajo_terminado =false;}
    public synchronized void TerminaTrabajo(){trabajo_terminado =true;}
    public synchronized boolean  EstaTerminado(){return trabajo_terminado;}
    public synchronized boolean CerrarHilo(){
        if (EstaTerminado()==true && contenido.size()==0 ){return true;}
        else {return false;}

    }


    public synchronized String GetFirst(){
        while(contenido.size()==0){}
        String salida = contenido.get(0);
        contenido.remove(0);
        notify();
        return  salida;
    }



}


