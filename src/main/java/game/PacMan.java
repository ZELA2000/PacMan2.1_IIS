package game;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

import java.io.File;
import java.util.Set;

import javafx.scene.input.KeyCode;

import javafx.scene.image.ImageView;

public class PacMan {
    private ImageView pacman;  // Rappresentazione grafica di Pac-Man come immagine
    private int row;  // Posizione attuale di Pac-Man
    private int col;  // Posizione attuale di Pac-Man
    private GridPane gridPane;

    public PacMan(GridPane gridPane, int startRow, int startCol) {
        this.gridPane = gridPane;
        this.row = startRow;
        this.col = startCol;

        // Carica l'immagine di Pac-Man
        Image pacmanImage = new Image(getClass().getResourceAsStream("pacman.png"));
        pacman = new ImageView(pacmanImage);
        pacman.setFitWidth(10);  // Dimensioni dell'immagine
        pacman.setFitHeight(10);

        // Posiziona Pac-Man nel GridPane
        GridPane.setRowIndex(pacman, row);
        GridPane.setColumnIndex(pacman, col);
        gridPane.getChildren().add(pacman);  // Aggiungi Pac-Man al GridPane
    }

    // Metodo per muovere Pac-Man
    public void move(KeyEvent event) {
        int newRow = row;
        int newCol = col;

        // Determina la nuova posizione in base al tasto premuto
        if (event.getCode().toString().equals("UP")) {
            newRow = Math.max(row - 1, 0);
            //giro l'immagine di pacman
            pacman.setRotate(270);
        } else if (event.getCode().toString().equals("DOWN")) {
            newRow = Math.min(row + 1, gridPane.getRowCount() - 1);
            //giro l'immagine di pacman
            pacman.setRotate(90);
        } else if (event.getCode().toString().equals("LEFT")) {
            newCol = Math.max(col - 1, 0);
            //giro l'immagine di pacman
            pacman.setRotate(180);
        } else if (event.getCode().toString().equals("RIGHT")) {
            newCol = Math.min(col + 1, gridPane.getColumnCount() - 1);
            //giro l'immagine di pacman
            pacman.setRotate(0);
        }

        // Muovi Pac-Man solo se la nuova posizione non è un muro
        if (!isWall(newRow, newCol)) {
            row = newRow;
            col = newCol;
            GridPane.setRowIndex(pacman, row);
            GridPane.setColumnIndex(pacman, col);
        }
    }


    // Controlla se Pac-Man ha toccato un fantasma
    public boolean checkCollision(Ghost ghost) {
        return row == ghost.getRow() && col == ghost.getCol();
    }

    // Controlla e raccoglie una moneta
    public boolean collectCoin() {
        Set<Node> monete = gridPane.lookupAll(".moneta");
        for (Node moneta : monete) {
            Integer monetaRow = GridPane.getRowIndex(moneta);
            Integer monetaCol = GridPane.getColumnIndex(moneta);
            if (monetaRow != null && monetaCol != null && monetaRow == row && monetaCol == col) {
                // Cambia la classe della moneta a "path"
                moneta.setStyle("-fx-fill: black;");  // Cambia il colore della moneta in bianco
                return true;  // Moneta raccolta
            }
        }
        return false;  // Nessuna moneta raccolta
    }

    // Controlla se c'è un muro nella nuova posizione
    private boolean isWall(int row, int col) {
        Set<Node> muri = gridPane.lookupAll(".muro");
        for (Node muro : muri) {
            Integer muroRow = GridPane.getRowIndex(muro);
            Integer muroCol = GridPane.getColumnIndex(muro);
            if (muroRow != null && muroCol != null && muroRow == row && muroCol == col) {
                return true; // C'è un muro
            }
        }
        return false; // Non c'è un muro
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
