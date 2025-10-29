package com.groupgrow.groupgrow.dto;

// Esta clase transportará la imagen del QR al frontend
public class QrResponse {
    private String qrCodeBase64; // La imagen del QR como string Base64

    public QrResponse(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

    // Getter y Setter
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
}