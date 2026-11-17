/* This assignment was completed by Maksym Kibalnyk (stud-kibalnyk@ruc.dk, 83896) and Mihail Tarales (stud-tarales@ruc.dk, 83888)
 Documentation for some libraries was consulted on https://www.w3schools.com/java/
 ChatGPT was used only to correct spelling in the comments*/


/* The Main class displays the map information and the action menu, where the player can
choose to move the vessel or place a barrier. The main game logic is in the Grid class.

The class contains two while loops.
The first loop checks whether the game is over using gameover(). If not, it displays
the map and game information in the console using view() and getinfo().
The second loop allows the player to choose another action after an invalid barrier placement.

According to the mini-project description, invalid barrier placement returns the player
to the action menu, while invalid vessel input must be entered again. If the vessel
encounters an obstacle, it stops before it and the turn continues normally.
Actions can be selected using uppercase (B, V) or lowercase (b, v) letters.

At the end of each round, move() moves the oil in the current direction.
Thread.sleep(500) adds a short delay so the player can read the displayed information,
such as the position of a newly placed barrier. */


import java.util.Scanner; //Used to read user input from the console

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
            }
        }
        grid.move();
        round++;
        Thread.sleep(500);
    }
}
