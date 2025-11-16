package com.groupgrow.groupgrow.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class FrontendController {

    /**
     * Sirve el index.html para todas las rutas que no sean API
     * Esto permite que React Router maneje el enrutamiento del lado del cliente
     * 
     * Este método captura todas las rutas que no empiezan con /api/ y no son recursos estáticos
     */
    @GetMapping(value = {
        "/",
        "/my-groups",
        "/groups",
        "/create-group",
        "/login",
        "/register",
        "/dashboard",
        "/profile",
        "/resources",
        "/activate-2fa"
    }, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> serveFrontend() {
        return getIndexHtml();
    }

    /**
     * Captura rutas dinámicas como /edit-group/{id}, /group/{id}, y /make-payment/{id}
     */
    @GetMapping(value = {
        "/edit-group/**",
        "/group/**",
        "/make-payment/**"
    }, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> serveFrontendDynamic() {
        return getIndexHtml();
    }

    private ResponseEntity<String> getIndexHtml() {
        try {
            Resource resource = new ClassPathResource("static/index.html");
            InputStream inputStream = resource.getInputStream();
            String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

