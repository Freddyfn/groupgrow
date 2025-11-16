package com.groupgrow.groupgrow.dto;

public class MemberInfo {
    private String nombreMiembro;
    private double totalAportadoMiembro;
    private String fechaUltimoAporte;
    private String siguienteAporteCalculado;

    // Constructors
    public MemberInfo() {
    }

    public MemberInfo(String nombreMiembro, double totalAportadoMiembro, String fechaUltimoAporte, String siguienteAporteCalculado) {
        this.nombreMiembro = nombreMiembro;
        this.totalAportadoMiembro = totalAportadoMiembro;
        this.fechaUltimoAporte = fechaUltimoAporte;
        this.siguienteAporteCalculado = siguienteAporteCalculado;
    }

    // Getters and Setters
    public String getNombreMiembro() {
        return nombreMiembro;
    }

    public void setNombreMiembro(String nombreMiembro) {
        this.nombreMiembro = nombreMiembro;
    }

    public double getTotalAportadoMiembro() {
        return totalAportadoMiembro;
    }

    public void setTotalAportadoMiembro(double totalAportadoMiembro) {
        this.totalAportadoMiembro = totalAportadoMiembro;
    }

    public String getFechaUltimoAporte() {
        return fechaUltimoAporte;
    }

    public void setFechaUltimoAporte(String fechaUltimoAporte) {
        this.fechaUltimoAporte = fechaUltimoAporte;
    }

    public String getSiguienteAporteCalculado() {
        return siguienteAporteCalculado;
    }

    public void setSiguienteAporteCalculado(String siguienteAporteCalculado) {
        this.siguienteAporteCalculado = siguienteAporteCalculado;
    }
}
