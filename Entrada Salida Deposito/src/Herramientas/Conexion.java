package Herramientas;

import java.sql.*;

public class Conexion {
    private static final String url 												= "jdbc:microsoft:sqlserver://";
    private static final String ipAndromeda 										= "192.168.250.21";
    private static final String ipPegasus	 									= "192.168.250.23";
    private static final String portNumber 										= "1433";
    private static  final String userName 										= "procesos_BA";
    private static  final String  password 										= "**city2015**";
    private static final String selectMethod 									= "Direct";
    private static Connection conexion = null;
    private static Statement statement;
    private static String c;

    public Conexion(String Servidor, String Database){
        if (Servidor.equalsIgnoreCase("andromeda")) {
            c = url + ipAndromeda + ":" + portNumber + ";databaseName=" + Database + ";selectMethod=" + selectMethod + ";";
        }

        if (Servidor.equalsIgnoreCase("pegasus")){
            c = url + ipPegasus + ":" + portNumber + ";databaseName=" + Database + ";selectMethod=" + selectMethod + ";";
        }
    }

    public Statement getStatement(){

        try {
        Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
        conexion = DriverManager.getConnection(c, userName, password);
        statement = conexion.createStatement();

        }
        catch (Exception e){e.printStackTrace();}
        return statement;
    }

    public static ResultSet SQL(String Consulta) throws Exception {
            ResultSet resultado;
            Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
            conexion = DriverManager.getConnection(c, userName, password);
            Statement stmt = conexion.createStatement();
            resultado = stmt.executeQuery(Consulta);
            return resultado;

    }


    public void Close(ResultSet resultado) throws SQLException {
        resultado.close();
        conexion.close();

    }




}
