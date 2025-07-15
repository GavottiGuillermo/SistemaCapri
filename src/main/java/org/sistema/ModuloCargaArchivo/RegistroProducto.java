package org.sistema.ModuloCargaArchivo;

public class RegistroProducto {
    private String estado, nombrePrenda, categoria, color, talle;
    private double precioCompra, precioVentaEfectivo, precioVentaTransf;



    private int id_lote;

    public RegistroProducto(String estado,
                            String nombrePrenda,
                            String categoria,
                            String color,
                            String talle,
                            double precioCompra,
                            double precioVentaEfectivo,
                            double precioVentaTransf,
                            int id_lote) {
        this.estado = estado;
        this.nombrePrenda = nombrePrenda;
        this.categoria = categoria;
        this.color = color;
        this.talle = talle;
        this.precioCompra = precioCompra;
        this.precioVentaEfectivo = precioVentaEfectivo;
        this.precioVentaTransf = precioVentaTransf;
        this.id_lote = id_lote;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombrePrenda() {
        return nombrePrenda;
    }

    public void setNombrePrenda(String nombrePrenda) {
        this.nombrePrenda = nombrePrenda;
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

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVentaEfectivo() {
        return precioVentaEfectivo;
    }

    public void setPrecioVentaEfectivo(double precioVentaEfectivo) {
        this.precioVentaEfectivo = precioVentaEfectivo;
    }

    public double getPrecioVentaTransf() {
        return precioVentaTransf;
    }

    public void setPrecioVentaTransf(double precioVentaTransf) {
        this.precioVentaTransf = precioVentaTransf;
    }

    public int getId_lote() {
        return id_lote;
    }

    public void setId_lote(int id_lote) {
        this.id_lote = id_lote;
    }
}
