package com.epumer.gestiondeincidencias;

public class Incidencia {

    String descripcion;
    String aula;
    String urlImagen;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;
    boolean resuelta;

    public Incidencia() {
    }

    public Incidencia(String descripcion, String aula, String urlImagen, boolean resuelta) {
        this.descripcion = descripcion;
        this.aula = aula;
        this.urlImagen = urlImagen;
        this.resuelta = resuelta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public void setResuelta(boolean resuelta) {
        this.resuelta = resuelta;
    }
}
