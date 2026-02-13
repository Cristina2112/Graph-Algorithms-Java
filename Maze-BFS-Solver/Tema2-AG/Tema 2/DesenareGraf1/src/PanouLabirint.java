import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PanouLabirint extends JPanel {
    private Labirint labirint;
    private int marimeCelula = 45;
    private Nod start = null;
    private List<List<Nod>> drumuri;
    private int indexDrumCurent = 0;
    private int offsetX, offsetY; //pt centrare labirint
    private List<Nod> iesiriInaccesibile = new ArrayList<>();

    private int nrDrumuriGasite = 0;
    private int nrIesiriInaccesibile = 0;

    // control starea de resetare
    private boolean animatieInDesfasurare = false;

    private static final Color PATH_COLOR_GREEN = Color.GREEN;
    private static final int GROSIME_LINIE = 4;
    private static final int MARIME_OVAL = 10;

    private static final int INALTIME_STATISTICI = 120;

    public PanouLabirint(Labirint labirint) {
        this.labirint = labirint;
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.white);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (start != null && !animatieInDesfasurare) {
                    start = null;
                    drumuri = null;
                    iesiriInaccesibile.clear();
                    nrDrumuriGasite = 0;
                    nrIesiriInaccesibile = 0;
                    repaint();
                    return;
                }


                // Dacă startul e setat si animatia ruleaza, ignora click-ul.
                if (start != null && animatieInDesfasurare) return;

                calculeazaCentrare();
                int col = (e.getX() - offsetX) / marimeCelula;
                int lin = (e.getY() - offsetY) / marimeCelula;

                Nod[][] noduri = labirint.getNoduri();
                if (lin >= 0 && lin < noduri.length && col >= 0 && col < noduri[0].length && noduri[lin][col] != null) {
                    start = noduri[lin][col];
                    drumuri = labirint.gasesteDrumuri(start);

                    initializari();

                    indexDrumCurent = 0;
                    repaint();
                    afiseazaDrumuriSuccesiv();
                }
            }
        });
    }

    private void initializari() {
        iesiriInaccesibile.clear();
        nrDrumuriGasite = 0;
        nrIesiriInaccesibile = 0;

        List<Nod> toateIesirile = labirint.getIesiri();

        for (int i = 0; i < drumuri.size(); i++) {
            List<Nod> drum = drumuri.get(i);
            if (drum == null) {
                iesiriInaccesibile.add(toateIesirile.get(i));
                nrIesiriInaccesibile++;
            } else {
                nrDrumuriGasite++;
            }
        }
    }

    private void afiseazaDrumuriSuccesiv() {
        indexDrumCurent = 0;
        animatieInDesfasurare = true;
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            if (indexDrumCurent >= drumuri.size()) {
                ((Timer)e.getSource()).stop();
                animatieInDesfasurare = false;
                repaint();
                return;
            }

            while (indexDrumCurent < drumuri.size() && drumuri.get(indexDrumCurent) == null) { // sarim iesirile inaccesibile
                indexDrumCurent++;
            }

            if (indexDrumCurent < drumuri.size()) {
                indexDrumCurent++;
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
                animatieInDesfasurare = false;
                repaint();
            }
        });
        timer.start();
    }

    private void calculeazaCentrare() {
        int w = getWidth(); //latime panou
        int h = getHeight();  //inaltime panou
        int mazeW = labirint.getColoane() * marimeCelula;
        int mazeH = labirint.getRanduri() * marimeCelula;

        // centrare orizontala
        offsetX = (w - mazeW) / 2;

        // centrare verticala
        int inaltimeUtila = h - INALTIME_STATISTICI;
        offsetY = (inaltimeUtila - mazeH) / 2;

    }

    private void deseneazaDrumLinii(Graphics g, List<Nod> path) {
        if (path == null || path.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(PATH_COLOR_GREEN);
        g2d.setStroke(new BasicStroke(GROSIME_LINIE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // 1. Desenam liniile intre noduri
        for (int i = 0; i < path.size() - 1; i++) {
            Nod current = path.get(i);
            Nod next = path.get(i + 1);

            int x1 = offsetX + current.getColoana() * marimeCelula + marimeCelula / 2;
            int y1 = offsetY + current.getLinie() * marimeCelula + marimeCelula / 2;

            int x2 = offsetX + next.getColoana() * marimeCelula + marimeCelula / 2;
            int y2 = offsetY + next.getLinie() * marimeCelula + marimeCelula / 2;

            g2d.drawLine(x1, y1, x2, y2);
        }

        // 2. Desenam punctele/ovalele pe fiecare nod
        g2d.setColor(PATH_COLOR_GREEN);
        for (Nod n : path) {
            if (n == start || iesiriInaccesibile.contains(n)) continue;

            int x = offsetX + n.getColoana() * marimeCelula + (marimeCelula - MARIME_OVAL) / 2;
            int y = offsetY + n.getLinie() * marimeCelula + (marimeCelula - MARIME_OVAL) / 2;
            g2d.fillOval(x, y, MARIME_OVAL, MARIME_OVAL);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        calculeazaCentrare();
        int[][] matrice = labirint.getMatrice();

        // desenare labirint
        for (int i = 0; i < labirint.getRanduri(); i++) {
            for (int j = 0; j < labirint.getColoane(); j++) {
                int x = offsetX + j * marimeCelula;
                int y = offsetY + i * marimeCelula;
                if (matrice[i][j] == 0) g.setColor(Color.BLACK);
                else g.setColor(Color.WHITE);
                g.fillRect(x, y, marimeCelula, marimeCelula);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, marimeCelula, marimeCelula);
            }
        }

        //desen iesirile inaccesibile (rosu)
        g.setColor(Color.RED);
        for (Nod iesire : iesiriInaccesibile) {
            int x = offsetX + iesire.getColoana() * marimeCelula;
            int y = offsetY + iesire.getLinie() * marimeCelula;
            g.fillRect(x, y, marimeCelula, marimeCelula);
        }

        // desen drum curent (verde)
        if (drumuri != null && indexDrumCurent > 0 && indexDrumCurent <= drumuri.size()) {
            // indexDrumCurent - 1 deoarece indexul este incrementat la Pasul 6 din Timer
            List<Nod> drum = drumuri.get(indexDrumCurent - 1);

            if (drum != null) {
                deseneazaDrumLinii(g, drum);
            }
        }

        // desen punct start (albastru)
        if (start != null) {
            g.setColor(Color.BLUE);
            int x = offsetX + start.getColoana() * marimeCelula;
            int y = offsetY + start.getLinie() * marimeCelula;
            g.fillRect(x, y, marimeCelula, marimeCelula);
        }

        // info sub labirint
        if (start != null) {
            int mazeH = labirint.getRanduri() * marimeCelula;

            int distantaSubLabirint = 30;
            int textY = offsetY + mazeH + distantaSubLabirint;

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));

            String drumuriText = "Drumuri găsite : " + nrDrumuriGasite;
            String inaccesibileText = "Ieșiri inaccesibile : " + nrIesiriInaccesibile;

            FontMetrics fm = g.getFontMetrics();

            int w1 = fm.stringWidth(drumuriText);
            g.drawString(drumuriText, offsetX + (labirint.getColoane() * marimeCelula - w1) / 2, textY);

            int w2 = fm.stringWidth(inaccesibileText);
            g.drawString(inaccesibileText, offsetX + (labirint.getColoane() * marimeCelula - w2) / 2, textY + 25);
        }
    }
}