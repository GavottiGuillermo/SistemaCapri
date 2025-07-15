package org.sistema.ModuloCargaArchivo;

public class RegistroLote {


    private String fecha, local, inversor;
    private double costo;

    private int id_lote;
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public String getInversor() {
        return inversor;
    }

    public void setInversor(String inversor) {
        this.inversor = inversor;
    }


    public RegistroLote(int id_lote, String celdaFechaString, double celdaCostoString, String celdaLocalString, String celdaInversorString) {
        this.id_lote = id_lote;
        this.fecha = fecha;
        this.costo = costo;
        this.local = local;
        this.inversor = inversor;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
    public int getId_lote() {
        return id_lote;
    }

    public void setId_lote(int id_lote) {
        this.id_lote = id_lote;
    }
}
