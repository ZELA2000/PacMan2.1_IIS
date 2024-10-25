package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.io.IOException;

public class Game extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Carica il file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("hello-view.fxml"));

        // Carica il contenuto FXML
        GridPane gridPane = fxmlLoader.load();  // Carica il GridPane dalla vista

        // Crea la scena e imposta il GridPane
        Scene scene = new Scene(gridPane, 600, 600);

        // Binding per scalare il GridPane in base alla dimensione della scena
        Scale scaleTransform = new Scale();
        gridPane.getTransforms().add(scaleTransform);
        scaleTransform.xProperty().bind(scene.widthProperty().divide(300));
        scaleTransform.yProperty().bind(scene.heightProperty().divide(300));

        // Imposta il titolo della finestra
        stage.setTitle("Pac-Man Game");

        // Associa la scena allo stage e mostra la finestra
        stage.setScene(scene);
        stage.show();

        // Dopo aver mostrato la scena, possiamo gestire gli eventi di tastiera
        scene.setOnKeyPressed(event -> {
            // Ottieni il controller associato
            GameController controller = fxmlLoader.getController();
            // Esegui la funzione per muovere Pac-Man basata sull'input
            controller.movePacman(event);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
