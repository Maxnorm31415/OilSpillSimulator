/*
The Grid class creates the initial map with predefined positions for the shore and oil,
as well as randomly generated positions for six rocks. The class contains several variables and methods,
which are explained in more detail below.

Imported classes:
java.util.ArrayList - Used to store a dynamic list of elements. In this project, it is mainly used to remove elements and track their number.
java.util.InputMismatchException - Used to handle invalid input when the user enters text instead of a number.
java.util.Random - Used to generate random positions for rocks on the map and change the current direction.
java.util.Scanner - Used to read user input from the console. */
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Grid {
    /*The default map size is 8x12 (rows x columns). The size can be changed, but the number
    of rocks and the predefined positions of the shore and oil will not automatically
    adjust to the new map dimensions.*/
    private final int height = 8;
    private final int length = 12;
    private final int rockCount = 6;
    private final String[] directions = new String[] {"East", "South", "West", "North"}; // Possible current directions
    private final int[][] positionOils = new int[][]{{1,4}, {4,7}}; // Coordinates of the oil patches (not individual oil cells)
    /*Instead of specifying every shore coordinate, we decided to store the number of shore cells
    at the beginning and end of each row: {number at the beginning, number at the end}.
    This approach is not suitable for placing shore cells in the middle of a row, but since
    the map follows the predefined layout from the mini-project description, it works well
    for this project.*/
    private final int[][] shoreWidths = new int[][]{{0,2}, {0,1}, {0,2}, {0,1}
            , {0,1}, {0,1}, {0,2}, {3,3}};
    private int index = 0; // Index for each oil cell
    private int oilCollected = 0;
    private int barriersLeft = 5;
    private int totalOils = 0; // Total number of oil cells on the map
    private Random random = new Random(); // Random object for generating random numbers
    private char[][] map = new char[height][length]; // 2D array for the map with dimensions height x length
    private int[] vesselPosition = {4,0}; // Vessel position, default: (4, 0)
    private String currentDirection = "East"; // Current direction of the current
    private ArrayList<oil> ListOils = new ArrayList<>(); // ArrayList containing all oil cells remaining in the game


    /*Instead of manually entering all 12 oil coordinates, we use generateOilPatch() to create
    the oil patches according to the mini-project description.

    We noticed that both oil patches have the same shape, with six oil cells arranged in
    the same pattern. This allows us to generate both patches using a single method.
    If the oil positions were randomly generated, this method would not be necessary.*/
    private void generateOilPatch(){
        for(int[] pos : positionOils){
            ListOils.add(new oil(pos[0], pos[1], index++));
            ListOils.add(new oil(pos[0], pos[1]+1, index++));
            ListOils.add(new oil(pos[0]+1, pos[1], index++));
            ListOils.add(new oil(pos[0]+1, pos[1]+1, index++));
            ListOils.add(new oil(pos[0]+1, pos[1]+2, index++));
            ListOils.add(new oil(pos[0]+2, pos[1]+1, index++));
        }
        for(oil Oil : ListOils){ // Adding each oil cell to the map
            map[Oil.getPosition()[0]][Oil.getPosition()[1]] = 'o';
        }
        totalOils = ListOils.size();
    }

    /*Since the shore follows a predefined pattern, we created the generateShore() method
    to place shore cells on the map.
    The first loop (int i = 0) goes through each row of the map. The second loop
    (int j = 0) places shore cells at the beginning of each row, while the third loop
    (int k = 0) places shore cells at the end of each row.
    The number of shore cells for each side is taken from the shoreWidths array.*/
    private void generateShore(){
        for(int i = 0; i < shoreWidths.length; i++){
            for(int j = 0; j < shoreWidths[i][0]; j++){
                map[i][j] = 'S';
            }
            for(int k = 0; k < shoreWidths[i][1]; k++){
                map[i][length - 1 - k] = 'S';
            }
        }
    }

    /*The generateRocks() method generates rocks at random coordinates using nextInt(x).
    This method generates a random number from 0 to x - 1 (inclusive), separately for
    the row and column. If the selected cell is already occupied by an obstacle or another object,
    the method generates new coordinates until a valid position is found.*/
    private void generateRocks(){
        int counter = rockCount; // Counter used to ensure that all six rocks are generated
        while(counter > 0){
            int r = random.nextInt(height);
            int c = random.nextInt(length);
            // Rocks are placed only on clean water and not on the vessel's starting cell:
            if(map[r][c] == '~' && (vesselPosition[0] != r || vesselPosition[1] != c)) {
                map[r][c] = 'R';
                counter--;
            }
        }
    }

    /* getIndexDirection(String direction) finds the index of the given direction in the
     directions array. This is used by the changeDirection() method. */
    private int getIndexDirection(String direction){
        for(int i = 0; i < directions.length; i++){
            if(direction.equals(directions[i])) return i; // The .equals() method compares the contents of Strings
        }
        return -1;
    }

    // getIndexOil(int r, int c) finds an Oil object by its coordinates (r, c). It is used by the collectOil() method.
    private int getIndexOil(int r, int c){
        for (oil Oil : ListOils){
            if(r == Oil.getPosition()[0] && c == Oil.getPosition()[1]) return Oil.getIndex();
        }
        return -1;
    }

    /*The clearOil(int ix) method removes the oil object with the given index from the ListOils
    ArrayList when it reaches the shore or is collected by the vessel. This ensures that oil which
    has already reached the shore or has been collected is no longer processed when the remaining oil cells move.*/
    private void clearOil(int ix){
        if(ix == -1){ // Check if the index is valid in case something goes wrong in getIndexOil().
            System.out.println("Error: Oil has coordinates but no index");
            return;
        }
        // Replace the oil cell with clean water without affecting the vessel's position:
        map[ListOils.get(ix).getPosition()[0]][ListOils.get(ix).getPosition()[1]] = '~';
        ListOils.remove(ix);
        // When an oil object is removed from the ArrayList, the indices of the remaining objects must also be updated:
        for(int i = ix; i < ListOils.size(); i++){
            ListOils.get(i).setIndex(i);
        }
    }

    // The changeDirection() method randomly changes the current direction: 50% chance to stay the same, 25% to turn left, and 25% to turn right.
    private void changeDirection(){
        int num = random.nextInt(1000); // Generate a random number from 0 to 999
        if(num % 2 == 0) return; // If the number is even, the current direction remains unchanged (50% chance)

        /*The directions array is used to determine the new current direction when it turns
        left or right. The directions are arranged in a specific order, so moving to the
        previous element represents a left turn, while moving to the next element
        represents a right turn. We also handle the array boundaries to ensure that turning from the first or
        last element correctly wraps around to the opposite end of the array. */
        int ix = getIndexDirection(currentDirection); // Current index of the element in the array
        if(num < 500){  // There are 250 odd numbers between 0 and 499, representing 25% of all possible values
            ix--;
            if(ix == -1) ix = 3;
        }
        else{ // There are 250 odd numbers between 500 and 999, representing the remaining 25%.
            ix++;
            if(ix == 4) ix = 0;
        }
        currentDirection = directions[ix];
    }

    // The collectOil() method is used to collect oil around the vessel.
    private int collectOil(){
        int collected = 0;
        final int[][] radar = new int[][]{{0,1}, {0,-1}, {1,0}, {-1,0}, {0,0}}; // Positions to check for oil around the vessel, including the cell beneath it
        for(int[] pos : radar){
            int[] checkPosition = new int[]{vesselPosition[0] + pos[0], vesselPosition[1] + pos[1]};
            if(checkPosition[0] < 0 || checkPosition[0] >= height  // Check the map boundaries. If the coordinates are out of bounds, the cell is skipped.
            || checkPosition[1] >= length || checkPosition[1]< 0) continue;
            char cell = map[checkPosition[0]][checkPosition[1]]; // cell stores the character of the corresponding map cell.
            if(cell == 'o'){ // If cell contains oil ('o'), the oil is collected and removed from the ArrayList.
                clearOil(getIndexOil(checkPosition[0], checkPosition[1]));
                collected++;
                oilCollected++;
            }
        }
        return collected; // Returns the number of oil cells collected during one round
    }

    /* The Grid() constructor fills the 2D map array with water ('~') and then adds
    the oil, shore, and rocks to the map. The vessel is not stored directly in the map array
    because it would overwrite the character of the cell beneath it. This allows the collectOil()
    method to detect and collect oil even when the vessel is on the same cell.*/
    public Grid(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                map[i][j] = '~';
            }
        }
        generateOilPatch();
        generateShore();
        generateRocks();
    }

    // The move() method moves each oil cell according to the current direction, if possible.
    public void move(){
        ArrayList<oil> oilToMove = new ArrayList<>(); // List of oil cells that will move to water.
        ArrayList<oil> oilToShore = new ArrayList<>(); // List of oil cells that will move to shore.
        int[] direction = switch (currentDirection) { // The switch statement selects the movement direction based on the current.
            case "East" -> new int[]{0, 1};
            case "West" -> new int[]{0, -1};
            case "South" -> new int[]{1, 0};
            case "North" -> new int[]{-1, 0};
            default -> { // The default case handles unexpected direction values. It sets the movement to (0, 0), so the oil stays in place
                System.out.println(" Error in the switch statement in the move() method - Direction: " + currentDirection);
                yield new int[]{0, 0};
            }
        };
        for(oil Oil : ListOils){
            int[] directNew = new int[]{direction[0] + Oil.getPosition()[0],
                    direction[1] + Oil.getPosition()[1]}; // New possible position of the oil cell
            // Check if the coordinates are within the map boundaries:
            if(directNew[0] >= height || directNew[0] < 0 || directNew[1] >= length || directNew[1] < 0) continue;
            char cell = map[directNew[0]][directNew[1]]; // cell stores the character of the corresponding map cell
            if(cell == '~'){ // If the target cell contains water, the oil is added to the oilToMove list.
                oilToMove.add(Oil);
                continue;
            }
            if(cell == 'S' || cell == 'X'){ // If the target cell contains shore, the oil is added to the oilToShore list
                oilToShore.add(Oil);
            }
            /* There is no separate check for obstacles (rocks, barriers, or other oil cells). Since these cells are neither water nor shore,
               the oil is not added to either list and will not move.*/
        }
        for(oil Oil : oilToMove){ // This loop moves the oil to water cells and updates the corresponding characters on the map.
            int[] oldPosition = new int[]{Oil.getPosition()[0],
                    Oil.getPosition()[1]};
            map[oldPosition[0]][oldPosition[1]] = '~';
            Oil.setPosition(oldPosition[0] + direction[0], oldPosition[1] + direction[1]);
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'o';
        }
        /*This loop moves oil onto the shore. The clearOil() method replaces the oil cell
         with water, while the shore cell becomes 'X' (even if it is already 'X'):*/
        for(oil Oil : oilToShore){
            int[] oldPosition = new int[]{Oil.getPosition()[0],
                    Oil.getPosition()[1]};
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'X';
            clearOil(Oil.getIndex()); // Since the oil has reached the shore, it is removed from the ListOils ArrayList
        }
        changeDirection(); // After moving the oil, the method updates the current direction (or keeps it unchanged).
    }

    /* The gameover() method checks whether the game is over (when no oil remains on the map). If the game is over,
       it displays the final map and game statistics in the console.*/
    public boolean gameover(){
        if(ListOils.isEmpty()){
            System.out.println("=== Game over: no more oil at sea ===");
            view();
            System.out.println("Oil recovered: " + oilCollected + " of " + totalOils
                    + " | Oil washed ashore: " + (totalOils - oilCollected));
            System.out.println("Score: " + oilCollected);
            return true;
        }
        else return false;
    }

    public void getInfo(){ // Displays the current round information.
        System.out.println("Current: " + currentDirection + " | Oil at sea: " + ListOils.size() + " | Barriers left: "
                + barriersLeft + " | Vessel at (" + vesselPosition[0] + "," + vesselPosition[1] + ") | Oil collected: " + oilCollected);
    }

    /* The placeBarrier() method allows the player to place a barrier on the map.
    Unlike vesselMove(), this method returns a boolean value, allowing the player
    to choose another action if no barriers are left or if the selected cell
    is occupied by an obstacle. */
    public boolean placeBarrier(){
        if(barriersLeft == 0){
            System.out.println("You have no barriers left");
            return false;
        }
        Scanner scan = new Scanner(System.in);
        int r = -1;
        int c = -1;
        while (true){
            try {  // The try-catch block handles invalid input, such as entering text instead of numbers when specifying the barrier coordinates.
                if(r == -1){
                    System.out.print("Barrier row: ");
                    r = scan.nextInt();
                    if(r < 0 || r >= height) { // Check if the player enters coordinates outside the map boundaries.
                        r = -1;
                        throw new Exception("Please enter a number between 0 and " + (height-1));
                    }
                }
                if(c == -1){
                    System.out.print("Barrier col: ");
                    c = scan.nextInt();
                    if(c < 0 || c >= length) { // Check if the player enters coordinates outside the map boundaries.
                        c = -1;
                        throw new Exception("Please enter a number between 0 and " + (length-1));
                    }
                }
                char cell = map[r][c];
                // Check if the selected coordinates contain water and do not match the vessel's position:
                if(cell == '~' && (vesselPosition[0] != r || vesselPosition[1] != c))  break;
                else {
                    System.out.println("Barriers can only be placed on clean water, away from the vessel");
                    /* If the player selects coordinates occupied by an obstacle, they can choose another action,
                       as described in the project requirements: */
                    return false;
                }

            }
            catch (InputMismatchException k){ // If the player enters a value that is not a number.
                System.out.println("Text input is not allowed. Please enter numbers only.");
                scan.nextLine();
            }
            catch (Exception e){ // If the player enters coordinates outside the map boundaries.
                System.out.println(e.getMessage());
                scan.nextLine();
            }
        }
        map[r][c] = '#';
        barriersLeft--;
        System.out.println("Barrier placed at (" + r + "," + c + ").");
        return true;
    }
    /*
    The vesselMove() method allows the player to move the vessel in four directions. Similar to collectOil(),
    we use an array to control movement. Depending on the chosen direction, one element is set to 0, while the other represents the
    number of cells the player wants to move. The direct variable determines which element to change. For example, when
    moving East or West, direct is 1, so only the horizontal position changes. */
    public void vesselMove(){
        Scanner scan = new Scanner(System.in);
        String act;
        String nameDirect = "None";
        int direct = -1;
        while (direct == -1) { // Loop to validate the player's input.
            System.out.print("Direction (N/E/S/W): ");
            act = scan.nextLine();
            act = act.toLowerCase(); // Allows the player to use both lowercase and uppercase letters
            switch (act){
                case "n":
                    direct = 0;
                    nameDirect = "north";
                    break;
                case "s":
                    direct = 0;
                    nameDirect = "south";
                    break;
                case "e":
                    direct = 1;
                    nameDirect = "east";
                    break;
                case "w":
                    direct = 1;
                    nameDirect = "west";
                    break;
                default:
                    System.out.println("Invalid input. Please enter N(n), E(e), S(s), or W(w)");
                    break;
            }
        }
        int moveDistance;
        while (true){
            System.out.print("Number of cells: ");
            try { // Try-catch handles cases where the player enters text instead of a number.
                moveDistance = scan.nextInt();
                if(moveDistance < 1 || moveDistance > 4) throw new Exception(); // The player can only choose to move between 1 and 4 cells.
            }
            catch (Exception e){
                System.out.println("Please enter a number between 1 and 4");
                scan.nextLine();
                continue;
            }
            break;
        }
        int[] pos = new int[]{0,0};
        String stopMessage = "The vessel cannot sail " + nameDirect + ": "; // Start of the warning for moving into an obstacle or outside the map boundaries
        int step = 1;
        /*
        The loop below handles vessel movement in four directions. Since moving West or North requires subtracting
        from the coordinates, step is either 1 or -1. We use Math.abs() to make sure the loop works correctly with both positive
        and negative values. The loop also checks whether the vessel can move to the next cell.*/
        if(nameDirect.equals("north") || nameDirect.equals("west")) step = -1;
        for(int i = step; Math.abs(i) <= moveDistance; i+=step){
            pos[direct] = i;
            // Check if the vessel would move outside the map boundaries:
            int[] positionNew = new int[]{vesselPosition[0] + pos[0], vesselPosition[1] + pos[1]};
            if(positionNew[0] >= height || positionNew[0] < 0 || positionNew[1] >= length || positionNew[1] < 0){
                System.out.println(stopMessage + "The edge of the board is in the way");
                pos[direct] -= step; // If the new coordinate is outside the map boundaries, move back one cell.
                break;
            }
            char cell = map[positionNew[0]][positionNew[1]];
            if(cell == 'S' || cell == 'R' || cell == '#' || cell == 'X'){  // Check for obstacles
                System.out.println(stopMessage + "There is an obstacle in the way");
                pos[direct] -= step; // If the new coordinate contains an obstacle, move back one cell.
                break;
            }
        }
        vesselPosition[0] += pos[0];
        vesselPosition[1] += pos[1];
        // Display information about the vessel's position and collected oil:
        int clld = collectOil();
        System.out.println("The vessel is at (" + vesselPosition[0] + "," + vesselPosition[1]
                + ") and skimmed " + clld + " oil cell(s).");
    }

    // The view() method displays the map in the terminal
    public void view(){
        System.out.print(" ");
        for(int i = -1; i < height; i++){
            if(i != -1) System.out.print(i + " "); // Display row numbers for easier navigation
            for (int j = 0; j < length; j++){
                if(i == -1) System.out.print(" " + j); // Display collum numbers for easier navigation
                // The vessel is not stored in the map array, so we display it separately:
                else if(i == vesselPosition[0] && j == vesselPosition[1]) System.out.print("V ");
                else System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
