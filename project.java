import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.w3c.dom.Node;

public class project{

    /**
     * will calculate the heuristic of the distance from the bot to the fire and the button
     * will get the heuristics from manhattan distance
     * after getting the distances,
     *  we will create firstGoal cell where its located between the fire cell and the button cell,
     *  this location will be reachable with considering the fire expanding speed. 
     * then bot will first initialize a 
     */
    

    /**
     * TO-DO
     * 1- need to figure out a new way to execute strategy one.
     */





    private static Random random = new Random();
    private static double flammability = 0.1;
    
    public static void main(String[] args) {
        int maxDimension = 5; // Change this to your desired max dimension
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
        if(locationBot != null){
            grid[locationBot[0]][locationBot[1]] = "B";
        }
        int[] locationFire = getRandomOpenCell(grid);
        if(locationFire != null){
            grid[locationFire[0]][locationFire[1]] = "F";
        }
        int[] locationButton = getRandomOpenCell(grid);
        if(locationButton != null){
            grid[locationButton[0]][locationButton[1]] = "E";
        }
        // if(locationBot != null && locationFire != null && locationButton != null && locationBot != locationFire && locationButton != locationFire){
        //     grid[locationBot[0]][locationBot[1]] = "B";
        //     grid[locationFire[0]][locationFire[1]] = "F";
        //     grid[locationButton[0]][locationButton[1]] = "E";
        // }
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
        int gridSize = grid.length;
        boolean[][] visited = new boolean[gridSize][gridSize];

        while (true) {
            // Calculate distances to the button and the fire
            double distanceToButton = calculateDistance( locationBot, locationButton);

            // Check if the bot enters the button cell
            if (locationBot[0] == locationButton[0] && locationBot[1] == locationButton[1]) {
                return true; // Task completed
            }

            // Check if the bot and fire occupy the same cell
            if (locationBot[0] == locationFire[0] && locationBot[1] == locationFire[1]) {
                return false; // Task failed
            }

            // Spread the fire to neighboring cells
            spreadFire(grid, flammability);

            // Calculate heuristic values based on distances and open paths
            double bestHeuristicValue = Double.NEGATIVE_INFINITY;
            int bestRow = locationBot[0];
            int bestCol = locationBot[1];

            int[] dx = { -1, 1, 0, 0 };
            int[] dy = { 0, 0, -1, 1 };

            for (int i = 0; i < 4; i++) {
                int newRow = locationBot[0] + dx[i];
                int newCol = locationBot[1] + dy[i];

                if (isValidCell(newRow, newCol, grid) && !visited[newRow][newCol] && !grid[newRow][newCol].equals("X")) {
                    double newDistanceToButton = calculateDistance(new int[]{newRow, newCol}, locationButton);
                    double heuristicValue = newDistanceToButton;
                    
                    // Check if the cell is on fire
                    if (grid[newRow][newCol].equals("F")) {
                        heuristicValue += 100.0; // Discourage moving towards fire
                    }
                    
                    if (heuristicValue > bestHeuristicValue) {
                        bestHeuristicValue = heuristicValue;
                        bestRow = newRow;
                        bestCol = newCol;
                    }
                }
            }

            // Move the bot to the cell with the best heuristic value
            visited[locationBot[0]][locationBot[1]] = true;
            locationBot[0] = bestRow;
            locationBot[1] = bestCol;

            // Print the updated grid
            grid[bestRow][bestCol] = "B";
            

            
        }
    }

    static class Node {
        int row, col;
        double cost;

        Node(int row, int col, double cost) {
            this.row = row;
            this.col = col;
            this.cost = cost;
        }
    }
    //euclidian distance
    public static double calculateDistance(int[] cell1, int[] cell2) {
        int dx = cell1[0] - cell2[0];
        int dy = cell1[1] - cell2[1];
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    
        
    
    public static void spreadFire(String[][] grid, double flammability) {
        int gridSize = grid.length;
        String[][] newGrid = new String[gridSize][gridSize];

        int[] dx = { -1, 1, 0, 0 };
        int[] dy = { 0, 0, -1, 1 };

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (grid[row][col].equals("O")) {
                    int burningNeighbors = 0;

                    for (int i = 0; i < 4; i++) {
                        int newRow = row + dx[i];
                        int newCol = col + dy[i];

                        if (isValidCell(newRow, newCol, grid) && grid[newRow][newCol].equals("F")) {
                            burningNeighbors++;
                        }
                    }

                    double probability = 1.0 - Math.pow(1.0 - flammability, burningNeighbors);

                    if (Math.random() < probability) {
                        newGrid[row][col] = "F"; // Cell catches fire
                    } else {
                        newGrid[row][col] = "O"; // Cell remains open
                    }
                } else {
                    newGrid[row][col] = grid[row][col]; // Blocked, Bot, Button, or Fire cells remain unchanged
                }
            }
        }

        // Update the original grid with the new fire spread
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                grid[row][col] = newGrid[row][col];
            }
        }
    }

    // creating the grid
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