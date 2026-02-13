import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class MyPanel extends JPanel
{
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    Point pointStart = null;
    Point pointEnd = null;
    Node n_start=null;
    Node n_end=null;
    boolean isDragging = false;
    private boolean grafOrientat = true;
    Node nodSelectat = null;
    boolean mutareNod = false;

    private int coordXInitial = -1;
    private int coordYInitial = -1;

    private double costMinimTotal = 0.0;
    private boolean mstCalculated = false;


    public MyPanel()
    {
        listaNoduri = new Vector<Node>();
        listaArce = new Vector<Arc>();

        // borderul panel-ului
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            //evenimentul care se produce la apasarea mousse-ului
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
                n_start=coleziuniNoduri(e.getX(), e.getY());

                if (e.getButton() == MouseEvent.BUTTON3) {
                    mutareNod = true;
                    nodSelectat = n_start;
                    pointStart=null;
                } else {
                    mutareNod = false;
                    nodSelectat = null;
                }

                if (nodSelectat != null) {
                    coordXInitial = nodSelectat.getCoordX();
                    coordYInitial = nodSelectat.getCoordY();
                }

            }

            //evenimentul care se produce la eliberarea mousse-ului
            public void mouseReleased(MouseEvent e) {
                if (mutareNod==true && nodSelectat != null) {
                    if (esteSuprapusCuAltul()) {
                        nodSelectat.setCoordX(coordXInitial);
                        nodSelectat.setCoordY(coordYInitial);
                        System.out.println("Spatiu insuficient - revenire la pozitia initiala.");
                    }

                    nodSelectat = null;
                    mutareNod = false;
                    coordXInitial = coordYInitial = -1;
                    repaint();
                    return;
                }


                if (!isDragging) {
                    if(coleziuniNoduri(e.getX(), e.getY())==null){
                        addNode(e.getX(), e.getY());
                        matriceAdiacenta();
                    }
                }
                else {

                    n_end = coleziunieArce(e.getX(), e.getY());
                    if (n_start != null && n_end != null && n_start != n_end) {
                        // verifica dacă muchia exista deja
                        Arc existingArc = gasireArc(n_start, n_end);
                        if (existingArc == null) {
                            // solicit cost muchie
                            String costStr = JOptionPane.showInputDialog(
                                    MyPanel.this,
                                    "Introduceți costul muchiei (" + n_start.getNumber() + "-" + n_end.getNumber() + "):",
                                    "Introducere cost",
                                    JOptionPane.QUESTION_MESSAGE
                            );

                            int cost = 1; // valoare implicita
                            if (costStr != null && !costStr.isEmpty()) {
                                try {
                                    cost = Integer.parseInt(costStr);
                                    if (cost <= 0) {
                                        JOptionPane.showMessageDialog(MyPanel.this, "Costul trebuie să fie pozitiv!", "Eroare", JOptionPane.ERROR_MESSAGE);
                                        cost = 1;
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(MyPanel.this, "Cost invalid! Se va folosi valoarea 1.", "Eroare", JOptionPane.ERROR_MESSAGE);
                                }
                            }

                            //muchia u -> v
                            Arc arc1 = new Arc(pointStart, pointEnd, n_start, n_end, cost);
                            arc1.setCost(cost);
                            listaArce.add(arc1);

                            // muchia v -> u
                            Arc arc2 = new Arc(pointStart, pointEnd, n_end, n_start, cost);
                            arc2.setCost(cost);
                            listaArce.add(arc2);

                            mstCalculated = false; // resetez flag-ul MST
                            matriceAdiacenta();
                            repaint();
                        } else {
                            JOptionPane.showMessageDialog(MyPanel.this, "Muchia deja există!", "Avertisment", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

                pointStart = null;
                pointEnd=null;
                n_start=null;
                n_end=null;
                isDragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            //evenimentul care se produce la drag&drop pe mousse
            public void mouseDragged(MouseEvent e) {
                pointEnd = e.getPoint();

                if (nodSelectat != null && mutareNod==true) {
                    nodSelectat.setCoordX(e.getX());
                    nodSelectat.setCoordY(e.getY());
                    isDragging=false;
                    repaint();
                    return;
                }

                if (!mutareNod) {
                    isDragging = true;
                    repaint();
                }
            }
        });
    }

    public void setGrafOrientat(boolean orientat) {
        this.grafOrientat = orientat;
    }

    public boolean getGrafOrientat(){
        return grafOrientat;
    }

    private Node coleziuniNoduri(int x,int y){
        for (int i=0;i<listaNoduri.size();i++){
            Node a =listaNoduri.get(i);
            if(a.getCoordX()-30<x && a.getCoordX()+30>x && a.getCoordY()-30<y && a.getCoordY()+30>y){
                //System.out.println("Coliziune noduri");
                return a;
            }

        }
        return null;
    }

    private Node coleziunieArce(int x,int y){
        for (int i=0;i<listaNoduri.size();i++){
            Node a =listaNoduri.get(i);
            if(a.getCoordX()-1<x && a.getCoordX()+30>x && a.getCoordY()-1<y && a.getCoordY()+30>y){
                return a;
            }

        }
        //System.out.println("Coliziune arce");
        return null;
    }

    private boolean esteSuprapusCuAltul() {
        if (nodSelectat == null) return false;

        for (int i = 0; i < listaNoduri.size(); i++) {
            Node n = listaNoduri.get(i);

            if (n != nodSelectat) {
                if (nodSelectat.getCoordX() - 45 < n.getCoordX() &&
                        nodSelectat.getCoordX() + 45 > n.getCoordX()  &&
                        nodSelectat.getCoordY() - 45 < n.getCoordY() &&
                        nodSelectat.getCoordY() + 45 > n.getCoordY()){
                    return true;
                }
            }
        }
        return false;
    }

    //metoda care se apeleaza la eliberarea mouse-ului
    private void addNode(int x, int y) {
        Node node = new Node(x, y, nodeNr);
        listaNoduri.add(node);
        nodeNr++;
        repaint();
    }

    public void matriceAdiacenta(){
        int n=listaNoduri.size();
        int [][] matrice=new int[n][n];

        // inițializare cu 0
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrice[i][j] = 0;
            }
        }

        for(Arc a : listaArce){
            int i1=a.getNodStart().getNumber()-1;
            int i2=a.getNodEnd().getNumber()-1;
            matrice[i1][i2] = 1;
        }

        // scrie matricea în fișier
        try (FileWriter fout = new FileWriter("matrice.txt")) {
            fout.write(n + "\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    fout.write(matrice[i][j] + " ");
                }
                fout.write("\n");
            }
            System.out.println("Fisierul 'matrice.txt' s-a modificat.");
        } catch (IOException e) {
            System.out.println("Eroare la scrierea fisierului: " + e.getMessage());
        }
    }

    // gasire un arc între două noduri
    private Arc gasireArc(Node n1, Node n2) {
        for (Arc a : listaArce) {
            if (a.getNodStart() == n1 && a.getNodEnd() == n2) {
                return a;
            }
            //exista arc invers
            if (a.getNodStart() == n2 && a.getNodEnd() == n1) {
                return a;
            }
        }
        return null;
    }

    //calcul MST folosind alg Prim
    public void calculateMST() {
        if (listaNoduri.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nu există noduri în graf!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (listaArce.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nu există muchii în graf!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MSTPrim mstPrim = new MSTPrim(listaNoduri, listaArce);
        costMinimTotal = mstPrim.calculateMST();
        mstCalculated = true;

        JOptionPane.showMessageDialog(this,
                "Arborele de cost minim calculat (Prim)!\nCostul total: " + costMinimTotal,
                "MST Calculat",
                JOptionPane.INFORMATION_MESSAGE);

        repaint();
    }

    // calcul MST folosind algoritmul Kruskal
    public void calculateMSTKruskal() {
        if (listaNoduri.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nu există noduri în graf!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (listaArce.size() == 0) {
            JOptionPane.showMessageDialog(this, "Nu există muchii în graf!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // resetare vizuală a MST-ului anterior
        for (Arc a : listaArce) {
            a.setIsMSTEdge(false);
        }

        MSTKruskal mstKruskal = new MSTKruskal(listaNoduri, listaArce);
        costMinimTotal = mstKruskal.calculateMST();
        mstCalculated = true;

        JOptionPane.showMessageDialog(this,
                "Arborele de cost minim calculat (Kruskal)!\nCostul total: " + costMinimTotal,
                "MST Calculat",
                JOptionPane.INFORMATION_MESSAGE);

        repaint();
    }

    //resetarea MST
    public void resetMST() {
        for (Arc a : listaArce) {
            a.setIsMSTEdge(false);
        }
        mstCalculated = false;
        costMinimTotal = 0.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawString("This is my Graph!", 10, 20);

        // cost minim MST
        if (mstCalculated) {
            g.setColor(Color.BLUE);
            g.drawString("Costul MST: " + costMinimTotal, 10, 40);
        }

        //deseneaza arcele existente in lista

        for (Arc a : listaArce)
        {
            a.drawArc(g,grafOrientat);
        }
        //deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null)
        {
            g.setColor(Color.RED);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
        }
        //deseneaza lista de noduri
        for(int i=0; i<listaNoduri.size(); i++)
        {
            listaNoduri.elementAt(i).drawNode(g, node_diam);
        }
    }
}