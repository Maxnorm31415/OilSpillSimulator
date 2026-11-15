import java.util.ArrayList;
import java.util.Random;

public class Grid {
    private final int height = 8;
    private final int length = 12;
    private final int rockCount = 6;
    private static int index = 0;
    private char[][] map = new char[height][length];
    private int[] vesselPosition = {4,0};
    private String[] directions = new String[] {"East", "South", "West", "North"};
    private String currentDirection = "East";
    private ArrayList<oil> ListOils = new ArrayList<oil>();
    private int[][] positionOils = new int[][]{{1,4}, {4,7}};
    private int[][] shoreWidths = new int[][]{{0,2}, {0,1}, {0,2}, {0,1}
            , {0,1}, {0,1}, {0,2}, {3,3}};

    // Needs improvement //
    private void generateOilPatch(){
        for(int i = 0; i < positionOils.length; i++){
            ListOils.add(new oil(positionOils[i][0], positionOils[i][1], index++));
            ListOils.add(new oil(positionOils[i][0], positionOils[i][1]+1, index++));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1], index++));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1]+1, index++));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1]+2, index++));
            ListOils.add(new oil(positionOils[i][0]+2, positionOils[i][1]+1, index++));
        }
        for(int i = 0; i < ListOils.size(); i++){
            map[ListOils.get(i).getPosition()[0]][ListOils.get(i).getPosition()[1]] = 'o';
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
        Random random = new Random();
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
            if(direction == directions[i]) return i;
        }
        return -1;
    }

    private void clearOil(int ix){
        ListOils.remove(ix);
        for(int i = ix; i < ListOils.size(); i++){
            ListOils.get(i).setIndex(i);
        }
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
        System.out.println(ListOils.size());
        ArrayList<oil> oilToMove = new ArrayList<>();
        ArrayList<oil> oilToShore = new ArrayList<>();
        int[] direction;
        switch (currentDirection){
            case "East":
                direction = new int[]{0,1};
                break;
            case "West":
                direction = new int[]{0,-1};
                break;
            case "South":
                direction = new int[]{-1,0};
                break;
            case "North":
                direction = new int[]{1,0};
                break;
            default:
                System.out.println(" Error in the switch statement in the move() method - Direction: " + currentDirection);
                direction = new int[]{0,0};
                break;
        }
        for(int i = 0; i < ListOils.size(); i++){
            int[] directNew = new int[]{direction[0] + ListOils.get(i).getPosition()[0],
                    direction[1] + ListOils.get(i).getPosition()[1]};
            char cell = map[directNew[0]][directNew[1]];
            if(cell == '~'){
                oilToMove.add(ListOils.get(i));
                continue;
            }
            if(cell == 'S' || cell == 'X'){
                oilToShore.add(ListOils.get(i));
            }
        }
        for(int i = 0; i < oilToMove.size(); i++){
            int[] oldPosition = new int[]{oilToMove.get(i).getPosition()[0],
                    oilToMove.get(i).getPosition()[1]};
            map[oldPosition[0]][oldPosition[1]] = '~';
            oilToMove.get(i).setPosition(oldPosition[0] + direction[0], oldPosition[1] + direction[1]);
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'o';
        }
        for(int i = 0; i < oilToShore.size(); i++){
            int[] oldPosition = new int[]{oilToShore.get(i).getPosition()[0],
                    oilToShore.get(i).getPosition()[1]};
            map[oldPosition[0]][oldPosition[1]] = '~';
            map[oldPosition[0] + direction[0]][oldPosition[1] + direction[1]] = 'X';
            clearOil(oilToShore.get(i).getIndex());
        }
    }

    public void view(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }
}
