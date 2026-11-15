public class oil {
    private int [] position;
    private int index = 0;
    public oil(int r, int c, int ix){
        position = new int[]{r,c};
        index = ix;
    }

    public void setPosition(int r, int c){
        position = new int[]{r,c};
    }

    public int[] getPosition(){
        return position;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int ix){
        index = ix;
    }

}
