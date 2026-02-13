
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Graf
{	
    private static void initUI(boolean grafOrientat) {
        JFrame f = new JFrame("Algoritmica Grafurilor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // creez panel-ul și setez tipul grafului
        MyPanel panel = new MyPanel();
        panel.setGrafOrientat(grafOrientat);

        f.add(panel);
        f.setSize(500, 500);
        f.setVisible(true);
    }
	
    public static void main(String[] args)
    {
        int raspuns = JOptionPane.showConfirmDialog(
                null,
                "Vrei să desenezi un graf orientat?",
                "Tip graf",
                JOptionPane.YES_NO_OPTION
        );

        boolean grafOrientat = (raspuns == JOptionPane.YES_OPTION);

        // pornesc UI-ul Swing
        SwingUtilities.invokeLater(() -> initUI(grafOrientat));
    }	
}

