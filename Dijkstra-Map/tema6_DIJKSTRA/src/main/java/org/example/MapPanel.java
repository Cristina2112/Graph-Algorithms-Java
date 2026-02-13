package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MapPanel extends JPanel {
    private GraphMap graph;
    private Node startNode = null;
    private Node endNode = null;
    private List<Node> currentPath = new ArrayList<>();

    private BufferedImage cachedMapImage = null;
    private boolean isReady = false; //false la incarcare fisier


    //flag pt masurare timp colorare
    private boolean measureColoringTime = false;

    public MapPanel(int width, int height) {
        graph = new GraphMap();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(width, height));

        //thread pt incarcare harta
        new Thread(() -> {
            long tStartLoad = System.currentTimeMillis();

            // cautare fișier
            String[] paths = {"hartaLuxembourg.xml", "src/main/resources/hartaLuxembourg.xml"};
            String xmlPath = "hartaLuxembourg.xml";
            for (String p : paths) if (new java.io.File(p).exists()) xmlPath = p;

            graph.loadFromXML(xmlPath, width, height);
            prepareMapImage(width, height);

            long tEndLoad = System.currentTimeMillis();
            System.out.println("Timp afisare harta: " + (tEndLoad - tStartLoad) + " ms");

            isReady = true;
            SwingUtilities.invokeLater(this::repaint);
        }).start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isReady) return;

                Node clicked = graph.getNearestNode(e.getX(), e.getY());
                if (clicked == null) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    startNode = clicked;
                    currentPath.clear();
                    System.out.println("--------------------------------");
                    System.out.println("ID Nod Start: " + startNode.getId());

                } else if (SwingUtilities.isRightMouseButton(e)) {
                    endNode = clicked;
                    System.out.println("ID Nod Stop: " + endNode.getId());
                }

                if (startNode != null && endNode != null) {
                    long t1 = System.nanoTime();
                    currentPath = DijkstraSolver.solve(startNode, endNode, graph);
                    long t2 = System.nanoTime();

                    System.out.println("Timp gasire drum: " + (t2 - t1) / 1_000_000.0 + " ms");
                    measureColoringTime = true;
                }
                repaint();
            }
        });
    }

    private void prepareMapImage(int w, int h) {
        cachedMapImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = cachedMapImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(200, 200, 200)); //colorare strazi

        Map<Integer, Node> nodes = graph.getNodes();
        Map<Integer, List<Edge>> adj = graph.getAdjacencyList();

        for (Map.Entry<Integer, List<Edge>> entry : adj.entrySet()) {
            Node n1 = nodes.get(entry.getKey());
            if (n1 == null) continue;
            for (Edge e : entry.getValue()) {
                Node n2 = nodes.get(e.getTargetNodeId());
                if (n2 != null) {
                    g2.drawLine(n1.getScreenX(), n1.getScreenY(), n2.getScreenX(), n2.getScreenY());
                }
            }
        }
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isReady) {
            g.setColor(Color.BLACK);
            g.drawString("incarcare...",getWidth()/2 - 50, getHeight()/2);
            return;
        }

        g.drawImage(cachedMapImage, 0, 0, null); //harta

        // desenare drum roșu
        if (!currentPath.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g;

            //timp pt desenarea drumului
            long tStartColor = System.nanoTime();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));

            // unire noduri pt pregatire drum colorat
            int[] xPoints = new int[currentPath.size()];
            int[] yPoints = new int[currentPath.size()];
            for(int i=0; i<currentPath.size(); i++) {
                xPoints[i] = currentPath.get(i).getScreenX();
                yPoints[i] = currentPath.get(i).getScreenY();
            }
            g2.drawPolyline(xPoints, yPoints, currentPath.size());

            long tEndColor = System.nanoTime();

            if (measureColoringTime) {
                System.out.println("Timp colorare drum: " + (tEndColor - tStartColor) / 1_000_000.0 + " ms");
                System.out.println("--------------------------------");
                measureColoringTime = false; // reesetare flag
            }
        }

        if (startNode != null) drawDot((Graphics2D)g, startNode, Color.BLUE, "START");
        if (endNode != null) drawDot((Graphics2D)g, endNode, new Color(0, 150, 0), "STOP");
    }

    private void drawDot(Graphics2D g, Node n, Color c, String label) {
        g.setColor(c);
        g.fillOval(n.getScreenX() - 6, n.getScreenY() - 6, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString(label, n.getScreenX() + 8, n.getScreenY());
    }
}