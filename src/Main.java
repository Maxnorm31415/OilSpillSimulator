

public static void main(String[] args) throws InterruptedException {
    Grid start = new Grid();
    while (true) {
        start.view();
        start.move();
        System.out.println("==============================");
        Thread.sleep(1000);
    }
}
