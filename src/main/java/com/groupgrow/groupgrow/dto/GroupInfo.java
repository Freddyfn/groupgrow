package com.groupgrow.groupgrow.dto;

public class GroupInfo {
    private String nombreGrupo;
    private String tipoGrupo; // "saving" o "investment"
    private double metaGrupo;
    private double actualGrupo;
    private double porcentajeProgreso;
    private double contribucionMinima;
    private String frecuenciaContribucion; // "weekly", "monthly", "quarterly", "flexible"
    private String fechaLimite; // Deadline del grupo
    private int diasRestantes; // Días hasta el deadline
    private int totalMiembros;

    // Constructors
    public GroupInfo() {
    }

    public GroupInfo(String nombreGrupo, String tipoGrupo, double metaGrupo, double actualGrupo, 
                     double porcentajeProgreso, double contribucionMinima, String frecuenciaContribucion,
                     String fechaLimite, int diasRestantes, int totalMiembros) {
        this.nombreGrupo = nombreGrupo;
        this.tipoGrupo = tipoGrupo;
        this.metaGrupo = metaGrupo;
        this.actualGrupo = actualGrupo;
        this.porcentajeProgreso = porcentajeProgreso;
        this.contribucionMinima = contribucionMinima;
        this.frecuenciaContribucion = frecuenciaContribucion;
        this.fechaLimite = fechaLimite;
        this.diasRestantes = diasRestantes;
        this.totalMiembros = totalMiembros;
    }

    // Getters and Setters
    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public double getMetaGrupo() {
        return metaGrupo;
    }

    public void setMetaGrupo(double metaGrupo) {
        this.metaGrupo = metaGrupo;
    }

    public double getActualGrupo() {
        return actualGrupo;
    }

    public void setActualGrupo(double actualGrupo) {
        this.actualGrupo = actualGrupo;
    }

    public double getPorcentajeProgreso() {
        return porcentajeProgreso;
    }

    public void setPorcentajeProgreso(double porcentajeProgreso) {
        this.porcentajeProgreso = porcentajeProgreso;
    }

    public String getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(String tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public double getContribucionMinima() {
        return contribucionMinima;
    }

    public void setContribucionMinima(double contribucionMinima) {
        this.contribucionMinima = contribucionMinima;
    }

    public String getFrecuenciaContribucion() {
        return frecuenciaContribucion;
    }

    public void setFrecuenciaContribucion(String frecuenciaContribucion) {
        this.frecuenciaContribucion = frecuenciaContribucion;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public int getTotalMiembros() {
        return totalMiembros;
    }

    public void setTotalMiembros(int totalMiembros) {
        this.totalMiembros = totalMiembros;
    }
}
