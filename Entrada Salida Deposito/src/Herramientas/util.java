package Herramientas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ecarreno on 22-07-2016.
 */


public class util {

    public static void print(String texto) {
        System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + " - " + texto);
    }

    public static void p(Object texto) {
        System.out.println(texto);
    }


    public static Date Fecha(String fecha, String formato) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat(formato);
        return format.parse(fecha);

    }


    public static void exit() {
        System.exit(0);
    }

    public static String get_id_fec_dia(String fecha) {

        String fecha_nueva = "";

        if (!fecha.equalsIgnoreCase("")) {

            String ano = fecha.substring(0, 4);

            Integer MES = Integer.parseInt(fecha.substring(5, 7));
            String mes;
            if (MES >= 10) {
                mes = MES.toString();
            } else {
                mes = "0" + MES.toString();

            }


            Integer DIA = Integer.parseInt(fecha.substring(8, 10));
            String dia;
            if (DIA >= 10) {
                dia = DIA.toString();
            } else {
                dia = "0" + DIA.toString();

            }


            fecha_nueva = ano + mes + dia;

        }
        return fecha_nueva;


    }

    //Encuentra el id_fec_dia del dia anterior y/o siguiente.
    public static String SumaRestaDias(String fecha, int dias) {

        String fecha_nueva = "";

        if (!fecha.equalsIgnoreCase("")) {
            int a = Integer.parseInt(fecha.substring(0, 4));
            int m = Integer.parseInt(fecha.substring(4, 6));
            int d = Integer.parseInt(fecha.substring(6, 8));


            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.YEAR, a);
            calendar.set(Calendar.MONTH, m - 1);
            calendar.set(Calendar.DAY_OF_MONTH, d);

            calendar.add(Calendar.DATE, dias);

            String ano = String.valueOf(calendar.get(Calendar.YEAR));
            String mes = String.valueOf(calendar.get(Calendar.MONTH) + 1).length() < 2 ? "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String dia = String.valueOf(calendar.get(Calendar.DATE)).length() < 2 ? "0" + String.valueOf(calendar.get(Calendar.DATE)) : String.valueOf(calendar.get(Calendar.DATE));

            fecha_nueva = ano + mes + dia;

        }

        return fecha_nueva;

    }


    public static String ConvertirRowCsv(ArrayList fila) {
        StringBuilder salida = new StringBuilder();

        for (int i = 0; i < fila.size() - 1; i++) {
            salida.append(fila.get(i)).append(",");}

        salida.append(fila.get(fila.size() - 1));
        return salida.toString();

    }

    public static String sumaID(String fecha, int dias) {

        String fecha_nueva = "";
        if (!fecha.equalsIgnoreCase("")) {
            Calendar calendar = Calendar.getInstance();


            int a = Integer.parseInt(fecha.substring(0, 4));
            int m = Integer.parseInt(fecha.substring(4, 6));
            int d = Integer.parseInt(fecha.substring(6, 8));

            calendar.set(Calendar.YEAR, a);
            calendar.set(Calendar.MONTH, m - 1);
            calendar.set(Calendar.DAY_OF_MONTH, d);

            calendar.add(Calendar.DATE, dias);

            String ano = String.valueOf(calendar.get(Calendar.YEAR));
            String mes = String.valueOf(calendar.get(Calendar.MONTH) + 1).length() < 2 ? "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String dia = String.valueOf(calendar.get(Calendar.DATE)).length() < 2 ? "0" + String.valueOf(calendar.get(Calendar.DATE)) : String.valueOf(calendar.get(Calendar.DATE));

            fecha_nueva = ano + mes + dia;

        }
        return fecha_nueva;

    }

    public static String printDate(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formato.format(fecha);

    }

    public static Date DateNull() {
        Date out = new Date();
        String a = "2999-12-31 23:59:59";
        try {
            out = Fecha(a, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return out;


    }
}

