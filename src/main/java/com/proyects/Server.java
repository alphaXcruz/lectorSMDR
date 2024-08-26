package com.proyects;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private Stack<Llamada> stack;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private static String BASE_DIR;
    private static String OUTPUT_DIR;
    private static String ERROR_DIR;
    private static String currentDate;
    private static int IDCentral;

    public Server(Stack<Llamada> stack) {
        this.stack = stack;
        loadConfigurations();
        validateConfigurations();
    }

    private void loadConfigurations() {
        // Configuraciones iniciales
        BASE_DIR = "C:/SMDR_DATA";
        OUTPUT_DIR = "C:/SMDR_DATA/SMDR";
        ERROR_DIR = "C:/SMDR_DATA/SMDR_LOGS";
        IDCentral = 2;
    }

    private void handleTCPException(String errorMessage) {
        String errorFileName = ERROR_DIR + "error_log_save_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFileName, true))) {
            writer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.write(" - Error: " + errorMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateConfigurations() {
        if (BASE_DIR == null || OUTPUT_DIR == null || ERROR_DIR == null) {
            throw new IllegalArgumentException("Las rutas de directorios no pueden ser nulas");
        }
    }

    public void startServer() {
        try {
            readDataFromFile();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error leyendo el archivo", e);
        }
    }

    private void readDataFromFile() {
        String fileName = OUTPUT_DIR + "/llamadas.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Llamada llamada = parseLlamada(line);
                if (llamada != null) {
                    stack.push(llamada);
                    System.out.println("Llamada leída del archivo y almacenada en la pila: " + llamada);
                }
            }
        } catch (IOException | ParseException e) {
            handleTCPException("Error leyendo el archivo: " + e.getMessage());
        }
    }

    private Llamada parseLlamada(String data) throws ParseException {
        String[] parts = data.split(",");
        if (parts.length < 15) {
            System.err.println("El formato del mensaje es incorrecto: " + data);
            return null;
        } else {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("ddMMyyyy");
            Date date = originalFormat.parse(parts[0].split(" ")[0]);

            SimpleDateFormat originalFormat2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat targetFormat2 = new SimpleDateFormat("HHmmss");
            Date originalTime = originalFormat2.parse(parts[0].split(" ")[1]);
            targetFormat2.format(originalTime);

            return Llamada.builder()
                    .idEstado(1)
                    .idCentral(parts[30])
                    .fecha(targetFormat.format(date))
                    .hora(targetFormat2.format(originalTime))
                    .duracion(formatoDuracion(parts[1]))
                    .troncal((parts[4]).equals("O") ? "0" : "9")
                    .linea(parts[13].replaceAll("\\D+", ""))
                    .interno(parts[3])
                    .cuenta(parts[7])
                    .numero(parts[5])
                    .tipoLlamada((short) 0)
                    .estadoRegistro(true)
                    .idUsuario(parts[12].length() < 10 ? parts[12] : parts[12].substring(0, 10))
                    .build();
        }
    }

    private static String formatoDuracion(String duracion) {
        int formathrs = 0;
        int formatmin = 0;
        int formatsec = 0;
        int formatDuracion = 0;

        formathrs = Integer.parseInt(duracion.trim().substring(0, 2)) * 3600;
        formatmin = Integer.parseInt(duracion.trim().substring(3, 5)) * 60;
        formatsec = Integer.parseInt(duracion.trim().substring(6, 8));

        formatDuracion = formathrs + formatmin + formatsec;
        return Integer.toString(formatDuracion);
    }
}
