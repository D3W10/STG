package com.g01.connection.models;

import com.g01.connection.Connection;

public class EventoTransito extends Connection {
    public EventoTransito() {
        super("EVENTO_TRANSITO", "(Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao)");
    }
}