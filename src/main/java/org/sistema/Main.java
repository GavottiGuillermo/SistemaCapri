package org.sistema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Front Capri Ropas.fxml"));
            Scene scene = new Scene(root);

            // Obtener la resolución de pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();

            // Ajustar el tamaño de la ventana
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenWidth);
            primaryStage.setHeight(screenHeight);

            // Opcional: o simplemente maximizar
            // primaryStage.setMaximized(true);

            primaryStage.setTitle("Sistema Capri Store");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Application.launch(Main.class, args);
        } catch (Exception e) {
            System.err.println("❌ Error crítico al iniciar la aplicación.");
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "La aplicación no pudo iniciarse. Consulte el registro de errores.", Alert.AlertType.ERROR);
        }
    }
    private static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}