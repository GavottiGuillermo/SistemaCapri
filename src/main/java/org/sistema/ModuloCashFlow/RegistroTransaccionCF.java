package org.sistema.ModuloCashFlow;

import java.lang.reflect.Array;

public class RegistroTransaccionCF {
    private Double debe;
    private Double haber;
    private Double saldo;
    private String fechaTransaccion, observacion;
    private java.sql.Array analisisArray;



    public RegistroTransaccionCF(String fechaTransaccion, Double debe, Double haber, Double saldo, String observacion, java.sql.Array analisisArray) {
        this.fechaTransaccion = fechaTransaccion;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
        this.observacion = observacion;
        this.analisisArray = analisisArray;
    }

    public String getFechaTransaccion() { return fechaTransaccion; }
    public Double getDebe() { return debe; }
    public Double getHaber() { return haber; }
    public Double getSaldo() { return saldo; }
    public String getObservacion() { return observacion; }
    public java.sql.Array getAnalisisArray() {
        return analisisArray;
    }

    public void setAnalisisArray(java.sql.Array analisisArray) {
        this.analisisArray = analisisArray;
    }
}

