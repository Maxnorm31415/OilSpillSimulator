import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Grid {
    private final int height = 8;
    private final int length = 12;
    private final int rockCount = 6;
    private final String[] directions = new String[] {"East", "South", "West", "North"};
    private final int[][] positionOils = new int[][]{{1,4}, {4,7}};
    private final int[][] shoreWidths = new int[][]{{0,2}, {0,1}, {0,2}, {0,1}
            , {0,1}, {0,1}, {0,2}, {3,3}};
    private static int index = 0;
    private int oilCollected = 0;
    private int barriersLeft = 5;
    private int totalOils = 0;
    private Random random = new Random();
    private char[][] map = new char[height][length];
    private int[] vesselPosition = {4,0};
    private String currentDirection = "East";
    private ArrayList<oil> ListOils = new ArrayList<>();

    // Needs improvement //
    private void generateOilPatch(){
        for(int[] pos : positionOils){
            ListOils.add(new oil(pos[0], pos[1], index++));
            ListOils.add(new oil(pos[0], pos[1]+1, index++));
            ListOils.add(new oil(pos[0]+1, pos[1], index++));
            ListOils.add(new oil(pos[0]+1, pos[1]+1, index++));
            ListOils.add(new oil(pos[0]+1, pos[1]+2, index++));
            ListOils.add(new oil(pos[0]+2, pos[1]+1, index++));
        }
        for(oil Oil : ListOils){
            map[Oil.getPosition()[0]][Oil.getPosition()[1]] = 'o';
        }
        totalOils = ListOils.size();
    }

    // Needs improvement //
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

    private void generateRocks(){
        int counter = rockCount;
        while(counter > 0){
            int r = random.nextInt(height);
            int c = random.nextInt(length);
            if(map[r][c] != '~') continue;
            map[r][c] = 'R';
            counter--;
        }
    }

    private int getIndexDirection(String direction){
        for(int i = 0; i < directions.length; i++){
            if(direction.equals(directions[i])) return i;
        }
        return -1;
    }

    private int getIndexOil(int r, int c){
        for (oil Oil : ListOils){
            if(r == Oil.getPosition()[0] && c == Oil.getPosition()[1]) return Oil.getIndex();
        }
        return -1;
    }

    private void clearOil(int ix){
        if(ix == -1){
            System.out.println("Error: Oil has coordinates but no index");
            return;
        }
        map[ListOils.get(ix).getPosition()[0]][ListOils.get(ix).getPosition()[1]] = '~';
        ListOils.remove(ix);
        for(int i = ix; i < ListOils.size(); i++){
            ListOils.get(i).setIndex(i);
        }
    }

    private void changeDirection(){
        int num = random.nextInt(1000);
        if(num % 2 == 0) return;
        int ix = getIndexDirection(currentDirection);
        if(num < 500){
            ix--;
            if(ix == -1) ix = 3;
        }
        else{
            ix++;
            if(ix == 4) ix = 0;
        }
        currentDirection = directions[ix];
    }

    private int collectOil(){
        int collected = 0;
        final int[][] radar = new int[][]{{0,1}, {0,-1}, {1,0}, {-1,0}, {0,0}};
        for(int[] pos : radar){
            int[] checkPosition = new int[]{vesselPosition[0] + pos[0], vesselPosition[1] + pos[1]};
            if(checkPosition[0] < 0 || checkPosition[0] >= height
            || checkPosition[1] >= length || checkPosition[1]< 0) continue;
            char cell = map[checkPosition[0]][checkPosition[1]];
            if(cell == 'o'){
                clearOil(getIndexOil(checkPosition[0], checkPosition[1]));
                collected++;
                oilCollected++;
            }
        }
        return collected;
    }

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

    public void move(){
        ArrayList<oil> oilToMove = new ArrayList<>();
        ArrayList<oil> oilToShore = new ArrayList<>();
        int[] direction = switch (currentDirection) {
            case "East" -> new int[]{0, 1};
            case "West" -> new int[]{0, -1};
            case "South" -> new int[]{1, 0};
            case "North" -> new int[]{-1, 0};
            default -> {
                System.out.println(" Error in the switch statement in the move() method - Direction: " + currentDirection);
                yield new int[]{0, 0};
            }
        };
        for(oil Oil : ListOils){
            int[] directNew = new int[]{direction[0] + Oil.getPosition()[0],
                    direction[1] + Oil.getPosition()[1]};
            if(directNew[0] >= height || directNew[0] < 0 || directNew[1] >= length || directNew[1] < 0) continue;
            char cell = map[directNew[0]][directNew[1]];
            if(cell == '~'){
                oilToMove.add(Oil);
                continue;
            }
            if(cell == 'S' || cell == 'X'){
                oilToShore.add(Oil);
            }
        }
        for(oil Oil : oilToMove){
            int[] oldPosition = new int[]{Oil.getPosition()[0],
                    Oil.getPosition()[1]};
            map[oldPosition[0]][oldPosition[1]] = '~';
            Oil.setPosition(oldPosition[0] + direction[0], oldPosition[1] + direction[1]);
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'o';
        }
        for(oil Oil : oilToShore){
            int[] oldPosition = new int[]{Oil.getPosition()[0],
                    Oil.getPosition()[1]};
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'X';
            clearOil(Oil.getIndex());
        }
        changeDirection();
    }

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

    public void getInfo(){
        System.out.println("Current: " + currentDirection + " | Oil at sea: " + ListOils.size() + " | Barriers left: "
                + barriersLeft + " | Vessel at (" + vesselPosition[0] + "," + vesselPosition[1] + ") | Oil collected: " + oilCollected);
    }
    public boolean placeBarrier(){
        if(barriersLeft == 0){
            System.out.println("You have no barriers left");
            return false;
        }
        Scanner scan = new Scanner(System.in);
        int r = -1;
        int c = -1;
        while (true){
            try {
                if(r == -1){
                    System.out.print("Barrier row: ");
                    r = scan.nextInt();
                    if(r < 0 || r >= height) {
                        r = -1;
                        throw new Exception("Please enter a number between 0 and " + height);
                    }
                }
                if(c == -1){
                    System.out.print("Barrier col: ");
                    c = scan.nextInt();
                    if(c < 0 || c >= length) {
                        c = -1;
                        throw new Exception("Please enter a number between 0 and " + length);
                    }
                }
                char cell = map[r][c];
                if(cell == '~')  break;
                else {
                    System.out.println("Barriers can only be placed on clean water, away from the vessel");
                    return false;
                }

            }
            catch (InputMismatchException k){
                System.out.println("Text input is not allowed. Please enter numbers only.");
                scan.nextLine();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                scan.nextLine();
            }
        }
        map[r][c] = '#';
        barriersLeft--;
        System.out.println("Barrier placed at (" + r + "," + c + ").");
        return true;
    }
    public void vesselMove(){
        Scanner scan = new Scanner(System.in);
        String act;
        String nameDirect = "None";
        int direct = -1;
        loop1:
        while (direct == -1) {
            System.out.print("Direction (N/E/S/W): ");
            act = scan.nextLine();
            act = act.toLowerCase();
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
            };
        }
        int moveDistance = -1;
        while (true){
            System.out.print("Number of cells: ");
            try {
                moveDistance = scan.nextInt();
                if(moveDistance < 1 || moveDistance > 4) throw new Exception();
            }
            catch (Exception e){
                System.out.println("Please enter a number between 1 and 4");
                scan.nextLine();
                continue;
            }
            break;
        }
        int[] pos = new int[]{0,0};
        String stopMessage = "The vessel cannot sail " + nameDirect + ": ";
        int step = 1;
        if(nameDirect.equals("north") || nameDirect.equals("west")) step = -1;
        for(int i = step; Math.abs(i) <= moveDistance; i+=step){
            pos[direct] = i;
            int[] positionNew = new int[]{vesselPosition[0] + pos[0], vesselPosition[1] + pos[1]};
            if(positionNew[0] >= height || positionNew[0] < 0 || positionNew[1] >= length || positionNew[1] < 0){
                System.out.println(stopMessage + "the edge of the board is in the way");
                pos[direct] -= step;
                break;
            }
            char cell = map[positionNew[0]][positionNew[1]];
            if(cell == 'S' || cell == 'R' || cell == '#' || cell == 'X'){
                System.out.println(stopMessage + ": There is an obstacle in the way");
                pos[direct] -= step;
                break;
            }
        }
        vesselPosition[0] += pos[0];
        vesselPosition[1] += pos[1];
        int clld = collectOil();
        System.out.println("The vessel is at (" + vesselPosition[0] + "," + vesselPosition[1]
                + ") and skimmed " + clld + " oil cell(s).");
    }

    public void view(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                if(i == vesselPosition[0] && j == vesselPosition[1]) System.out.print("V ");
                else System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
