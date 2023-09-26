import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class project{
    // // public static int maxDimension = 5;
    // // public static Random random = new Random();
    // // public static int d = random.nextInt(maxDimension) + 1;
    // // public static String[][] grid = new String[d][d];
    

    // public static String[] currentValidBC(String[][] grid, int row, int column) {
    //     int cursor = 0;
    //     String[] neighbor = null;

    // // Iterate over all the neighbors of the cell Z
    //     for (int i = row - 1; i <= row + 1; i++) {
    //         for (int j = column - 1; j <= column + 1; j++) {
    //             // Check if the adjacent cell is within the bounds of the grid and has the value "a"
    //             if (i >= 0 && i < grid.length && j >= 0 && j < grid[i].length && grid[i][j].equals("x")) {
    //                 // Check if the adjacent cell has only one neighbor with the value of the cell that is located at the row and column parameters that are passed to the method
    //                 if (countNeighborsWithSameValue(grid, i, j) == 1) {
    //                     cursor++;
    //                     neighbor = new String[]{i + "", j + ""};

    //                     // If we have found a neighbor that meets the conditions, stop searching
    //                     if (cursor == 1) {
    //                         break;
    //                     }
    //                 }
    //             }
    //         }

    //         // If we have found a neighbor that meets the conditions, stop searching
    //         if (cursor == 1) {
    //             break;
    //         }
    //     }

    //     return neighbor;
    //     }

    // // public static String[] oneCurrentValidBC(String[][] grid, int row, int column) {
    // //     List<String[]> neighbors = currentValidBCs(grid, row, column);
    
    // //     if (neighbors.isEmpty()) {
    // //         return null;
    // //     }
    
    // //     Random random = new Random();
    // //     int randomIndex = random.nextInt(neighbors.size());
    
    // //     return neighbors.get(randomIndex);
    // // }
    // // i'm thinking to just get the first one

    // private static int countNeighborsWithSameValue(String[][] grid, int i, int j) {
    //     int count = 0;
    //     for (int k = i - 1; k <= i + 1; k++) {
    //         for (int l = j - 1; l <= j + 1; l++) {
    //             if (k >= 0 && k < grid.length && l >= 0 && l < grid[k].length && grid[k][l].equals("o")) {
    //                 count++;
    //             }
    //         }
    //     }

    //     return count;
    // }


    // public static String[][] createRandomGrid(int maxDimension, String x, String o) {
    //     Random random = new Random();
    //     int d = random.nextInt(maxDimension) + 1;

    //     String[][] grid = new String[d][d];
    //     for (int i = 0; i < d; i++) {
    //         for (int j = 0; j < d; j++) {
    //             grid[i][j] = x; // Initialize all cells in the row to x
    //         }
    //     }
    //     int randomRow = random.nextInt(d);
    //     int randomColumn = random.nextInt(d);
    //     grid[randomRow][randomColumn] = o;
        
    //     // open the neighboor of the selected cell
    //     int neighborRow = Integer.parseInt(currentValidBC(grid, randomRow, randomColumn)[0]);
    //     int neighborColumn = Integer.parseInt(currentValidBC(grid, randomRow, randomColumn)[1]);
    //     grid[neighborRow][neighborColumn] = o;

    //     // i finished the 3b and now at 3c
    //     // also need to figure out where i need to start the loop for 3
        
    //     return grid;
    // }

    // public static void main(String[] args) {
    //     int maxDimension = 5;
    //     String x = "x";
    //     String o = "o";
    //     String[][] grid = createRandomGrid(maxDimension, x, o);

    //     // Print the grid
    //     for (int i = 0; i < grid.length; i++) {
    //         for (int j = 0; j < grid[i].length; j++) {
    //             System.out.print(grid[i][j] + " ");
    //         }
    //         System.out.println();
    //     }

    // }
    private static Random random = new Random();
    public static void main(String[] args) {
        int maxDimension = 10; // Change this to your desired max dimension
        String x = "X";
        String o = "O";
        String[][] grid = createRandomGrid(maxDimension, x, o);
        boolean result;

        // 3 - do this until there is no other currently blocked cells left
        while (hasBlockedCellOON(grid)) {
            openBlockedCellWOON(grid);
        }

        // 4 & 5 - identify the dead ends and open half of them  
        openDeadEnds(grid, 0.5); 
        
        // print the final grid
        printGrid(grid);
        
        // pick a random open cell for the bot
        int[] locationBot = getRandomOpenCell(grid);
        int[] locationFire = getRandomOpenCell(grid);
        int[] locationButton = getRandomOpenCell(grid);
        if(locationBot != null && locationFire != null && locationButton != null){
            grid[locationBot[0]][locationBot[1]] = "B";
            grid[locationFire[0]][locationFire[1]] = "F";
            grid[locationButton[0]][locationButton[1]] = "E";
        }
        System.out.println();
        printGrid(grid);

        result = strategyOne(grid, locationBot, locationButton, locationFire);
        
        if(result){
            System.out.println("Success");
        }
        else{
            System.out.println("Failure");
        }
    }
    public static boolean strategyOne(String[][] grid, int[] locationBot, int[] locationButton, int[] locationFire){
        return true;
        // need to implement the first strategy now
    }

    public static int[] getRandomOpenCell(String[][] grid) {
        random = new Random();
        int d = grid.length;

        for (int i = 0; i < d * d; i++) {
            int randomRow = random.nextInt(d);
            int randomColumn = random.nextInt(d);

            if (grid[randomRow][randomColumn].equals("O")) {
                return new int[] { randomRow, randomColumn };
            }
        }

        return null;
    }

    /**
     * 1 - create the random grid.
     * 
     * @param maxDimension
     * @param x
     * @param o
     * @return
     */
    public static String[][] createRandomGrid(int maxDimension, String x, String o) {
        random = new Random();
        int d = random.nextInt(maxDimension) + 1;
        
        // 1 - create a grid with cell and row size equal to d
        String[][] grid = new String[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                grid[i][j] = x; // initialize all cells in the row to x
            }
        }
        // 2 - pick a random cell called open cell
        int randomRow = random.nextInt(d);
        int randomColumn = random.nextInt(d);
        grid[randomRow][randomColumn] = o;

        return grid;
    }

    /**
     * checker method for whether blocked cell have one open neighbor or not,
     * so loop can finish if it returns false.
     * @param grid
     * @return
     */
    public static boolean hasBlockedCellOON(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("X") && countOpenNeighbors(grid, i, j) == 1) {
                    return true; 
                }
            }
        }
        return false; 
    }

    /**
     * opens the blocked cell with one open neighboor.
     * 
     * @param grid
     */
    public static void openBlockedCellWOON(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("X") && countOpenNeighbors(grid, i, j) == 1) {
                    int[] neighbor = findOpenNeighbor(grid, i, j);
                    grid[i][j] = "O"; 
                    grid[neighbor[0]][neighbor[1]] = "O"; 
                    return; 
                }
            }
        }
    }

    /**
     * count how many open neighbors the given cell have.
     * 
     * @param grid
     * @param row
     * @param col
     * @return
     */
    public static int countOpenNeighbors(String[][] grid, int row, int col) {
        int count = 0;
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} }; 

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidCell(newRow, newCol, grid) && grid[newRow][newCol].equals("O")) {
                count++;
            }
        }

        return count;
    }

    /**
     * find the open neighbor of given cell.
     * 
     * @param grid
     * @param row
     * @param col
     * @return
     */
    public static int[] findOpenNeighbor(String[][] grid, int row, int col) {
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} }; 

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidCell(newRow, newCol, grid) && grid[newRow][newCol].equals("O")) {
                return new int[] {newRow, newCol};
            }
        }

        return null; 
    }

    /**
     * checker for whether the cell is inside of the boundaries of the grid.
     * @param row
     * @param col
     * @param grid
     * @return
     */
    public static boolean isValidCell(int row, int col, String[][] grid) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    /**
     * print the grid.
     * 
     * @param grid
     */
    public static void printGrid(String[][] grid) {
        for (String[] row : grid) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    /**
     * 4 & 5 - identify and open the dead ends.
     * 
     * @param grid
     * @param openFraction
     */
    public static void openDeadEnds(String[][] grid, double openFraction) {

        Random random = new Random();
        int totalDeadEnds = 0;
        int openedDeadEnds = 0;

        
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("O") && countOpenNeighbors(grid, i, j) == 1) {
                    totalDeadEnds++;

                    if (random.nextDouble() <= openFraction) {
                        int[] neighbor = findClosedNeighbor(grid, i, j);
                        if (neighbor != null) {
                            grid[neighbor[0]][neighbor[1]] = "O"; // Open the closed neighbor
                            openedDeadEnds++;
                        }
                    }
                }
            }
        }

        System.out.println("Total Dead Ends: " + totalDeadEnds);
        System.out.println("Opened Dead Ends: " + openedDeadEnds);
    }

    /**
     * find the closed neighbors of the given cell for the dead ends.
     * 
     * @param grid
     * @param row
     * @param col
     * @return
     */
    public static int[] findClosedNeighbor(String[][] grid, int row, int col) {
    int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} }; 

    Random random = new Random();
    List<int[]> closedNeighbors = new ArrayList<>();

    for (int[] dir : directions) {
        int newRow = row + dir[0];
        int newCol = col + dir[1];

        if (isValidCell(newRow, newCol, grid) && grid[newRow][newCol].equals("X")) {
            closedNeighbors.add(new int[] {newRow, newCol});
        }
    }

    if (!closedNeighbors.isEmpty()) {
        int randomIndex = random.nextInt(closedNeighbors.size());
        return closedNeighbors.get(randomIndex);
    }

    return null; 
}

}