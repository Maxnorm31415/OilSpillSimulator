import java.util.Scanner;

public static void main(String[] args) throws InterruptedException {
    int round = 1;
    Scanner scan = new Scanner(System.in);
    Grid grid = new Grid();
    while (!grid.gameover()) {
        System.out.println("\n--- Round " + round + " ---");
        grid.view();
        grid.getInfo();
        loop:
        while (true){
            System.out.print("Action (B = place barrier, V = sail vessel): ");
            String input = scan.nextLine();
            input = input.toLowerCase();
            switch (input){
                case "b":
                    if (grid.placeBarrier()) break loop;
                    else continue loop;
                case "v":
                    grid.vesselMove();
                    break loop;
                default:
                    System.out.println("Invalid input. Please enter V(v) to sail the vessel or B(b) to place a barrier.");
                    continue loop;
            }
        }
        grid.move();
        round++;
        Thread.sleep(500);
    }
}
