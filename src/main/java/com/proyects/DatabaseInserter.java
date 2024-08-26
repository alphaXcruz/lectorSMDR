package com.proyects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Stack;

public class DatabaseInserter {
//    private static String DB_URL = "jdbc:sqlserver://10.1.4.63:1433;databaseName=SCOTNET";
//    private static String USER = "sa";
//    private static String PASS = "Alpha123!";
    private static String DB_URL = "jdbc:sqlserver://10.1.4.63:1433;databaseName=SCOTNET";
    private static String USER = "sa";
    private static String PASS = "Alpha123!";
    private Stack<Llamada> stack;

    public DatabaseInserter(Stack<Llamada> stack) {
        this.stack = stack;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insertData() {
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter("error_insertsbd.txt", true))) {
            while (!stack.isEmpty()) {
                Llamada llamada = stack.pop();
                System.out.println(llamada);
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                     PreparedStatement pstmt = conn.prepareStatement("INSERT INTO SMDR (IDEstado, IDCentral, Fecha, Hora, Duracion, Troncal, Linea, Interno, Cuenta, Numero, TipoLlamada, EstadoRegistro, IDUsuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstmt.setInt(1, llamada.getIdEstado());
                    pstmt.setInt(2, buscarCentralPorId(llamada.getIdCentral()));
                    pstmt.setString(3, llamada.getFecha());
                    pstmt.setString(4, llamada.getHora());
                    pstmt.setString(5, llamada.getDuracion());
                    pstmt.setString(6, llamada.getTroncal());
                    pstmt.setString(7, llamada.getLinea());
                    pstmt.setString(8, llamada.getInterno());
                    pstmt.setString(9, llamada.getCuenta());
                    pstmt.setString(10, llamada.getNumero());
                    pstmt.setShort(11, llamada.getTipoLlamada());
                    pstmt.setBoolean(12, llamada.isEstadoRegistro());
                    pstmt.setString(13, llamada.getIdUsuario());

                    boolean results = pstmt.execute();
                    while (!results && pstmt.getUpdateCount() != -1) {
                        results = pstmt.getMoreResults();
                        System.out.println(results);
                    }
                    System.out.println("Llamada insertada en la base de datos: " + llamada);

                } catch (SQLException e) {
                    errorWriter.write("Error al insertar llamada: " + llamada.toString() + "\n");
                    errorWriter.write("Mensaje de error: " + e.getMessage() + "\n\n");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int buscarCentralPorId(String idCentral) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer IDCentral = 0;
        try {
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            String selectSQL = "SELECT IDCentral FROM Central WHERE IpAdress = ?";
            pstmt = con.prepareStatement(selectSQL);
            pstmt.setString(1, idCentral);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                IDCentral= rs.getInt("IDCentral");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return IDCentral;
    }
}

