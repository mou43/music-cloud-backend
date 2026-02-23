package com.tfg.music_cloud_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/*
    Su única responsabilidad es ejecutar scripts Python externos desde Java
    y devolver su salida como String.

    No contiene lógica de negocio, por eso no tiene interfaz.
    Es una capa de comunicación entre Spring Boot y el sistema operativo.

*/

@Service
@Getter
public class PythonService {


    /*
        Ruta a la carpeta scripts/ configurada en application.properties.
        Usamos @Value en vez de @AllArgsConstructor porque @Value inyecta
        un valor de configuración (String), no un bean de Spring.
        Lombok no sabe manejar @Value en constructores automáticos.
    */
    @Value("${python.scripts.path}")
    private String scriptsPath;

    // ObjectMapper se usa para convertir JSON en objetos Java
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String executeScript(List<String> args) {
        try {
            // Construimos el comando: python scripts/search.py "arg1" "arg2"...
            ProcessBuilder pb = new ProcessBuilder(args);
            pb.redirectErrorStream(true); // mezcla stdout y stderr
            Process process = pb.start();

            // Leemos la salida del script
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            // Buscamos la línea que empieza por '{' o '[' que es el JSON real
            String jsonLine = null;
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                    jsonLine = trimmed;
                }
            }

            process.waitFor();
            return jsonLine != null ? jsonLine : "{}"; // Devuelve la salida como un String (sera un JSON)

        } catch (Exception e) {
            throw new RuntimeException("Error executing Python script: " + e.getMessage());
        }
    }
}