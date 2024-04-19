public class Grid
{
    String[][] stringGrid;
    
    // Constructor for a grid of strings.  
    public Grid(int n) {
        this.stringGrid = new String[n][n];
    }

    public String[][] getStringGrid() 
    {
        return this.stringGrid;
    }
    public String getStringGrid(int row, int col) 
    {
        return this.stringGrid[row][col];
    }
    public void setStringGrid(int row, int col, String value)
    {
        this.stringGrid[row][col] = value;
    }

    public String[][] generateAnswerKey(String[][] hiddenGrid, int n, int k) {   
        // Initialize with empty strings
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                hiddenGrid[i][j] = " ";
            }
        }
    
        // Place bombs randomly
        int bombsPlaced = 0;
        while (bombsPlaced < k) {
            int randomRow = (int) (Math.random() * n);
            int randomCol = (int) (Math.random() * n);
    
            // Check if the cell is empty before placing a bomb
            if (hiddenGrid[randomRow][randomCol].equals(" ")) {
                hiddenGrid[randomRow][randomCol] = " *  ";
                bombsPlaced++;
            }
        }
    
        // Calculate neighboring bomb counts and update the grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (hiddenGrid[i][j].equals(" ")) { // Check if cell is still empty
                    int count = countNeighboringBombs(hiddenGrid, i, j);
                    hiddenGrid[i][j] = " " + count + "  ";
                }
            }
        }
    
        return hiddenGrid;
    }
    

    private static int countNeighboringBombs(String[][] grid, int row, int col) {
        int count = 0;
        int n = grid.length;

        // Define directions for neighboring cells
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Check neighboring cells
        for (int i = 0; i < 8; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
            if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n && grid[newRow][newCol] == " *  ") {
                count++;
            }
        }

        return count;
    }

    // Function to generate the hidden grid
    public String[][] generateDisplayedGrid(String[][] displayedGrid, int n) {

        // Fill hidden grid with #
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                displayedGrid[i][j] = " #  ";
            }
        }

        return displayedGrid;
    }
    // Function to print the hidden grid
    public void printGrid(String[][] grid) {
        int n = grid.length;
    
        printColumnNumbers(n);
        printTopBorder(n);
        printGridContents(grid);
        printBottomBorder(n);
    }

    public void printColumnNumbers(int n) {
        System.out.print("   ");
        for (int i = 0; i < n; i++) {
            if (i <= 9) {
                System.out.printf("  %d ", i);
            } else {
                System.out.printf(" %d ", i);
            }
        }
        System.out.println();
    }
    
    public void printTopBorder(int n) {
        System.out.print("   ");
        for (int i = 0; i < n; i++) {
            System.out.print("----");
        }
        System.out.println("-");
    }
    
    public void printGridContents(String[][] grid) {
        int n = grid.length;
        for (int i = 0; i < n; i++) {
            // Print row number
            System.out.printf("%2d |", i);
    
            // Print cells
            for (int j = 0; j < n; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println("|");
        }
    }
    
    public void printBottomBorder(int n) {
        System.out.print("   ");
        for (int i = 0; i < n; i++) {
            System.out.print("----");
        }
        System.out.println("-");
    }

    
    public void checkSurroundingCells(String[][] answerKey, String[][] displayedGrid, int row, int col) {
        if (displayedGrid[row][col].equals(" 0  ")){
            int n = answerKey.length;
            // Define directions for neighboring cells
            int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        
            // Check neighboring cells
            for (int i = 0; i < 8; i++) {
                int newRow = row + dx[i];
                int newCol = col + dy[i];
                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                    if(displayedGrid[newRow][newCol].equals(" #  ")){
                        displayedGrid[newRow][newCol] = answerKey[newRow][newCol];
                        if (answerKey[newRow][newCol].equals(" 0  ")) {
                            // If the neighboring cell is also "0", recursively call checkSurroundingCells
                            checkSurroundingCells(answerKey, displayedGrid, newRow, newCol);
                        }
                    }
                }
            }  
        } 
    }

    public void addBombsToGrid(String[][] displayedGrid, String[][] answerKey){
        for (int i = 0; i < displayedGrid.length; i++) {
            for (int j = 0; j < displayedGrid.length; j++) {
                if(displayedGrid[i][j].equals(" ?  ") && !answerKey[i][j].equals(" *  ")){
                    displayedGrid[i][j] = " x  ";
                } else if (answerKey[i][j].equals(" *  ")){
                    displayedGrid[i][j] = answerKey[i][j];
                }
            }
        }
    }
    public boolean checkWinCondition(String[][] displayedGrid, String[][] answerKey) {
        int n = displayedGrid.length; // Assuming displayedGrid is a square grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String displayedCell = displayedGrid[i][j];
                String answerCell = answerKey[i][j];
                // If it's a non-bomb cell and not revealed or covered with a flag, return false
                if ((displayedCell.equals(" #  ") || displayedCell.equals(" ?  ")) && !answerCell.equals(" *  ")) {
                    return false;
                }
            }
        }
        // If all conditions are met, return true indicating the win
        return true;
    }
        
}
