package com.groupgrow.groupgrow.dto;

import java.util.Date;

public class MemberStatusDto {
    private Long userId;
    private String nombreMiembro;
    private double totalAportadoEnGrupo;
    private Date fechaUltimoAporte;
    private String frecuenciaDeAporte;

    public MemberStatusDto(Long userId, String nombreMiembro, double totalAportadoEnGrupo, Date fechaUltimoAporte, String frecuenciaDeAporte) {
        this.userId = userId;
        this.nombreMiembro = nombreMiembro;
        this.totalAportadoEnGrupo = totalAportadoEnGrupo;
        this.fechaUltimoAporte = fechaUltimoAporte;
        this.frecuenciaDeAporte = frecuenciaDeAporte;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNombreMiembro() {
        return nombreMiembro;
    }

    public void setNombreMiembro(String nombreMiembro) {
        this.nombreMiembro = nombreMiembro;
    }

    public double getTotalAportadoEnGrupo() {
        return totalAportadoEnGrupo;
    }

    public void setTotalAportadoEnGrupo(double totalAportadoEnGrupo) {
        this.totalAportadoEnGrupo = totalAportadoEnGrupo;
    }

    public Date getFechaUltimoAporte() {
        return fechaUltimoAporte;
    }

    public void setFechaUltimoAporte(Date fechaUltimoAporte) {
        this.fechaUltimoAporte = fechaUltimoAporte;
    }

    public String getFrecuenciaDeAporte() {
        return frecuenciaDeAporte;
    }

    public void setFrecuenciaDeAporte(String frecuenciaDeAporte) {
        this.frecuenciaDeAporte = frecuenciaDeAporte;
    }
}
