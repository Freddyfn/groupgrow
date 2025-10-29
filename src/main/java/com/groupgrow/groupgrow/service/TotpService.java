package com.groupgrow.groupgrow.service; // Asegúrate que el paquete sea correcto

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.stereotype.Service; // Asegúrate de tener esta importación
import java.util.Base64;

@Service // ¡Importante! Para que Spring lo reconozca como un servicio
public class TotpService {

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(64); // Generador de secretos
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator(); // Generador de QR
    private final CodeVerifier codeVerifier; // Verificador de códigos

    // Constructor: Inicializa el verificador de códigos
    public TotpService() {
        TimeProvider timeProvider = new SystemTimeProvider();
        // Usamos el algoritmo SHA1 y códigos de 6 dígitos (estándar)
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
        // El verificador permite una pequeña desviación de tiempo
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    /**
     * Genera un nuevo secreto TOTP único para un usuario.
     * @return El secreto como un String (ej. "JBSWY3DPEHPK3PXP")
     */
    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    /**
     * Genera la imagen QR como un string Base64 que se puede usar en una etiqueta <img>.
     * @param email El email del usuario (se muestra en la app autenticadora).
     * @param secret El secreto TOTP generado para este usuario.
     * @return El string Base64 de la imagen PNG (ej. "data:image/png;base64,iVBORw0KG...") o null si hay error.
     */
    public String getQrCodeAsBase64(String email, String secret) {
        // Prepara los datos para el QR según el estándar otpauth://
        QrData data = new QrData.Builder()
                .label(email) // Etiqueta que se muestra en la app
                .secret(secret) // El secreto
                .issuer("GroupGrow") // Nombre de tu aplicación
                .algorithm(HashingAlgorithm.SHA1) // Algoritmo (debe coincidir con el CodeGenerator)
                .digits(6) // Número de dígitos (debe coincidir con el CodeGenerator)
                .period(30) // Período de validez del código en segundos
                .build();
        try {
            // Genera los bytes de la imagen PNG
            byte[] imageData = qrGenerator.generate(data);
            // Convierte los bytes a un string Base64 con el prefijo correcto para HTML/CSS
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
        } catch (QrGenerationException e) {
            // Loggea el error si falla la generación del QR
            System.err.println("Error al generar QR: " + e.getMessage());
            e.printStackTrace();
            return null; // Devuelve null si no se pudo generar
        }
    }

    /**
     * Verifica si un código OTP (ingresado por el usuario) es válido para un secreto dado.
     * @param secret El secreto TOTP del usuario guardado en la base de datos.
     * @param code El código de 6 dígitos ingresado por el usuario desde su app.
     * @return true si el código es válido (considerando la ventana de tiempo), false si no.
     */
    public boolean verifyCode(String secret, String code) {
        // Usa el CodeVerifier para comprobar si el código es correcto
        return codeVerifier.isValidCode(secret, code);
    }
}