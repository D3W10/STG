package com.g01.connection.models;

import com.g01.connection.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EventoTransito extends Connection {
    public EventoTransito() {
        super("EVENTO_TRANSITO", "(Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao)");
    }
}