package game;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    @FXML
    private AnchorPane root;

    @FXML
    private GridPane gridPane;

    private PacMan pacman;  // Rappresentazione di Pac-Man
    private List<Ghost> ghosts = new ArrayList<>();  // Lista dei fantasmi

    private volatile boolean gameRunning = true;  // Stato del gioco
    private boolean isGameStarted = false;  // Stato che indica se il gioco è iniziato
    private int totalCoins;  // Numero totale di monete
    private int coinsCollected = 0;  // Numero di monete raccolte

    @FXML
    public void initialize() {


        // Inizializza Pac-Man e posizionalo nel GridPane
        pacman = new PacMan(gridPane, 17, 15);  // Posizione iniziale di Pac-Man

        // Conta il numero totale di monete all'inizio del gioco
        totalCoins = countTotalCoins();

        // Aggiungi fantasmi e posizionali nel gridPane
        int startRow = 13;
        int startCol = 14;
        ghosts.add(new Ghost(gridPane, startRow, startCol));

        startRow = 13;
        startCol = 15;
        ghosts.add(new Ghost(gridPane, startRow, startCol));

        startRow = 12;
        startCol = 14;
        ghosts.add(new Ghost(gridPane, startRow, startCol));

        startRow = 12;
        startCol = 15;
        ghosts.add(new Ghost(gridPane, startRow, startCol));
    }

    // Conta tutte le monete presenti all'inizio del gioco
    private int countTotalCoins() {
        return gridPane.lookupAll(".moneta").size();
    }

    private void startGhostMovement() {
        // Crea un nuovo Thread per muovere i fantasmi
        new Thread(() -> {
            while (gameRunning) {
                for (Ghost ghost : ghosts) {
                    ghost.move(pacman.getRow(), pacman.getCol()); // Ogni fantasma si muove
                    checkCollision(ghost);  // Controlla se Pac-Man ha toccato il fantasma
                }
                try {
                    Thread.sleep(500);  // Attendere un po' prima di aggiornare la posizione
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Metodo per muovere Pac-Man e iniziare il gioco
    public void movePacman(KeyEvent event) {
        if (!gameRunning) return;  // Se il gioco è terminato, ignora gli input

        // Inizia il gioco se non è ancora partito
        if (!isGameStarted) {
            isGameStarted = true;
            startGhostMovement();  // Avvia il movimento dei fantasmi
        }

        pacman.move(event);  // Muovi Pac-Man

        // Controlla se Pac-Man ha toccato un fantasma
        for (Ghost ghost : ghosts) {
            if (pacman.checkCollision(ghost)) {
                checkCollision(ghost);  // Controlla se Pac-Man ha toccato il fantasma
            }
        }

        // Controlla se Pac-Man ha raccolto una moneta e aggiorna il conteggio
        if (pacman.collectCoin()) {
            coinsCollected++;
            if (coinsCollected == totalCoins) {
                winGame();  // Pac-Man ha raccolto tutte le monete
            }
        }
    }

    // Controlla se Pac-Man ha toccato un fantasma
    private void checkCollision(Ghost ghost) {
        if (pacman.getRow() == ghost.getRow() && pacman.getCol() == ghost.getCol()) {
            endGame();  // Termina il gioco se c'è una collisione
        }
    }

    // Metodo per terminare il gioco con sconfitta
    private void endGame() {
        gameRunning = false;  // Imposta lo stato del gioco a terminato

        // Mostra l'alert sul thread dell'applicazione JavaFX
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Pac-Man è stato preso! Game Over.");
            alert.showAndWait();

            // Chiudi l'applicazione dopo che l'utente ha chiuso l'alert
            Platform.exit();
        });
    }

    // Metodo per terminare il gioco con vittoria
    private void winGame() {
        gameRunning = false;  // Imposta lo stato del gioco a terminato

        // Mostra l'alert di vittoria
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hai vinto!");
            alert.setHeaderText(null);
            alert.setContentText("Congratulazioni! Hai raccolto tutte le monete!");
            alert.showAndWait();

            // Chiudi l'applicazione dopo che l'utente ha chiuso l'alert
            Platform.exit();
        });
    }
}
