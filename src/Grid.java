import java.util.ArrayList;

public class Grid {
    private final int height = 8;
    private final int length = 12;
    private char[][] map = new char[height][length];
    private int[] vesselPosition = {4,0};
    private ArrayList<oil> ListOils = new ArrayList<oil>();
    private int[][] positionOils = new int[][]{{1,4}, {4,7}};
    private int[][] shoreWidths = new int[][]{{0,2}, {0,1}, {0,2}, {0,1}
            , {0,1}, {0,1}, {0,2}, {3,3}};

    // Needs improvement //
    private void generateOilPatch(){
        for(int i = 0; i < positionOils.length; i++){
            ListOils.add(new oil(positionOils[i][0], positionOils[i][1]));
            ListOils.add(new oil(positionOils[i][0], positionOils[i][1]+1));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1]));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1]+1));
            ListOils.add(new oil(positionOils[i][0]+1, positionOils[i][1]+2));
            ListOils.add(new oil(positionOils[i][0]+2, positionOils[i][1]+1));
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

    public Grid(){
        for(int i = 0; i < height; i++){
            for (int j = 0; j < length; j++){
                map[i][j] = '~';
            }
        }
        generateOilPatch();
        generateShore();
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
