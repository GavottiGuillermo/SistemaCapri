package org.sistema.ModuloCargaArchivo;

public class RegistroCliente {
    private String nombre, correo, instagram;

    public RegistroCliente(String nombre, String correo, String instagram) {
        this.nombre = nombre;
        this.correo = correo;
        this.instagram = instagram;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
