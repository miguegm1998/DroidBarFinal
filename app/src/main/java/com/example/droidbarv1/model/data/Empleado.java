package com.example.droidbarv1.model.data;

public class Empleado {
    private long id;
    private String login;
    private String password;

    public Empleado(){
        this(0,"","");
    }

    public Empleado(long id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password=password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
