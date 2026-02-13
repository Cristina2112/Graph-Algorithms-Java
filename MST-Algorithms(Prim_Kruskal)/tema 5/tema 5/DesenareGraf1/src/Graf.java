
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Graf
{	
    private static void initUI(boolean grafOrientat) {
        JFrame f = new JFrame("Algoritmica Grafurilor - Arborele de Cost Minim (Prim)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // creez panel-ul și setez tipul grafului
        MyPanel panel = new MyPanel();
        panel.setGrafOrientat(grafOrientat);

        // Creez un panou pentru butoane
        JPanel buttonPanel = new JPanel();

        JButton calculateMSTButton = new JButton("Calculează MST Prim");
        calculateMSTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.calculateMST();
            }
        });

        JButton resetMSTButton = new JButton("Reset MST");
        resetMSTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.resetMST();
            }
        });

        JButton calculateMSTKruskalButton = new JButton("Calculează MST Kruskal");
        calculateMSTKruskalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.calculateMSTKruskal();
            }
        });



        buttonPanel.add(calculateMSTButton);
        buttonPanel.add(resetMSTButton);
        buttonPanel.add(calculateMSTKruskalButton);

        f.setLayout(new BorderLayout());
        f.add(panel, BorderLayout.CENTER);
        f.add(buttonPanel, BorderLayout.SOUTH);

        f.setSize(800, 600);
        f.setVisible(true);
    }
	
    public static void main(String[] args)
    {
        /*
        int raspuns = JOptionPane.showConfirmDialog(
                null,
                "Vrei să desenezi un graf orientat?",
                "Tip graf",
                JOptionPane.YES_NO_OPTION
        );

        boolean grafOrientat = (raspuns == JOptionPane.YES_OPTION);
        */

        //graful trebuie să fie neorientat pt MST Prim si Kruskal
        boolean grafOrientat = false;

        // pornesc UI-ul Swing
        SwingUtilities.invokeLater(() -> initUI(grafOrientat));
    }	
}

