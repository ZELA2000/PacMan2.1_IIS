package game;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.*;

public class Ghost {
    private ImageView ghost;
    private int row;  // Posizione attuale del fantasma
    private int col;  // Posizione attuale del fantasma
    private int startRow;  // Posizione di partenza
    private int startCol;  // Posizione di partenza
    private GridPane gridPane;
    private Color originalColor = Color.RED;  // Colore originale del fantasma
    private boolean isRunningAway = false;  // Stato di fuga
    private boolean isEaten = false;  // Stato del fantasma se viene mangiato
    private Random random = new Random();
    private int targetRow;  // Coordinata riga del punto casuale
    private int targetCol;  // Coordinata colonna del punto casuale

    public Ghost(GridPane gridPane, int startRow, int startCol) {
        this.gridPane = gridPane;
        this.startRow = startRow;
        this.startCol = startCol;
        this.row = startRow;
        this.col = startCol;

        // Crea il fantasma
        Image ghostImage = new Image(getClass().getResourceAsStream("ghost.png"));
        ghost = new ImageView(ghostImage);
        ghost.setFitWidth(10);  // Dimensioni dell'immagine
        ghost.setFitHeight(10);

        // Posiziona il fantasma nel GridPane
        GridPane.setRowIndex(ghost, row);
        GridPane.setColumnIndex(ghost, col);
        gridPane.getChildren().add(ghost);

        // Imposta un punto casuale iniziale
        generateRandomTarget();
    }

    // Muove il fantasma normalmente o scappa da Pac-Man
    public void move(int pacmanRow, int pacmanCol) {
        if (isEaten) return;  // Se il fantasma è stato mangiato, non si muove

        // Calcola la distanza da Pac-Man
        int distance = Math.abs(row - pacmanRow) + Math.abs(col - pacmanCol);
        if (distance <= 10) {
            // Se Pac-Man è entro 10 blocchi, inseguirlo usando A*
            moveTowards(pacmanRow, pacmanCol);
        } else {
            // Altrimenti, muoversi verso il punto casuale usando A*
            moveTowards(targetRow, targetCol);

            // Se il fantasma ha raggiunto il punto casuale, selezionane un altro
            if (row == targetRow && col == targetCol) {
                generateRandomTarget();
            }
        }
    }

    // Genera un nuovo punto casuale sulla mappa
    private void generateRandomTarget() {
        int rows = gridPane.getRowCount();
        int cols = gridPane.getColumnCount();
        do {
            targetRow = random.nextInt(rows);
            targetCol = random.nextInt(cols);
        } while (isWall(targetRow, targetCol));  // Assicurati che il target non sia un muro
    }

    // Muove il fantasma verso una destinazione (riga e colonna) usando A*
    private void moveTowards(int destRow, int destCol) {
        List<int[]> path = astar(row, col, destRow, destCol);
        if (!path.isEmpty()) {
            // Muove verso il primo passo del percorso trovato
            int[] nextStep = path.get(0);
            row = nextStep[0];
            col = nextStep[1];
            updatePosition();
        }
    }

    // Resetta il colore del fantasma e lo mette in modalità normale
    public void deactivatePowerUpMode() {
        isRunningAway = false;
    }

    // Viene chiamato quando il fantasma viene mangiato da Pac-Man
    public void getEaten() {
        isEaten = true;
        Platform.runLater(() -> gridPane.getChildren().remove(ghost));  // Rimuove il fantasma dalla griglia

        // Respawn dopo 5 secondi
        new Thread(() -> {
            try {
                Thread.sleep(5000);  // Aspetta 5 secondi prima di far respawnare il fantasma
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(this::respawn);  // Respawn nel thread FX
        }).start();
    }

    // Respawn del fantasma nel punto di origine
    public void respawn() {
        isEaten = false;
        row = startRow;
        col = startCol;
        Platform.runLater(() -> {
            GridPane.setRowIndex(ghost, row);
            GridPane.setColumnIndex(ghost, col);
            gridPane.getChildren().add(ghost);  // Riaggiunge il fantasma al GridPane
        });
    }

    // Implementazione dell'algoritmo A* (resta invariato)
    private List<int[]> astar(int startRow, int startCol, int targetRow, int targetCol) {
        PriorityQueue<NodeData> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<String> closedSet = new HashSet<>();
        NodeData startNode = new NodeData(startRow, startCol, 0, heuristic(startRow, startCol, targetRow, targetCol));
        openSet.add(startNode);
        Map<String, String> cameFrom = new HashMap<>();

        while (!openSet.isEmpty()) {
            NodeData current = openSet.poll();
            if (current.row == targetRow && current.col == targetCol) {
                return reconstructPath(cameFrom, current.row, current.col);
            }
            closedSet.add(current.row + "," + current.col);

            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int newRow = current.row + direction[0];
                int newCol = current.col + direction[1];

                // Controlla se la nuova posizione è valida
                if (isWall(newRow, newCol) || closedSet.contains(newRow + "," + newCol)) {
                    continue;
                }

                int tentativeGCost = current.gCost + 1;
                int hCost = heuristic(newRow, newCol, targetRow, targetCol);
                NodeData neighbor = new NodeData(newRow, newCol, tentativeGCost, tentativeGCost + hCost);

                openSet.add(neighbor);
                cameFrom.put(newRow + "," + newCol, current.row + "," + current.col);
            }
        }

        return new ArrayList<>();
    }

    // Funzione euristica (distanza di Manhattan)
    private int heuristic(int row, int col, int targetRow, int targetCol) {
        return Math.abs(row - targetRow) + Math.abs(col - targetCol);
    }

    // Ricostruzione del percorso
    private List<int[]> reconstructPath(Map<String, String> cameFrom, int row, int col) {
        List<int[]> path = new ArrayList<>();
        String current = row + "," + col;

        while (cameFrom.containsKey(current)) {
            String[] coords = current.split(",");
            path.add(0, new int[]{Integer.parseInt(coords[0]), Integer.parseInt(coords[1])});
            current = cameFrom.get(current);
        }

        return path;
    }

    // Aggiorna la posizione del fantasma nel GridPane
    private void updatePosition() {
        GridPane.setRowIndex(ghost, row);
        GridPane.setColumnIndex(ghost, col);
    }

    // Controlla se c'è un muro nella nuova posizione
    private boolean isWall(int row, int col) {
        // Verifica che gli indici siano all'interno dei limiti della griglia
        if (row < 0 || col < 0 || row >= gridPane.getRowCount() || col >= gridPane.getColumnCount()) {
            return true; // Considera fuori dai limiti come muro
        }

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

    public boolean isRunningAway() {
        return isRunningAway;
    }

    // Classe per memorizzare i dati dei nodi per l'algoritmo A*
    private static class NodeData {
        int row, col;
        int gCost;  // Costo dal nodo iniziale
        int fCost;  // Somma di gCost e heuristic

        public NodeData(int row, int col, int gCost, int fCost) {
            this.row = row;
            this.col = col;
            this.gCost = gCost;
            this.fCost = fCost;
        }
    }
}
