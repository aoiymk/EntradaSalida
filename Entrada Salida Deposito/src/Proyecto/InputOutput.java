package Proyecto;

import Herramientas.DataFrame;
import Herramientas.util;
import Herramientas.BufferArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;


public class InputOutput {

    /**--------Metodos unicos del algoritmo------------**/

    //Obtiene data de EntradaSalidaDeposito
    public DataFrame GetESD(){

        DataFrame depo = new DataFrame();
        try {

            depo = GetDataCSV("esd", util.sumaID(fecha, -1), cliente);
            depo.StringtoDate("de_fx_hora", "yyyy-MM-dd HH:mm:ss");
        }
        catch (Exception e)
        {
            util.print("No existe archivo de deposito del dia anterior");
        }
        return  depo;
    }


    //Obtiene coordenada de depositos.
    public DataFrame depositos() {
            DataFrame depositos = GetDataSQL("depo", new String[]{}, new String[]{});
            return depositos;
    }

    public Integer numero_hilos(){
        return Integer.parseInt(pro.getProperty("numero_hilos"));
    }


    /**--------Metodos propios de la clase------------**/
    static String fecha;
    static String cliente;
    static Properties pro;
    public InputOutput(String fecha, String cliente){

        this.fecha= fecha;
        this.cliente = cliente;

        pro = new Properties();
        try {
            String ruta = System.getProperty("user.dir");
            InputStream entrada =  new FileInputStream(ruta +"\\Input\\prop.properties");
            pro.load(entrada);
            util.print("Configuracion cargada para: " + pro.getProperty("algoritmo"));
        }

        catch (Exception e) {throw  new RuntimeException("No se encuentra el archivo de propiedades"); }
    }


    public void WriteOutput(ArrayList<String> columnas , BufferArrayList buffer,String output ) {
        String ruta = pro.getProperty("ruta_output");
        ruta = SetPath(ruta, fecha, cliente);
        String nombre = SetNombre(pro.getProperty("nomArchivo_output_"+output));

        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        FileWriter out = null;
        try {
            out = new FileWriter(ruta+"/"+nombre+".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(out);

        pw.println(util.ConvertirRowCsv(columnas));

        String linea;
        while (buffer.size() > 0) {
            linea = buffer.GetFirst();
            pw.println(linea);

        }

        pw.close();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  String GetQuery(String NombreArchivo) {
        String output;
        try {
            String ruta = System.getProperty("user.dir");
            BufferedReader b = new BufferedReader(new FileReader(ruta + "/Input/" + NombreArchivo + ".txt"));
            output = "";
            String cadena;

            while ((cadena = b.readLine()) != null) {
                output = output + "\n" + cadena;
            }

            b.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar: " + NombreArchivo + ".txt", e);
        }
        return output;
    }

    private  DataFrame GetDataSQL(String nombre, String[] parametro, String[] reemplazar){
        try {
            DataFrame output = new DataFrame();
            String sql = GetQuery(pro.getProperty("ruta_"+nombre));
            for (int i =0; i<parametro.length ;i++ ){
                sql = sql.replace(parametro[i], reemplazar[i]);
            }
            output.DataFrameSQL(sql, pro.getProperty("ser_"+nombre), pro.getProperty("base_"+nombre));
            return output;
        }
        catch (Exception e){throw new RuntimeException("Conexión SQL para obtener " + nombre,e);}
    }

    public String RutaGPS(){
        String ruta = pro.getProperty("ruta_gps");

        if (cliente.equalsIgnoreCase("1"))
        {
            ruta =ruta+"/Alsacia/"+fecha;
        }
        else{ ruta = ruta+"/Express/"+fecha;}

        return ruta;
    }


    public  DataFrame GetDataCSV(String nombre, String fecha,String cliente){
        try {
            DataFrame output = new DataFrame();
            String ruta = pro.getProperty("ruta_"+nombre);
            String n = pro.getProperty("nomArchivo_"+nombre);

            output.DataframeCSV(SetNombre(n,fecha) ,SetPath(ruta,fecha,cliente));

            return output;
        }
        catch (Exception e){throw new RuntimeException("Ruta para obtener " + nombre,e);}
    }


    public static String SetPath(String origen,String fecha, String cliente){
        if(cliente.equals("1")){origen = origen+"/Alsacia/"+fecha.substring(0,4)+"/";}
        if(cliente.equals("2")){origen = origen+"/Express/"+fecha.substring(0,4)+"/";}
        return  origen;

    }

    public static String SetNombre(String nombre){
        return fecha + "_" + cliente + "_"+ nombre;
    }
    public static String SetNombre(String nombre, String fecha){
        return fecha + "_" + cliente + "_"+ nombre;
    }
}



