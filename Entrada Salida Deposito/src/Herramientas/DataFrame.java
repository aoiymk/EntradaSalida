package Herramientas;

import Geometria.Poligono;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ecarreno on 14-06-2016.
 */

public class DataFrame {
    public ArrayList<String> columnas;
    public ArrayList<ArrayList> datos;


    /**CONSTRUCTORES**/
    public DataFrame() {
        ArrayList columnas = new ArrayList();
        ArrayList datos = new ArrayList();
        this.columnas= columnas;
        this.datos= datos;

    }

    public DataFrame(ArrayList<String> columnas, ArrayList<ArrayList> datos) {
        this.datos = datos;
        this.columnas = columnas;
    }

    /**SET'S**/
    public void set(String nombre, int fila, Object valor) {
        datos.get(fila).set( this.Col(nombre),valor );
    }
    public void append(DataFrame dataframe) {
        ArrayList fila;
        for(int i =0;i< dataframe.size();i++){
            fila = new ArrayList();

            for (int j=0;j<columnas.size();j++){
                try{fila.add( dataframe.getDate( columnas.get(j) ,i ) );
                }
                catch (Exception e){
                    fila.add( dataframe.getString( columnas.get(j) ,i ) );
                }
            }
            datos.add(fila);
        }
    }

    /**GET'S **/
    public ArrayList getDatos(DataFrame dataframe) {return datos;}
    public int getIndexFila(String columna,String parametro){
        Integer fila=-1;

        for(int i=0;i<this.size();i++){
            if(datos.get(i).contains(parametro)==true) {
                if(this.Col(columna) == datos.get(i).indexOf(parametro)){fila=i; }

            }
        }
        return  fila;

    }
    public String getString(String nombre, int fila) {return datos.get(fila).get(Col(nombre)).toString();}
    public int getInt(String nombre, int fila) {return Integer.parseInt(getString(nombre,fila));}
    public ArrayList getColumna(String nombrecolumna) {
        ArrayList columna = new ArrayList();
        for(int i=0;i<datos.size();i++){
            columna.add(datos.get(i).get(Col(nombrecolumna)));
        }
    return columna;
    }
    public ArrayList getFila(int fila){
        return datos.get(fila);
    }
    public Date getDate(String nombre, int fila) {
        return (Date) datos.get(fila).get( this.Col(nombre) );
    }
    public Double getDouble(String nombre, int fila) {
        String salida = getString(nombre,fila);
        return Double.parseDouble(salida);
    }

    public Poligono getPoligono(String nombre, int fila) {
        return (Poligono) datos.get(fila).get(Col(nombre));
    }

    public int Col(String nombre) {
        if (columnas.indexOf(nombre) == -1) {
            throw new RuntimeException("No se puede Encontrar la Columna: " + nombre);

        } else {
            return columnas.indexOf(nombre);
        }
    }

    public Float getFloat(String nombre, int fila) { String salida = getString(nombre,fila);
        return Float.parseFloat(salida);
    }

    /**INSERCION DE DATOS**/
    public void InsertaNuevaColumna(ArrayList nuevo, String nombre,String tipo) {
        columnas.add(nombre);
        for (int i = 0; i < datos.size(); i++){datos.get(i).add(nuevo.get(i));}
    }
    public void InsertaNuevaColumnaVacia(String nombre) {
        columnas.add(nombre);

        for (int i = 0; i < this.size(); i++) {
            datos.get(i).add("");
        }
    }
    public void add(ArrayList fila) {datos.add(fila);}

    /**INFORMACION DE LA TABLA**/
    public int size() {return datos.size();}
    public void print(){

        for (int i =0;i< columnas.size();i++){
            System.out.print(columnas.get(i));
            System.out.print("\t|\t");
        }
        System.out.println();
        for(int i=0;i<datos.size();i++){

            for(int j=0;j<datos.get(i).size();j++){
                System.out.print(datos.get(i).get(j));
                System.out.print("\t|\t");
            }
            System.out.println();

        }


    }
    public void  Columnas(){System.out.println(columnas);}

    /**EXPORTACION DE DATOS**/
    public void to_Csv(String nombre) {
        try {
            FileWriter fichero = new FileWriter(nombre + ".csv");

            //Escribe encabezados
            for (int col = 0; col < columnas.size(); col++) {
                fichero.write(columnas.get(col) + ",");
            }

            fichero.write("\n");

            for (int fila = 0; fila < datos.size(); fila++) {
                for (int col = 0; col < datos.get(fila).size(); col++) {

                    fichero.write(datos.get(fila).get(col) + ",");
                }
                fichero.write("\n");
            }
            fichero.close();
        }
        catch (Exception e ){
            e.printStackTrace();
        }
    }
    public void to_Csv(String nombre, String ruta) {

        PrintWriter fichero = null;
        try {
            fichero = new PrintWriter(new FileWriter(ruta + nombre));


            //Escribe encabezados
            for (int col = 0; col < columnas.size(); col++) {
                fichero.write(columnas.get(col) + ",");
            }

            fichero.write("\n");

            for (int fila = 0; fila < datos.size(); fila++) {
                for (int col = 0; col < datos.get(fila).size(); col++) {

                    fichero.write(datos.get(fila).get(col) + ",");
                }
                fichero.write("\n");
            }
            fichero.close();

        }

        catch (IOException e) {
            throw  new RuntimeException("Hubo un error al generar ruta de salida",e);
        }
    }


    /**IMPORTACION DE DATOS**/
    public void DataframeCSV(String nombre, String Ruta) {
        try {
            ArrayList<String> columnas = new ArrayList();
            ArrayList<ArrayList> datos = new ArrayList();

            this.datos = datos;
            this.columnas = columnas;

            File archivo = new File(Ruta + nombre + ".csv");

            FileReader f = new FileReader(archivo);
            BufferedReader b = new BufferedReader(f);

            String linea = b.readLine();
            String[] cadena = linea.split(",");

            //Identifica los nombres de las columnas
            for (int i = 0; i < cadena.length; i++) {
                columnas.add(cadena[i]);
            }

            f = new FileReader(archivo);
            b = new BufferedReader(f);

            ArrayList fila = null;

            linea = b.readLine();
            linea = b.readLine();

            while (linea != null) {
                cadena = linea.split(",");

                if (cadena.length != columnas.size()){
                    System.err.println("Archivo con missing:" );
                    System.err.println("Nombre: " + nombre  );
                    System.err.println("Ruta: " + Ruta  );
                    System.exit(-1);

                }

                fila = new ArrayList();
                for (int col = 0; col < cadena.length; col++) {

                    try {
                        fila.add(Integer.parseInt(cadena[col]));

                    } catch (Exception e) {

                        try {
                            fila.add(Double.parseDouble(cadena[col]));

                        } catch (Exception e1) {
                            fila.add(cadena[col]);

                        }
                    }
                }

                datos.add(fila);
                linea = b.readLine();
            }


            b.close();
            f.close();
        }
        catch (Exception e){throw new RuntimeException("Error al cargar datos desde: " + Ruta+nombre ,e);}


    }


    /**IMPORTACION DE DATOS**/
    public void DataframeCSV(String FullPath) {
        try {
            ArrayList<String> columnas = new ArrayList();
            ArrayList<ArrayList> datos = new ArrayList();

            this.datos = datos;
            this.columnas = columnas;

            File archivo = new File(FullPath);

            FileReader f = new FileReader(archivo);
            BufferedReader b = new BufferedReader(f);

            String linea = b.readLine();
            String[] cadena = linea.split(",");

            //Identifica los nombres de las columnas
            for (int i = 0; i < cadena.length; i++) {
                columnas.add(cadena[i]);

            }

            f = new FileReader(archivo);
            b = new BufferedReader(f);

            ArrayList fila = null;

            linea = b.readLine();
            linea = b.readLine();

            while (linea != null) {
                cadena = linea.split(",");

                if (cadena.length!=columnas.size()){
                    System.err.println("Archivo con missing:" + FullPath);
                    System.exit(-1);

                }

                fila = new ArrayList();
                for (int col = 0; col < cadena.length; col++) {

                    try {
                        fila.add(Integer.parseInt(cadena[col]));

                    } catch (Exception e) {

                        try {
                            fila.add(Double.parseDouble(cadena[col]));

                        } catch (Exception e1) {
                            fila.add(cadena[col]);

                        }
                    }
                }

                datos.add(fila);
                linea = b.readLine();
            }


            b.close();
        }
        catch (Exception e){throw new RuntimeException("Error al cargar datos desde: " + FullPath ,e);}


    }

    public ResultSet DataFrameSQL(String Consulta, String Servidor , String BasedeDatos ) throws Exception {

        ArrayList<String> columnas = new ArrayList();
        ArrayList<ArrayList> datos = new ArrayList();
        this.datos = datos;
        this.columnas = columnas;

        Conexion conexion = new Conexion(Servidor,BasedeDatos);
        ResultSet resultado = conexion.SQL(Consulta);
        ResultSetMetaData metadata = resultado.getMetaData();

        for (int i = 1; i <= resultado.getMetaData().getColumnCount(); i++) {
            String nombre = metadata.getColumnName(i);
            columnas.add(nombre.toLowerCase());

        }


        while (resultado.next()) {
            ArrayList fila = new ArrayList();
            // System.out.println(resultado.getRow());

            for (int i = 1; i <= resultado.getMetaData().getColumnCount(); i++) {

                if(metadata.getColumnTypeName(i).equalsIgnoreCase("datetime") ){
                    Date salida = new Date(resultado.getTimestamp(i).getTime());
                    fila.add( salida);}

                else{fila.add(resultado.getString(i)); }
            }
            datos.add(fila);
        }

        conexion.Close(resultado);
        return resultado;
    }

    /**ELIMINAR DATOS**/
    public void BorrarFila(int fila) {datos.remove(fila);}
    public void clear(){this.datos.clear();}
    public void BorrarColumna(String nombre){
        int eliminar = Col(nombre);
        this.columnas.remove(eliminar);

        for(int i =0;i< this.datos.size();i++){
            this.datos.get(i).remove(eliminar);
        }

    }

    public void EliminaDuplicados(String[] criterios){
        HashMap validacion = new HashMap();
        ArrayList datos_nuevo = new ArrayList();

        String llave;
        for ( int i =0; i< datos.size(); i++){
            llave = "";


            for(int j =0;j<criterios.length;j++ ){
                if(j==0){

                    llave = getString(criterios[j],i);
                }

                else{llave = llave +","+ getString(criterios[j],i);}


            }

            if (!validacion.containsKey(llave)){
                validacion.put(llave,llave.split(","));
                datos_nuevo.add(datos.get(i));
            }


        }
        this.datos= datos_nuevo;

    }
    public HashMap EliminaDuplicadosHashmap(String[] criterios){
        HashMap validacion = new HashMap();

        String llave;
        for ( int i =0; i< datos.size(); i++){
            llave = "";



            for(int j =0;j<criterios.length;j++ ){
                if(j==0){

                    llave = getString(criterios[j],i);
                }

                else{llave = llave +","+ getString(criterios[j],i);}


            }

            if (!validacion.containsKey(llave)){
                validacion.put(llave,llave.split(","));

            }


        }

        return validacion;
    }
    public DataFrame filtrar(String[] columnas, String[] criterios){
        DataFrame out = new DataFrame();
        ArrayList dato_nuevo = new ArrayList();
        boolean esta;
        for(int i =0;i<datos.size();i++){
            esta= false;
            for(int j =0;j< columnas.length;j++){
                if (getString(columnas[j],i).equalsIgnoreCase(criterios[j])){
                    esta=true;
                }
                else {esta=false; break;}

            }
            if (esta==true){
                dato_nuevo.add(datos.get(i));


            }


        }
        out.columnas = (ArrayList<String>) this.columnas.clone();
        out.datos= dato_nuevo;

        return out;
    }

    /**CALCULOS**/
    public ArrayList ObtieneNoDuplicados(String nombrecolumna) {
        int col = Col(nombrecolumna);
        TreeMap noduplicados = new TreeMap();
        for (int i = 0; i <datos.size(); i++) {
            noduplicados.put(datos.get(i).get(col), i);
        }
        ArrayList res = new ArrayList();
        Collections.addAll(res, noduplicados.keySet().toArray());
        return res;
    }
    public DataFrame filtrar(String nombrecolumna, String criterio, boolean IgualDistinto) {
        ArrayList<ArrayList> datos1 = new ArrayList();
        ArrayList col1= new ArrayList();

        for (int i = 0; i < this.size(); i++) {
            if((IgualDistinto == true) && criterio.equalsIgnoreCase((String) this.getString(nombrecolumna, i))){
                datos1.add(datos.get(i));
            }

            if (IgualDistinto == false && criterio.equalsIgnoreCase((String) this.getString(nombrecolumna,i))==false) {
                datos1.add(datos.get(i));
            }

        }


        for(int i=0;i<columnas.size();i++){col1.add(columnas.get(i));}

        DataFrame salida = new DataFrame(col1, datos1);

        return salida;

    }
    public void creaid(){columnas.add("id");

        for(int i =0;i<datos.size();i++){ datos.get(i).add(i);}
    }
    public DataFrame copy(){
        ArrayList columnas_copia = (ArrayList) columnas.clone();
        ArrayList datos_copia = new ArrayList();


        for(ArrayList fila : datos){datos_copia.add(fila.clone());}
        return new DataFrame(columnas_copia,datos_copia);
    }
    public void RestaFechas(String fecha_inicio , String fecha_fin, String nombre_diferencia, String unidad){
        columnas.add(nombre_diferencia);

        Date inicio_movimiento;
        Date fin_movimiento;

        for (int i =0;i<datos.size();i++){

            inicio_movimiento  =  getDate(fecha_inicio,i);
            fin_movimiento  =  getDate(fecha_fin,i);

            Double dif= Double.valueOf( fin_movimiento.getTime() - inicio_movimiento.getTime());
            if(unidad.equalsIgnoreCase("segundos")){
                dif = dif /1000;

            }

            if(unidad.equalsIgnoreCase("minutos")){
                dif = dif /(1000*60);
            }

            if(unidad.equalsIgnoreCase("horas")){
                dif = dif /(1000*60*60);

            }

            datos.get(i).add(dif);
        }


    }
    public void InsertarConstante(String nombre, String valor){
        columnas.add(nombre);
        for (int i =0;i<datos.size();i++){
            datos.get(i).add(valor);
        }

    }
    public DataFrame GroupBy(String[] grupos, String[][] agregaciones){

        DataFrame out = new DataFrame();

        HashMap<String, String[]> llave = EliminaDuplicadosHashmap(grupos);

        for(String s:grupos){out.columnas.add(s);}
        for(String[] s : agregaciones ){out.columnas.add(s[1]+"_"+s[0]);}


        for (String[] value : llave.values() ) {

            ArrayList fila = new ArrayList();
            DataFrame temp = filtrar(grupos, value);


            for(String v :value){
                fila.add(v);
            }


            for(String[] s : agregaciones )  {

                fila.add(temp.calculo(s[0],s[1]) );

            }
            out.datos.add(fila);

        }
        return out;
    }
    public Double calculo(String columna,String funcion){
        Double output = 0.00;
        if(funcion.equalsIgnoreCase("max")){
            output = Double.MAX_VALUE *-1;
            for(int i =0; i< datos.size();i++){
                output = Double.max(output,getDouble(columna,i));
            }

        }

        if(funcion.equalsIgnoreCase("min")){
            output = Double.MAX_VALUE ;
            for(int i =0; i< datos.size();i++){
                output = Double.min(output,  getDouble(columna,i));}


        }

        if(funcion.equalsIgnoreCase("mean")) {
            output = 0.00;
            for (int i = 0; i < datos.size(); i++) {
                output = output + getDouble(columna, i);
            }
            output = output / datos.size();

        }

        return output;

    }


    /**CONVERSION DE DATOS**/
    public void DatetoString(String nombre_columna,String formato){
        SimpleDateFormat f = new SimpleDateFormat(formato);
        for(int i =0;i< datos.size();i++){
            set(nombre_columna,i,f.format(getDate(nombre_columna,i)));
        }

    }
    public void ApplyLk(HashMap lk ,String columna_join, String nueva_columna){
        String contenido;

        for (int i=0;i<datos.size();i++){

            contenido =  lk.get( getInt(columna_join,i) ).toString();
            datos.get(i).set(Col(columna_join),contenido);

        }
        columnas.set(Col(columna_join),nueva_columna);

    }
    //TODO eliminar webeo
    public void StringtoDate(String nombre,String formato){
        int i =0;
        while (i < datos.size()){

            try{
                set(nombre,i, util.Fecha( getString(nombre,i) ,formato));
            i=i+1;
            }
            catch (Exception e){ datos.remove(i);  }
        }
    }

    public void StringtoDouble(String nombre){
        for ( int i =0;i< datos.size();i++){
            set(nombre,i, Double.parseDouble( getString(nombre,i) ));
        }
    }
    public void Renombrar(String columna_actual,String columna_nueva){
        this.columnas.set(Col(columna_actual),columna_nueva);
    }
    public void OrdernarFechaAscendente(String nombre_columna){
        ArrayList por_ordenar = new ArrayList();
        Date fecha;


        TreeMap ordenado = new TreeMap();
        ArrayList<Integer> listaTemporal;

        for (int i = 0; i < datos.size(); i++) {

            fecha = getDate(nombre_columna,i);
            if (ordenado.containsKey(fecha)) {
                listaTemporal = (ArrayList<Integer>) ordenado.get(fecha);
                listaTemporal.add(i);
            }
            else {
                listaTemporal = new ArrayList();
                listaTemporal.add(i);
            }

            ordenado.put(fecha, listaTemporal);
        }

        Set keys = ordenado.keySet();
        ArrayList pos = new ArrayList();

        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            Object key = i.next();
            ArrayList valores = (ArrayList) ordenado.get(key);
            for (int t = 0; t < valores.size(); t++) {
                pos.add(valores.get(t));
            }
        }

        //pos contiene en que orden se deben referenciar los objetos antiguos para tener el Herramientas ordenado.
        ArrayList salida = new ArrayList();

        for(int i=0;i< pos.size();i++){
            salida.add(datos.get((Integer) pos.get(i)));

        }

        this.datos= salida;

    }




}

