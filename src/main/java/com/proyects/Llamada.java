package com.proyects;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Llamada implements Serializable {

    private int idEstado;

    private String idCentral;

    private String fecha;

    private String hora;

    private String duracion;

    private String troncal;

    private String linea;

    private String interno;

    private String cuenta;

    private String numero;

    private short tipoLlamada;

    private boolean estadoRegistro;

    private String idUsuario;
}
