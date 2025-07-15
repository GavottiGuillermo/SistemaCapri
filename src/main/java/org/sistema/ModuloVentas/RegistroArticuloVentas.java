package org.sistema.ModuloVentas;

public class RegistroArticuloVentas {
    private String estado;
    private String nombreArticulo;
    private String categoria;
    private String color;
    private String talle;
    private int idArticulo;
    private double precioEfectivo;
    private double precioTransferencia;

    // Constructor
    public RegistroArticuloVentas(int idArticulo, String estado, String nombreArticulo, String categoria,
                    String color, String talle, double precioEfectivo, double precioTransferencia) {
        this.idArticulo = idArticulo;
        this.estado = estado;
        this.nombreArticulo = nombreArticulo;
        this.categoria = categoria;
        this.color = color;
        this.talle = talle;
        this.precioEfectivo = precioEfectivo;
        this.precioTransferencia = precioTransferencia;
    }

    // Getters y Setters
    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTalle() {
        return talle;
    }

    public void setTalle(String talle) {
        this.talle = talle;
    }

    public double getPrecioEfectivo() {
        return precioEfectivo;
    }

    public void setPrecioEfectivo(double precioEfectivo) {
        this.precioEfectivo = precioEfectivo;
    }

    public double getPrecioTransferencia() {
        return precioTransferencia;
    }

    public void setPrecioTransferencia(double precioTransferencia) {
        this.precioTransferencia = precioTransferencia;
    }

}
