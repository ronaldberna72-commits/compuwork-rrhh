package com.compuwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Punto de entrada principal de la aplicación gráfica JavaFX
 * para el Sistema de Gestión de RRHH de CompuWork.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/vista/main.fxml"),
                        "No se encontró el archivo /vista/main.fxml")
        );
        primaryStage.setTitle("CompuWork - Sistema de Gestión de RRHH");
        primaryStage.setScene(new Scene(root, 980, 680));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(620);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
