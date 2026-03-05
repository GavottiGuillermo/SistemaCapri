package org.sistema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GoogleCloudConfig {
    public static void configureGoogleCredentials() {
        try {
            // Load the credential file from the classpath
            InputStream inputStream = GoogleCloudConfig.class.getResourceAsStream("/capri-store-4220e6ade3c1.json");
            if (inputStream == null) {
                throw new RuntimeException("Unable to find credential file in classpath");
            }

            // Create a temporary file to store the credential JSON
            File tempFile = Files.createTempFile("google-credentials", ".json").toFile();
            tempFile.deleteOnExit(); // Ensure the temp file is deleted on exit

            // Copy the content of the credential file to the temporary file
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Set the system property to point to the temporary file
            System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", tempFile.getAbsolutePath());
            System.out.println("Credenciales configuradas: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to configure Google credentials", e);
        }
    }
}
