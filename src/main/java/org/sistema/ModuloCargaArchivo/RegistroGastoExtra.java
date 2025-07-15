package org.sistema.ModuloCargaArchivo;

public class RegistroGastoExtra {
    private String fecha, inversor, observacion;
    private double monto;


    public RegistroGastoExtra(String fecha, double monto, String inversor, String observacion) {
        this.fecha = fecha;
        this.monto = monto;
        this.inversor = inversor;
        this.observacion = observacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getInversor() {
        return inversor;
    }

    public void setInversor(String inversor) {
        this.inversor = inversor;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
