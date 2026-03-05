package org.sistema.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Centraliza la configuración de la conexión a PostgreSQL para permitir reutilizar la app con otros esquemas.
 */
public final class DatabaseConfig {

    private static final String PROPERTIES_FILE = "database.properties";
    private static final Map<String, String> ENV_MAPPING = buildEnvMapping();
    private static final Properties PROPERTIES = loadProperties();

    private DatabaseConfig() {
    }

    private static Map<String, String> buildEnvMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("db.host", "DB_HOST");
        map.put("db.port", "DB_PORT");
        map.put("db.name", "DB_NAME");
        map.put("db.user", "DB_USER");
        map.put("db.password", "DB_PASSWORD");
        map.put("db.schema", "DB_SCHEMA");
        map.put("db.sslmode", "DB_SSLMODE");
        map.put("db.applicationName", "DB_APPLICATION_NAME");
        map.put("db.passwordCredentialTarget", "DB_PASSWORD_CREDENTIAL");
        return map;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró el archivo " + PROPERTIES_FILE + " en resources");
            }
            props.load(in);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar la configuración de base de datos", ex);
        }

        // Permite sobrescribir con variables de entorno para despliegues específicos
        overrideFromEnvironment(props);
        return props;
    }

    private static void overrideFromEnvironment(Properties props) {
        ENV_MAPPING.forEach((propertyKey, envKey) -> {
            String envValue = System.getenv(envKey);
            if (envValue != null && !envValue.isBlank()) {
                props.setProperty(propertyKey, envValue.trim());
            }
        });
    }

    private static String require(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("La propiedad obligatoria '" + key + "' no está configurada");
        }
        return value.trim();
    }

    /**
     * Crea una nueva conexión usando los parámetros centralizados y aplicando el esquema deseado.
     */
    public static Connection createConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el controlador PostgreSQL", e);
        }

        String host = require("db.host");
        String port = PROPERTIES.getProperty("db.port", "5432").trim();
        String dbName = require("db.name");
        String schema = require("db.schema");

        // Cambiar la obtención de usuario y contraseña
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbUser == null || dbUser.isBlank()) {
            throw new IllegalStateException("La variable de entorno DB_USER no está definida o está vacía");
        }
        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException("La variable de entorno DB_PASSWORD no está definida o está vacía");
        }

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);

        Properties jdbcProps = new Properties();
        jdbcProps.setProperty("user", dbUser);
        jdbcProps.setProperty("password", dbPassword);
        jdbcProps.setProperty("currentSchema", schema);

        String sslMode = PROPERTIES.getProperty("db.sslmode");
        if (sslMode != null && !sslMode.isBlank()) {
            jdbcProps.setProperty("sslmode", sslMode.trim());
        }

        String appName = PROPERTIES.getProperty("db.applicationName");
        if (appName != null && !appName.isBlank()) {
            jdbcProps.setProperty("ApplicationName", appName.trim());
        }

        return DriverManager.getConnection(url, jdbcProps);
    }

    public static String getSchema() {
        return require("db.schema");
    }

    public static Map<String, String> getCurrentConfigSnapshot() {
        Map<String, String> snapshot = new HashMap<>();
        PROPERTIES.forEach((key, value) -> snapshot.put(Objects.toString(key), Objects.toString(value)));
        snapshot.replace("db.password", "********");
        return snapshot;
    }
}
