package org.sistema;
import java.io.File;
import java.nio.file.Paths;

public class GoogleCloudConfig {
        public static void configureGoogleCredentials() {
            String rutaCredenciales = Paths.get("src/main/resources/capri-store-4220e6ade3c1.json").toAbsolutePath().toString();
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", rutaCredenciales);
            System.out.println("Credenciales configuradas: " + rutaCredenciales);
        }

}
