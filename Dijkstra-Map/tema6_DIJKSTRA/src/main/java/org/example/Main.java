package org.example;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Harta Luxembourg - Dijkstra");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            int width = 1000;
            int height = 800;

            MapPanel panel = new MapPanel(width, height);
            panel.setBackground(new java.awt.Color(240, 240, 240)); // fundal gri
            frame.add(panel);

            frame.setSize(width, height);
            frame.setLocationRelativeTo(null); // centreaza fereastra pe ecran

            frame.setVisible(true);

        });
    }
}
