package com.g01;

public class ConnectionInfo {
    private final String url;
    private final String user;
    private final String password;

    public ConnectionInfo(String database, String user, String password) {
        this.url = "jdbc:postgresql://localhost:5432/postgres?currentSchema=" + database;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}