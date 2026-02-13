import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Labirint labirint = new Labirint("maze.txt");
        JFrame frame = new JFrame("Labirint BFS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PanouLabirint(labirint));
        frame.pack();
        frame.setVisible(true);
    }
}
