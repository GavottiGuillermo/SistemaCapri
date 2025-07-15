package org.sistema.ModuloCargaArchivo;

public class RegistroPago {
    private String fecha, nombreCliente, metodoPago;
    private double monto;

    public RegistroPago(String fecha, double monto, String nombreCliente, String metodoPago) {
        this.fecha = fecha;
        this.monto = monto;
        this.nombreCliente = nombreCliente;
        this.metodoPago = metodoPago;

    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
