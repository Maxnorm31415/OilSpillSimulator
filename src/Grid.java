import java.util.ArrayList;
import java.util.Random;

public class Grid {
    private final int height = 8;
    private final int length = 12;
    private final int rockCount = 6;
    private final String[] directions = new String[] {"East", "South", "West", "North"};
    private final int[][] positionOils = new int[][]{{1,4}, {4,7}};
    private final int[][] shoreWidths = new int[][]{{0,2}, {0,1}, {0,2}, {0,1}
            , {0,1}, {0,1}, {0,2}, {3,3}};
    private static int index = 0;
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

    private void clearOil(int ix){
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

    public Grid(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                map[i][j] = '~';
            }
        }
        map[vesselPosition[0]][vesselPosition[1]] = 'V';
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
            map[oldPosition[0]][oldPosition[1]] = '~';
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'X';
            clearOil(Oil.getIndex());
        }
        changeDirection();
    }

    public void view(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(currentDirection);
    }
}
