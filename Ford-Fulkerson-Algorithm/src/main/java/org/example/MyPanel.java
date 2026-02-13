package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MyPanel extends JPanel {
    private List<Nod> nods;
    private List<Arc> arcs;
    private Map<Arc, Integer> flows;
    private Set<Arc> minCut;

    private Nod draggedNod;
    private Nod sourceNod;
    private Point dragPoint;
    private Main parentFrame;
    private boolean isMovingNode = false; // flag pentru a diferenția mutare vs creare arc

    public MyPanel(Main parent) {
        this.parentFrame = parent;
        nods = new ArrayList<>();
        arcs = new ArrayList<>();
        flows = new HashMap<>();
        minCut = new HashSet<>();

        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void handleMouseClicked(MouseEvent e) {
        // adauga nod la click stânga
        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
            Nod clickedNod = findNode(e.getX(), e.getY());
            if (clickedNod == null) {
                nods.add(new Nod(nods.size(), e.getX(), e.getY()));
                parentFrame.updateStatus("Nod " + (nods.size() - 1) + " adăugat");
                repaint();
            }
        }
        // double-click stânga pe nod îl șterge
        else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            Nod clickedNod = findNode(e.getX(), e.getY());
            if (clickedNod != null) {
                deleteNode(clickedNod);
            }
        }
        // click dreapta pe nod mutare
        else if (e.getButton() == MouseEvent.BUTTON3) {
            Nod clickedNod = findNode(e.getX(), e.getY());
            if (clickedNod != null) {
                draggedNod = clickedNod;
                sourceNod = clickedNod;
                dragPoint = e.getPoint();
            }
        }
    }

    private void handleMousePressed(MouseEvent e) {
        draggedNod = findNode(e.getX(), e.getY());
        if (draggedNod != null) {
            sourceNod = draggedNod;
            dragPoint = e.getPoint();
            // click dreapta = mutare nod, click stânga = creare arc
            isMovingNode = (e.getButton() == MouseEvent.BUTTON3);
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (draggedNod != null) {
            dragPoint = e.getPoint();
            if (isMovingNode) {
                draggedNod.x = e.getX();
                draggedNod.y = e.getY();
            }
            repaint();
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        if (draggedNod != null) {

            if (isMovingNode) {
                sourceNod.x = e.getX();
                sourceNod.y = e.getY();
                parentFrame.updateStatus("Nod " + sourceNod.id + " mutat");
            }
            // creare arc
            else {
                Nod targetNod = findNode(e.getX(), e.getY());
                if (targetNod != null && targetNod != sourceNod) {
                    createEdge(sourceNod.id, targetNod.id);
                    parentFrame.updateStatus("Arc creat: " + sourceNod.id + " → " + targetNod.id);
                }
            }

            draggedNod = null;
            sourceNod = null;
            dragPoint = null;
            isMovingNode = false;
            repaint();
        }
    }

    private void createEdge(int from, int to) {
        // verif daca arcul există deja
        for (Arc arc : arcs) {
            if (arc.from == from && arc.to == to) {
                JOptionPane.showMessageDialog(this, "Arcul există deja!");
                return;
            }
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField capacityField = new JTextField("10");
        JTextField flowField = new JTextField("0");


        panel.add(new JLabel("Flux inițial:"));
        panel.add(flowField);
        panel.add(new JLabel("Capacitate:"));
        panel.add(capacityField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Arc " + from + " → " + to, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int capacity = Integer.parseInt(capacityField.getText());
                int flow = Integer.parseInt(flowField.getText());

                if (capacity < 0 || flow < 0 || flow > capacity) {
                    JOptionPane.showMessageDialog(this, "Valori invalide! (capacitate ≥ 0, 0 ≤ flux ≤ capacitate)");
                    return;
                }

                arcs.add(new Arc(from, to, capacity, flow));
                repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduceți numere valide!");
            }
        }
    }

    private void deleteNode(Nod nod) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Ștergeți nodul " + nod.id + " și toate arcele conectate?",
                "Confirmare", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // sterge toate arcele care conțin acest nod
            arcs.removeIf(arc -> arc.from == nod.id || arc.to == nod.id);

            // sterge nodul
            int deletedId = nod.id;
            nods.remove(nod);

            // re-indexează nodurile și arcele
            for (int i = 0; i < nods.size(); i++) {
                int oldId = nods.get(i).id;
                nods.get(i).id = i;

                // actualizează arcele
                for (Arc arc : arcs) {
                    if (arc.from == oldId) arc.from = i;
                    if (arc.to == oldId) arc.to = i;
                }
            }

            parentFrame.updateStatus("Nod " + deletedId + " șters");
            repaint();
        }
    }

    private Nod findNode(int x, int y) {
        for (Nod nod : nods) {
            if (nod.contains(x, y)) {
                return nod;
            }
        }
        return null;
    }

    public void setFlows(Map<Arc, Integer> flows) {
        this.flows = new HashMap<>(flows);
        repaint();
    }

    public void setMinCut(Set<Arc> minCut) {
        this.minCut = new HashSet<>(minCut);
        repaint();
    }

    public void resetFlow() {
        flows.clear();
        minCut.clear();
        for (Arc arc : arcs) {
            arc.flow = 0;
        }
        parentFrame.updateStatus("Fluxuri resetate");
        repaint();
    }

    public void clear() {
        nods.clear();
        arcs.clear();
        flows.clear();
        minCut.clear();
        parentFrame.updateStatus("Rețea ștearsă ");
        repaint();
    }

    public List<Nod> getNodes() { return nods; }
    public List<Arc> getEdges() { return arcs; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // desenare arce
        for (Arc arc : arcs) {
            Nod fromNod = nods.get(arc.from);
            Nod toNod = nods.get(arc.to);

            boolean isMinCut = minCut.contains(arc);
            int currentFlow = flows.getOrDefault(arc, arc.flow);

            g2.setColor(isMinCut ? Color.RED : Color.BLACK);  //colorare taietura min
            g2.setStroke(new BasicStroke(isMinCut ? 3 : 2));

            drawArrow(g2, fromNod.x, fromNod.y, toNod.x, toNod.y);

            // label cu capacitate/flux
            int midX = (fromNod.x + toNod.x) / 2;
            int midY = (fromNod.y + toNod.y) / 2;

            String label = currentFlow + "/" + arc.capacity;

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            // pt lizibilitate label
            g2.setColor(Color.WHITE);
            g2.fillRect(midX + 3, midY - 15, labelWidth + 4, 16);

            g2.setColor(Color.BLACK);
            g2.drawString(label, midX + 5, midY - 5);
        }

        // desenare linie punctata intitial
        if (draggedNod != null && dragPoint != null && sourceNod != null && !isMovingNode) {
            g2.setColor(new Color(100, 100, 100, 150));
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
            g2.drawLine(sourceNod.x, sourceNod.y, dragPoint.x, dragPoint.y);
        }

        // desenare noduri
        for (Nod nod : nods) {
            g2.setColor(new Color(255, 105, 180)); // Roz (HotPink)
            g2.fillOval(nod.x - Nod.RADIUS, nod.y - Nod.RADIUS,
                    2 * Nod.RADIUS, 2 * Nod.RADIUS);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(nod.x - Nod.RADIUS, nod.y - Nod.RADIUS,
                    2 * Nod.RADIUS, 2 * Nod.RADIUS);

            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            String id = String.valueOf(nod.id);
            g2.drawString(id, nod.x - fm.stringWidth(id)/2, nod.y + fm.getAscent()/2 - 2);
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);

        int startX = (int)(x1 + Nod.RADIUS * Math.cos(angle));
        int startY = (int)(y1 + Nod.RADIUS * Math.sin(angle));
        int endX = (int)(x2 - Nod.RADIUS * Math.cos(angle));
        int endY = (int)(y2 - Nod.RADIUS * Math.sin(angle));

        g2.drawLine(startX, startY, endX, endY);

        int arrowSize = 10;
        int dx = (int)(arrowSize * Math.cos(angle - Math.PI / 6));
        int dy = (int)(arrowSize * Math.sin(angle - Math.PI / 6));
        g2.drawLine(endX, endY, endX - dx, endY - dy);

        dx = (int)(arrowSize * Math.cos(angle + Math.PI / 6));
        dy = (int)(arrowSize * Math.sin(angle + Math.PI / 6));
        g2.drawLine(endX, endY, endX - dx, endY - dy);
    }
}

