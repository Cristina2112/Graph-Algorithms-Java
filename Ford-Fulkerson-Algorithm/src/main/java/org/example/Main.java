package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class Main extends JFrame {
    private MyPanel myPanel;
    private JButton calculateMaxFlowBtn, clearBtn, resetFlowBtn;
    private JLabel statusLabel;

    public Main() {
        setTitle("Flux maxim și taietură minima");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // panel
        myPanel = new MyPanel(this);
        add(myPanel, BorderLayout.CENTER);

        // pt butoane
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        calculateMaxFlowBtn = new JButton("Calculează flux max ");
        resetFlowBtn = new JButton("Resetează flux ");
        clearBtn = new JButton("Șterge ");

        calculateMaxFlowBtn.addActionListener(e -> calculateMaxFlow());
        resetFlowBtn.addActionListener(e -> myPanel.resetFlow());
        clearBtn.addActionListener(e -> myPanel.clear());

        // adugare taste scurte
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyCode() == KeyEvent.VK_F) {
                        calculateMaxFlow();
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_R) {
                        myPanel.resetFlow();
                        return true;
                    } else if (e.getKeyCode() == KeyEvent.VK_C) {
                        myPanel.clear();
                        return true;
                    }
                }
                return false;
            }
        });

        // butoane sus
        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topButtonPanel.add(calculateMaxFlowBtn);
        topButtonPanel.add(resetFlowBtn);
        controlPanel.add(topButtonPanel, BorderLayout.NORTH);

        //butonul Șterge jos
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomButtonPanel.add(clearBtn);
        controlPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);

        //status bar jos
        statusLabel = new JLabel("");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void calculateMaxFlow() {
        if (myPanel.getNodes().size() < 2) {
            JOptionPane.showMessageDialog(this, "Adăugați cel puțin 2 noduri!");
            return;
        }

        String sourceStr = JOptionPane.showInputDialog(this, "Introduceți nodul sursă (0-" + (myPanel.getNodes().size()-1) + "):");
        String sinkStr = JOptionPane.showInputDialog(this, "Introduceți nodul destinație (0-" + (myPanel.getNodes().size()-1) + "):");

        if (sourceStr == null || sinkStr == null) return;

        try {
            int source = Integer.parseInt(sourceStr);
            int sink = Integer.parseInt(sinkStr);

            if (source < 0 || source >= myPanel.getNodes().size() ||
                    sink < 0 || sink >= myPanel.getNodes().size() || source == sink) {
                JOptionPane.showMessageDialog(this, "Noduri invalide!");
                return;
            }

            FordFulkerson ff = new FordFulkerson(myPanel.getNodes().size());
            for (Arc arc : myPanel.getEdges()) {
                ff.addEdge(arc.from, arc.to, arc.capacity, arc.flow);
            }

            int maxFlow = ff.getMaxFlow(source, sink);
            Set<Arc> minCut = ff.getMinCut(source);

            myPanel.setFlows(ff.getFlows());
            myPanel.setMinCut(minCut);

            statusLabel.setText("Flux Maxim: " + maxFlow + " | Tăietură minimă evidențiată cu roșu");

            JOptionPane.showMessageDialog(this,
                    "Flux Maxim: " + maxFlow + "\n" +
                            "Tăietură Minimă: " + minCut.size() + " arce\n" +
                            "Capacitate tăietură: " + ff.getMinCutCapacity(minCut),
                    "Rezultat", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduceți numere valide!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
