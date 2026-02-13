import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class CondensedGraphPanel extends JPanel {

    private final List<Node> nodes; // Nodurile CTC
    private final List<String> labels; // Etichetele CTC (continutul componentelor)
    private final GraphResults.CTCResult result; // Rezultatul CTC cu muchiile condensate
    private final int NODE_DIAM = 60;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 600;

    public CondensedGraphPanel(GraphResults.CTCResult result) {
        this.result = result;
        this.nodes = new ArrayList<>();
        this.labels = new ArrayList<>();

        for (int i = 0; i < result.sccsList.size(); i++) {
            this.nodes.add(new Node(0, 0, i + 1));

            // Eticheta e convertita la string (ex: "[1, 2, 5]")
            String label = result.sccsList.get(i).toString();
            // Elimină parantezele drepte (ex: "1, 2, 5")
            label = label.substring(1, label.length() - 1);

            this.labels.add(label);
        }

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    }

    private Node findNodeByNumber(List<Node> lst, int num) {
        for (Node n : lst) {
            if (n.getNumber() == num) return n;
        }
        return null;
    }


    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(Color.RED);
        int arrowLength = 10;
        int arrowAngleDegrees = 30;

        // 1. Calculul Unghiului și Punctului de Oprire (circumferință)
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.hypot(dx, dy);
        if (dist == 0) return;

        double angle = Math.atan2(dy, dx);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Punctul de oprire (xStop, yStop) pe circumferința nodului destinatie
        int xStop = (int) (x2 - (NODE_DIAM / 2) * cos);
        int yStop = (int) (y2 - (NODE_DIAM / 2) * sin);

        // 2. Desenează linia principală
        g.drawLine(x1, y1, xStop, yStop);

        // 3. Desenează Capul Săgeții (două linii simple)

        // laterala 1 (unghiul principal + 30 grade)
        double angle1 = angle + Math.toRadians(arrowAngleDegrees);
        int xP1 = (int) (xStop - arrowLength * Math.cos(angle1));
        int yP1 = (int) (yStop - arrowLength * Math.sin(angle1));

        // laterala 2 (unghiul principal - 30 grade)
        double angle2 = angle - Math.toRadians(arrowAngleDegrees);
        int xP2 = (int) (xStop - arrowLength * Math.cos(angle2));
        int yP2 = (int) (yStop - arrowLength * Math.sin(angle2));

        g.drawLine(xStop, yStop, xP1, yP1);
        g.drawLine(xStop, yStop, xP2, yP2);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.drawString("Graful Condensat", 10, 20);

        // poziționarea și desenarea nodurilor și muchiilor
        int n = nodes.size();
        int R = Math.min(getWidth(), getHeight()) / 2 - NODE_DIAM;
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            Node node = nodes.get(i);
            // Salvează poziția în obiectul Node
            node.setCoordX((int)(cx + R * Math.cos(angle)) - NODE_DIAM / 2);
            node.setCoordY((int)(cy + R * Math.sin(angle)) - NODE_DIAM / 2);
        }

        // desenare muchii graf condensat
        for (GraphResults.CTCResult.Pair<Integer, Integer> edge : result.condensedEdges) {
            // Nodurile sunt stocate in lista cu numarul = Index CTC + 1
            Node a = findNodeByNumber(nodes, edge.source + 1);
            Node b = findNodeByNumber(nodes, edge.destination + 1);

            if (a == null || b == null) continue;

            // Coordonatele folosesc centrul nodului
            drawArrow(g, a.getCoordX() + NODE_DIAM / 2, a.getCoordY() + NODE_DIAM / 2,
                    b.getCoordX() + NODE_DIAM / 2, b.getCoordY() + NODE_DIAM / 2);
        }

        //desenare noduri si etichete
        g.setFont(new Font("TimesRoman", Font.BOLD, 13));
        for (int i = 0; i < nodes.size(); i++) {
            Node nNode = nodes.get(i);

            // Culoare nod= Albastru
            g.setColor(Color.BLUE);
            g.fillOval(nNode.getCoordX(), nNode.getCoordY(), NODE_DIAM, NODE_DIAM);
            g.setColor(Color.BLACK);
            g.drawOval(nNode.getCoordX(), nNode.getCoordY(), NODE_DIAM, NODE_DIAM);

            // Culoare eticheta = Alb
            g.setColor(Color.WHITE);
            String label = labels.get(i); // Eticheta fără paranteze
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(label);
            int h = fm.getAscent();

            // Desenare etichetă centrată
            g.drawString(label,
                    nNode.getCoordX() + (NODE_DIAM - w) / 2,
                    nNode.getCoordY() + (NODE_DIAM + h) / 2 - 3);
        }
    }


    public static void display(GraphResults.CTCResult result) {
        JFrame f = new JFrame("Graful Condensat");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CondensedGraphPanel panel = new CondensedGraphPanel(result);
        f.add(panel);
        f.pack();
        f.setVisible(true);
    }
}