public class oil {
    private int [] position;
    public oil(int r, int c){
        position = new int[]{r,c};
    }

    public void setPosition(int r, int c){
        position = new int[]{r,c};
    }

    public int[] getPosition(){
        return position;
    }
}
