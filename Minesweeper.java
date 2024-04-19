import java.util.Scanner;

public class Minesweeper{

    public Grid answerKey, displayedGrid;
    boolean playGame, gameOver;
    int score;

    public Minesweeper(int n, int k) {
        answerKey = new Grid(n);
        answerKey.generateAnswerKey(answerKey.getStringGrid(), n, k);
        displayedGrid = new Grid(n);
        displayedGrid.generateDisplayedGrid(displayedGrid.getStringGrid(), n);
        displayedGrid.printGrid(displayedGrid.getStringGrid());
        this.playGame = true;
        this.gameOver = false;
        this.score = 0;
    }
    public void playGameLoop(int n, int k, Scanner scanner) {
        while (!gameOver) {
            String action = promptAction(scanner);
            handleAction(action, n, k, scanner);
        }
    }

    private String promptAction(Scanner scanner) {
        System.out.print("Choose action (g: guess, f: flag, r: remove flag): ");
        return scanner.next().toLowerCase();
    }

    private void handleAction(String action, int n, int k, Scanner scanner) {
        switch (action) {
            case "g":
            case "f":
            case "r":
                handleCellSelection(action, n, k, scanner);
                break;
            case "q":
                quitGame();
                break;
            case "s":
                restartGame();
                break;
            default:
                System.out.println("Please enter a valid response ('g', 'f', 'r', 'q', or 's').");
                break;
        }
    }

    private void handleCellSelection(String action, int n, int k, Scanner scanner) {
        int row = 0, col = 0;
        boolean validInput = false;
        do {
            String optionOrRowCol = getOptionOrRowCol(scanner);
    
            if (optionOrRowCol.equals("b")) {
                action = "b";
                validInput = true;
            } else {
                try {
                    // If "b" was not entered, assume it's the row and column inputs
                    row = Integer.parseInt(optionOrRowCol);
                    col = Integer.parseInt(scanner.next());
    
                    validInput = validateInput(scanner, action, row, col, n, k);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter integers for row and column.");
                    scanner.nextLine();
                }
            }
        } while (!validInput);
    
        executeAction(action, row, col, n, k);
    }
    
    private String getOptionOrRowCol(Scanner scanner) {
        System.out.print("Enter 'b' to reselect your option, or select the cell by entering the row and column: ");
        return scanner.next().toLowerCase();
    }
    
    private boolean validateInput(Scanner scanner, String action, int row, int col, int n, int k) {
        if (isWithinBounds(row, col, n)) {
            String cellValue = displayedGrid.getStringGrid(row, col);
            if (cellValue.equals(" #  ")) {
                return handleHiddenCell(action);
            } else if (cellValue.equals(" ?  ")) {
                return handleFlaggedCell(action, scanner);
            } else {
                System.out.println("Previously selected cell!");
            }
        } else {
            System.out.println("Invalid row or column!");
        }
        return false;
    }
    
    private boolean isWithinBounds(int row, int col, int n) {
        return row >= 0 && row < n && col >= 0 && col < n;
    }
    
    private boolean handleHiddenCell(String action) {
        if (action.equals("g") || action.equals("f")) {
            return true;
        } else if (action.equals("r")) {
            System.out.println("There is no flag on that cell to remove!");
        }
        return false;
    }
    
    private boolean handleFlaggedCell(String action, Scanner scanner) {
        if (action.equals("r")) {
            return true;
        } else if (action.equals("g")) {
            if (askToRemoveFlag(scanner)) {
                return true;
            }
        } else {
            System.out.println("There is already a flag on that cell!");
        }
        return false;
    }
    
    
    private void executeAction(String action, int row, int col, int n, int k) {
        if (action.equals("f")) {
            flagCell(row, col, k);
        } else if (action.equals("r")) {
            removeFlag(row, col, k);
        } else if (answerKey.getStringGrid(row, col).equals(" *  ")) {
            handleBombHit();
        } else if (action.equals("b")) {
            // Do nothing
        } else {
            revealCell(action, row, col, n, k);
        }
    }
    
    private void flagCell(int row, int col, int k) {
        if (k > 0) {
            // Mark the cell with a flag
            displayedGrid.setStringGrid(row, col, " ?  ");
            displayedGrid.printGrid(displayedGrid.getStringGrid());
            k--;
            System.out.println("Score: " + score);
            System.out.println("Bombs/Flags Remaining: " + k);
        } else {
            System.out.println("You need to remove a flag before adding another one");
            System.out.println("Choose another option:");
        }
    }
    
    private void removeFlag(int row, int col, int k) {
        displayedGrid.setStringGrid(row, col, " #  ");
        k++;
        displayedGrid.printGrid(displayedGrid.getStringGrid());
    }
    
    private void handleBombHit() {
        System.out.println("Game Over! You hit a bomb.");
        displayedGrid.addBombsToGrid(displayedGrid.getStringGrid(), answerKey.getStringGrid());
        displayedGrid.printGrid(displayedGrid.getStringGrid());
        
        restartGame();
    }
    
    private void revealCell(String action, int row, int col, int n, int k) {
        updateDisplayedGrid(row, col);
        checkGameStatus(row, col, n, k);
    }
    
    private void updateDisplayedGrid(int row, int col) {
        displayedGrid.setStringGrid(row, col, answerKey.getStringGrid(row, col));
        displayedGrid.checkSurroundingCells(answerKey.getStringGrid(), displayedGrid.getStringGrid(), row, col);
        score++;
    }
    
    private void checkGameStatus(int row, int col, int n, int k) {
        if (displayedGrid.checkWinCondition(displayedGrid.getStringGrid(), answerKey.getStringGrid())) {
            handleGameWin();
        } else {
            printGameStatus(row, col, k);
        }
    }
    
    private void handleGameWin() {
        System.out.println("Congratulations! You've won the game!");
        displayedGrid.printGrid(answerKey.getStringGrid());
        quitGame();
    }
    
    private void printGameStatus(int row, int col, int k) {
        displayedGrid.printGrid(displayedGrid.getStringGrid());
        System.out.println("Score: " + score);
        System.out.println("Bombs/Flags Remaining: " + k);
    }
    
    
    private void quitGame() {
        System.out.println("Score: " + score);
        System.out.println("Come back soon!");
        playGame = false;
        gameOver = true;
    }

    private void restartGame() {
        System.out.println("Score: " + score);
        System.out.println("\n\n\nNew Game!");
        score = 0;
        gameOver = true;
    }

    public static boolean askToRemoveFlag(Scanner scanner) {
        System.out.println("Remove flag before guessing cell");
        System.out.println("Would you like to remove the flag? y/n");
        String response = scanner.next().toLowerCase();
        if (response.equals("y")) {
            return true;
        } else {
            System.out.println("Choose a different cell");
            return false;
        }
    }
    
    public static int getInput(String message, Scanner scanner) {
        System.out.print(message);
        String input = scanner.next().toLowerCase();
        if (input.equals("q")) {
            System.out.println("Come back soon!");
            System.exit(0); // Exit the program
        } else if (input.equals("s")) {
            System.out.println("New Game!");
            return -1; // Signal to restart the game
        } 
        return Integer.parseInt(input);
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Whenever you would like, type 'q' to quit the game and 's' to start a new game.");
                
        Minesweeper game = null; // Initialize game outside the loop

        while (game == null || game.playGame)  {
            int n = getInput("Enter the size of the n x n grid (type a number): ", scanner);
            if (n == -1) {
                continue; // Restart the game
            }

            int k = getInput("Enter the number of bombs: ", scanner);
            if (k == -1) {
                continue; // Restart the game
            } else if(k >= n * n) {
                System.out.println("More Bombs than cells! Try again.");
                continue; // Restart the game
            }

            game = new Minesweeper(n, k);
            game.playGameLoop(n, k, scanner);            
        }
        scanner.close();
    }
}
