import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;


public class project{
    
    /**
     * TO-DO
     * 1- Double check the strategy one
     * 2- Start to work on strategy two
     */

    private static Random random = new Random();
    private static double flammability = 0.9;
    private static int maxTimestamp = 10;
    private static int maxDimension = 5;
    
    public static void main(String[] args) {
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
        else{
            return;
        }
        int[] locationFire = getRandomOpenCell(grid);
        if(locationFire != null){
            grid[locationFire[0]][locationFire[1]] = "F";
        }
        else{
            return;
        }
        int[] locationButton = getRandomOpenCell(grid);
        if(locationButton != null){
            grid[locationButton[0]][locationButton[1]] = "E";
        }
        else{
            return;
        }
        
        System.out.println();

        printGrid(grid);
        
        //result = strategyOne(grid, locationBot, locationButton, locationFire, flammability);
        result = strategyOne(grid,locationBot,locationButton, locationFire);
        if(result){
            System.out.println("success");
            
        }
        else{
            System.out.println("failure");
        }
    }
    
    private static class Node {
        private int row;
        private int col;
        private int cost;
        private Node parent;

        public Node(int row, int col, int cost, Node parent) {
            this.row = row;
            this.col = col;
            this.cost = cost;
            this.parent = parent;
        }
    }
    
    public static int calculateManhattanDistance(int[] cell1, int[] cell2) {
        return Math.abs(cell1[0] - cell2[0]) + Math.abs(cell1[1] - cell2[1]);
    }

   
    public static boolean strategyOne(String[][] grid, int[] locationBot, int[] locationButton, int[] locationFire) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Double.compare(node1.cost, node2.cost);
            }
        });
        Set<Node> explored = new HashSet<>();

        // Add the starting node to the frontier.
        priorityQueue.add(new Node(locationBot[0], locationBot[1], 0, null));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            grid[currentNode.row][currentNode.col] = "B";
            // Check if the current node is the goal node.
            if (currentNode.row == locationButton[0] && currentNode.col == locationButton[1]) {
                // Return the path to the goal node.
                return true;
            }

            // Add the current node to the explored set.
            explored.add(currentNode);
            
            // Expand the current node.
            for (int[] neighbor : getNeighbors( grid, currentNode.row, currentNode.col)) {
                int neighborRow = neighbor[0];
                int neighborCol = neighbor[1];
                // !!!! need to check here whether it is valid cell or not !!!!!!
                if(grid[neighborRow][neighborCol].equals("O")){
                    // check if the neighbor node is not explored
                    if (!explored.contains(new Node(neighborRow, neighborCol, 0, null))) {
                        // Add the neighbor node to the frontier.
                        priorityQueue.add(new Node(neighborRow, neighborCol, currentNode.cost + 1, currentNode));
                    }
                }
                else if(grid[neighborRow][neighborCol].equals("E")){
                    return true;
                }
                else{
                    continue;
                }
            }
            spreadFire(grid, locationFire[0], locationFire[1], flammability);
            if(grid[locationButton[0]][locationButton[1]].equals("F")){
                return false;
            }
        }

        // No path found.
        return false;
    }
    private static List<int[]> getNeighbors(String[][] grid, int x, int y) {
        List<int[]> neighbors = new ArrayList<>();

        // Add the four neighboring cells.
        neighbors.add(new int[]{x, y + 1});
        neighbors.add(new int[]{x + 1, y});
        neighbors.add(new int[]{x, y - 1});
        neighbors.add(new int[]{x - 1, y});

        // Remove any neighbors that are outside of the grid.
        neighbors.removeIf(neighbor -> neighbor[0] < 0 || neighbor[0] >= grid.length || neighbor[1] < 0 || neighbor[1] >= grid[0].length);

        return neighbors;
    }

    /**
     * Spreading the fire for current timestamp
     * @param grid
     * @param row
     * @param col
     * @param flammability
     */
    public static void spreadFire(String[][] grid, int row, int col, double flammability) {
    
        // Define the neighboring directions
        int[] dx = { -1, 1, 0, 0 };
        int[] dy = { 0, 0, -1, 1 };
    
        // Check each neighboring cell
        for (int i = 0; i < 4; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
    
            // Check if the neighboring cell is within bounds and open
            if (isValidCell(newRow, newCol, grid)) {
                if( grid[newRow][newCol].equals("O") || grid[newRow][newCol].equals("E") || grid[newRow][newCol].equals("B")){
                    int burningNeighbors = countBurningNeighbors(grid, newRow, newCol);
    
                    double probability = 1.0 - Math.pow((1.0 - flammability), burningNeighbors);
                    double randomDouble = random.nextDouble() * (1.0 - 0.0) + 0.0;
        
                    if (randomDouble < probability) {
                        grid[newRow][newCol] = "F"; // Cell catches fire
                        // Recursively spread fire to the newly ignited cell
                        
                        // spreadFire(grid, newRow, newCol, flammability);
                        printGrid(grid);
                    }
                }
                
            }
        }
    }
    
    public static int countBurningNeighbors(String[][] grid, int row, int col) {
        int burningNeighbors = 0;
    
        int[] dx = { -1, 1, 0, 0 };
        int[] dy = { 0, 0, -1, 1 };
    
        // Count neighboring cells that are on fire
        for (int i = 0; i < 4; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
    
            if (isValidCell(newRow, newCol, grid) && grid[newRow][newCol].equals("F")) {
                burningNeighbors++;
            }
        }
    
        return burningNeighbors;
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
        System.out.println();
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