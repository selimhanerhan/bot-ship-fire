
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;


public class project{
    
    /**
     * TO-DO
     * 1- I need to check if we are really running every possible neighbors then do the strategy 2
     * DONE 2- I need to check if fire is spreading correctly when it gets to strategy 2 (locationFire is never updated)
        maxDimension shouldn't be anything less than 30  ** THIS IS EXTEMELY IMPOrtant DONE

        small to-do:
        a- i need to check what happens when fire catches bot

        
     */

    private static Random random = new Random();
    private static double flammability = 0.5;
    private int maxDimension = 50;
    private final int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static int numberOfIteartions = 4268;
    
    
    public static void main(String[] args) {
        project theProject = new project();
        String x = "X";
        String o = "O";
        int success = 0, loses = 0;
        for(int i = 0; i < numberOfIteartions; i++){
            String[][] grid = theProject.createRandomGrid(theProject.maxDimension, x, o);
            boolean result;

            // 3 - do this until there is no other currently blocked cells left
            while (theProject.hasBlockedCellOON(grid)) {
                theProject.openBlockedCellWOON(grid);
            }

            // 4 & 5 - identify the dead ends and open half of them  
            theProject.openDeadEnds(grid, 0.5); 
            
            // print the final grid
            //printGrid(grid);
            
            // pick a random open cell for the bot
            int[] locationBot = theProject.getRandomOpenCell(grid);
            if(locationBot != null){
                grid[locationBot[0]][locationBot[1]] = "B";
            }
            else{
                return;
            }
            int[] locationFire = theProject.getRandomOpenCell(grid);
            if(locationFire != null){
                grid[locationFire[0]][locationFire[1]] = "F";
            }
            else{
                return;
            }
            int[] locationButton = theProject.getRandomOpenCell(grid);
            if(locationButton != null){
                grid[locationButton[0]][locationButton[1]] = "E";
            }
            else{
                return;
            }
            
            //result = theProject.strategyOne(grid, locationBot, locationButton, locationFire);
            //result = theProject.strategyTwo(grid, locationBot[0], locationBot[1], locationButton, locationFire);
            result = theProject.strategyThree(grid, locationBot, locationButton,locationFire);
            //theProject.printGrid(grid);
            System.out.println(i);
            //theProject.printGrid(grid);
            if(result){
                success++;
                continue;
            }
            else{
                loses++;
                continue;
            }
        }
        System.out.println("success rate:"+ success + "lose rate:" + loses);

        // String[][] grid = theProject.createRandomGrid(theProject.maxDimension, x, o);
        // boolean result;

        // // 3 - do this until there is no other currently blocked cells left
        // while (theProject.hasBlockedCellOON(grid)) {
        //     theProject.openBlockedCellWOON(grid);
        // }

        // // 4 & 5 - identify the dead ends and open half of them  
        // theProject.openDeadEnds(grid, 0.5); 
        
        // // print the final Fgrid
        
        // // pick a random open cell for the bot
        // int[] locationBot = theProject.getRandomOpenCell(grid);
        // if(locationBot != null){
        //     grid[locationBot[0]][locationBot[1]] = "B";
        // }
        // else{
        //     return;
        // }
        // int[] locationFire = theProject.getRandomOpenCell(grid);
        // if(locationFire != null){
        //     grid[locationFire[0]][locationFire[1]] = "F";
        // }
        // else{
        //     return;
        // }
        // int[] locationButton = theProject.getRandomOpenCell(grid);
        // if(locationButton != null){
        //     grid[locationButton[0]][locationButton[1]] = "E";
        // }
        // else{
        //     return;
        // }
        
        // System.out.println();

        
        // result = theProject.strategyOne(grid,locationBot,locationButton, locationFire);
        // //result = strategyTwo(grid, locationBot[0],locationBot[1], locationButton, locationFire);
        // //result = strategyThree(grid, locationBot, locationButton, locationFire);
        // if(result){
        //     System.out.println("success");
            
        // }
        // else{
        //     System.out.println("failure");
        // }
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

    public void spreadFire(String[][] grid, int initialRow, int initialCol, double flammability, int level) {
        int gridSize = grid.length;
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<int[]> fireQueue = new LinkedList<>();
    
        // Start with the initial fire position
        fireQueue.add(new int[]{initialRow, initialCol});
        visited[initialRow][initialCol] = true;
    
        // Track the current level
        int currentLevel = 0;
    
        // While the queue is not empty, keep checking for fire spread
        while (!fireQueue.isEmpty()) {
            int[] current = fireQueue.poll();
    
            int row = current[0];
            int col = current[1];
    
            // Only check the neighbors of cells that are at the current level
            if (currentLevel <= level) {
                int[] dx = {-1, 1, 0, 0};
                int[] dy = {0, 0, -1, 1};
    
                for (int i = 0; i < 4; i++) {
                    int newRow = row + dx[i];
                    int newCol = col + dy[i];
    
                    if (isValidCell(newRow, newCol, grid) && !visited[newRow][newCol] && !grid[newRow][newCol].equals("X")) {
                        int burningNeighbors = countBurningNeighbors(grid, newRow, newCol);
    
                        double probability = 1.0 - Math.pow((1.0 - flammability), burningNeighbors);
                        double randomDouble = random.nextDouble();
    
                        if (randomDouble < probability) {
                            grid[newRow][newCol] = "F"; // Cell catches fire
                            visited[newRow][newCol] = true;
                            fireQueue.add(new int[]{newRow, newCol});
                            //printGrid(grid);
                        }
                    }
                }
            }
    
            // Increment the current level
            currentLevel++;
        }
    
        // Print the grid after fire spread (if needed)
        //printGrid(grid);
    }

    // need to find what happens when the fire catches bot
    public boolean strategyOne(String[][] grid, int[] locationBot, int[] locationButton, int[] locationFire) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Double.compare(node1.cost, node2.cost);
            }
        });
        Set<Node> explored = new HashSet<>();
        int level = 0;
        // Add the starting node to the frontier.
        priorityQueue.add(new Node(locationBot[0], locationBot[1], 0, null));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            // Check if the current node is the goal node.
            if (currentNode.row == locationButton[0] && currentNode.col == locationButton[1]) {
                // Return the path to the goal node.
                return true;
            }
            
            // Add the current node to the explored set.
            explored.add(currentNode);
            //printGrid(grid);
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
            
            }
            //printGrid(grid);
            spreadFire(grid,locationFire[0],locationFire[1], flammability, level);
            level += 1;
            if(grid[locationButton[0]][locationButton[1]].equals("F") || grid[currentNode.row][currentNode.col].equals("F")){
                return false;
            }
            // i need to double check on this one.
            // if(grid[currentNode.row][currentNode.col] == "F"){
            //     return false;
            // }
        }

        // No path found.
        return false;
    }
    

    /**
     * Get neighbors without checking the fire cells
     */
    private List<int[]> getNeighbors(String[][] grid, int x, int y) {
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

    public boolean strategyTwo(String[][] grid, int botRow, int botCol, int[] locationButton, int[] locationFire) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Double.compare(node1.cost, node2.cost);
            }
        });
        Set<Node> explored = new HashSet<>();
        int level = 0;
        //printGrid(grid);

        // Add the starting node to the frontier.
        priorityQueue.add(new Node(botRow, botCol, 0, null));

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            //grid[currentNode.row][currentNode.col] = "B";
            // Check if the current node is the goal node.
            if (currentNode.row == locationButton[0] && currentNode.col == locationButton[1]) {
                // Return the path to the goal node.
                return true;
            }

            // Add the current node to the explored set.
            explored.add(currentNode);
            
            // Expand the current node.
            for (int[] neighbor : getNeighborsWithoutFire( grid, currentNode.row, currentNode.col)) {
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
            }
            spreadFire(grid, locationFire[0], locationFire[1], flammability, level);
            level += 1;
            if(grid[locationButton[0]][locationButton[1]].equals("F") || grid[currentNode.row][currentNode.col].equals("F")){
                return false;
            }
        }

        // No path found.
        return false;
    }

    // This function returns the neighbors of a given node that are not fire cells.
    private List<int[]> getNeighborsWithoutFire(String[][] grid, int x, int y) {
        List<int[]> neighbors = new ArrayList<>();

        // Define the relative coordinates of the neighboring cells.
        

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Check if the neighboring cell is within the grid bounds.
            if (isValidCell(newX, newY, grid)) {
                if(grid[newX][newY].equals("O") || grid[newX][newY].equals("E")){ 
                // Check if the neighboring cell does not contain "F" (fire).
                    
                    neighbors.add(new int[]{newX, newY});
                    
                }
            }
        }

        return neighbors;
} 
    /**
     * TOUGHTS: (THIS MAY NOT BE NEEDED SINCE IT IS A VERY SMALL EDGE CASE)
     * To make this better structured, we can just have two methods. CBL = CURRENT BOT LOCATION - CFL = CURRENT FIRE LOCATION
     * 1- strategyThree:
     *      it calls the strategyThreeHelper method, 
     *        if it returns true, then return true, 
     *        if it returns false in the first time, run it again, if it returns false in the second time, just call strategy two and return whatever that returns
     * 2- strategyThreeHelper(currentBotLocation, locationButton, currentFireLocations)
     *      you can try to find a path from CBL to locationButton by avoiding the neighboring cells that is adjacent to CFL.
     */
    public boolean strategyThree(String[][] grid, int[] locationBot, int[] locationButton, int[] locationFire) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                return Double.compare(node1.cost, node2.cost);
            }
        });
        Set<String> explored = new HashSet<>();
        int level = 0;

        // Add the starting node to the frontier.
        priorityQueue.add(new Node(locationBot[0], locationBot[1], 0, null));
        Node currentNode;
        //printGrid(grid);
        while (!priorityQueue.isEmpty()) {
            currentNode = priorityQueue.poll();

            // Check if the current node is the goal node.
            if (currentNode.row == locationButton[0] && currentNode.col == locationButton[1]) {
                // Return the path to the goal node.
                return true;
            }

            // Add the current node to the explored set.
            String currentKey = currentNode.row + "-" + currentNode.col;
            explored.add(currentKey);
            
            
            // Expand the current node.
            for (int[] neighbor : getNeighborsWithoutAdjacentFireCells(grid, currentNode.row, currentNode.col)) {
                int neighborRow = neighbor[0];
                int neighborCol = neighbor[1];

                // Check if the neighbor node is open and not explored.
                if (grid[neighborRow][neighborCol].equals("O")) {
                    // Add the neighbor node to the frontier.
                    String neighboringKey = neighborRow + "-" + neighborCol;
                    if(!explored.contains(neighboringKey)){
                        priorityQueue.add(new Node(neighborRow, neighborCol, currentNode.cost + 1, currentNode));

                    }
                }
                else if(grid[neighborRow][neighborCol].equals("E")){
                    return true;
                }
                // now i need to find a way when there is no such path, the bot needs to look for other paths.
                // to look for other paths you need to explore the cells that you already explored before.

                
            }
            // i need to check if we are really running every possible neighbors then do the strategy 2
            // i need to check if fire is spreading correctly when it gets to strategy 2
            if(priorityQueue.isEmpty()){
                return strategyTwo(grid, currentNode.row, currentNode.col, locationButton, locationFire);
            }
            spreadFire(grid,locationFire[0], locationFire[1], flammability , level);
            level +=1;
            if(grid[locationButton[0]][locationButton[1]].equals("F")){
                return false;
            }
            
        }

        // No path found.
        return false;
    }

    public boolean canPickCell(String[][] grid, int x, int y) {
        if (grid[x][y].equals("O") || grid[x][y].equals("E")) {
            if(grid[x][y].equals("E")){
                return true;
            }
            for (int[] neighbor : getNeighbors(grid, x, y)) {
                if (grid[neighbor[0]][neighbor[1]].equals("F")) {
                    return true;
                }
            }
        }
        return false;
      }
      

      private List<int[]> getNeighborsWithoutAdjacentFireCells(String[][] grid, int x, int y) {
        List<int[]> neighbors = new ArrayList<>();
    
        // Add the four neighboring cells.
        neighbors.add(new int[]{x, y + 1});
        neighbors.add(new int[]{x + 1, y});
        neighbors.add(new int[]{x, y - 1});
        neighbors.add(new int[]{x - 1, y});
    
        // Remove any neighbors that are fire cells or adjacent to fire cells.
        neighbors.removeIf(neighbor -> neighbor[0] < 0 || neighbor[0] >= grid.length || neighbor[1] < 0 || neighbor[1] >= grid[0].length || grid[neighbor[0]][neighbor[1]].equals("F") || isAdjacentToFireCell(neighbor[0], neighbor[1], grid));
    
        return neighbors;
    }
    // check if the neighboring cell is adjacent to fire, if it is in the first attempt you try to find another path
    // if it is not adjacent then you go there anyway
    private boolean isAdjacentToFireCell(int x, int y, String[][] grid) {
        // Check the four neighboring cells.
        for (int[] neighbor : getNeighbors(grid, x, y)) {
            if (grid[neighbor[0]][neighbor[1]].equals("F")) {
                return true;
            }
        }
    
        return false;
    }
    


    /**
     * Spreading the fire for current timestamp
     * @param grid
     * @param row
     * @param col
     * @param flammability
     */
    // public void spreadFire(String[][] grid, int row, int col, double flammability) {
    
    //     // Define the neighboring directions
    //     int[] dx = { -1, 1, 0, 0 };
    //     int[] dy = { 0, 0, -1, 1 };
    
    //     // Check each neighboring cell
    //     for (int i = 0; i < 4; i++) {
    //         int newRow = row + dx[i];
    //         int newCol = col + dy[i];
    
    //         // Check if the neighboring cell is within bounds and open
    //         if (isValidCell(newRow, newCol, grid)) {
    //             if( grid[newRow][newCol].equals("O") || grid[newRow][newCol].equals("E") || grid[newRow][newCol].equals("B")){
    //                 int burningNeighbors = countBurningNeighbors(grid, newRow, newCol);
    
    //                 double probability = 1.0 - Math.pow((1.0 - flammability), burningNeighbors);
    //                 double randomDouble = random.nextDouble() * (1.0 - 0.0) + 0.0;
        
    //                 if (randomDouble < probability) {
    //                     grid[newRow][newCol] = "F"; // Cell catches fire
    //                     // Recursively spread fire to the newly ignited cell
                        
    //                     // spreadFire(grid, newRow, newCol, flammability);
                        
    //                 }
                    
    //             }
                
    //         }
            
    //     }
    //     //printGrid(grid);
    // 
        /*
         * Queue<Cell> queue = new LinkedList<>();

        // Add the initial fire cell to the queue.
        queue.add(new Cell(initialFireRow, initialFireColumn));

        // While the queue is not empty, keep checking for fire spread.
        while (!queue.isEmpty()) {
            Cell currentCell = queue.poll();

            // For each adjacent cell, check if it is open and not burning. If it is, calculate the probability of it catching fire.
            for (int[] direction : DIRECTIONS) {
                int neighborRow = currentCell.row + direction[0];
                int neighborColumn = currentCell.column + direction[1];

                if (neighborRow >= 0 && neighborRow < grid.length && neighborColumn >= 0 && neighborColumn < grid[0].length && grid[neighborRow][neighborColumn].equals("O")) {
                    double probabilityOfCatchingFire = 1 - Math.pow(1 - flammability, currentCell.numberOfBurningNeighbors + 1);

                    // If the probability of catching fire is greater than or equal to a random number between 0 and 1, set the cell on fire.
                    if (probabilityOfCatchingFire >= Math.random()) {
                        grid[neighborRow][neighborColumn] = "F";
                        queue.add(new Cell(neighborRow, neighborColumn));
                    }
                }
            }
        }
         */
    
    
    public int countBurningNeighbors(String[][] grid, int row, int col) {
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
    public int[] getRandomOpenCell(String[][] grid) {
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
    public String[][] createRandomGrid(int maxDimension, String x, String o) {
        random = new Random();
        //int d = random.nextInt(maxDimension) + 1;
        int d = maxDimension;
        
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
    public boolean hasBlockedCellOON(String[][] grid) {
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
    public void openBlockedCellWOON(String[][] grid) {
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
    public int countOpenNeighbors(String[][] grid, int row, int col) {
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
    public int[] findOpenNeighbor(String[][] grid, int row, int col) {
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
    public boolean isValidCell(int row, int col, String[][] grid) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    /**
     * print the grid.
     * 
     * @param grid
     */
    public void printGrid(String[][] grid) {
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
    public void openDeadEnds(String[][] grid, double openFraction) {

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

        
    }

    /**
     * find the closed neighbors of the given cell for the dead ends.
     * 
     * @param grid
     * @param row
     * @param col
     * @return
     */
    public int[] findClosedNeighbor(String[][] grid, int row, int col) {
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