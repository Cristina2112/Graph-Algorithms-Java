import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
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

    private GraphResults.CTCResult ctcResult = null;
    private List<List<Integer>> ccResult = null;

    // Variabile pentru Problema 4
    private GraphResults.ShortestPathResult spResult = null;
    private int sourceNode;

    // Pentru desenarea secvențială
    private List<List<Integer>> allShortestPaths = new ArrayList<>();
    private int currentPathIndex = -1;

    private int coordXInitial = -1;
    private int coordYInitial = -1;

    private List<Integer> topologicalResult = null;


    public MyPanel()
    {
        listaNoduri = new Vector<Node>();
        listaArce = new Vector<Arc>();

        // borderul panel-ului
        setBorder(BorderFactory.createLineBorder(Color.black));

        // Buton pentru a rula Algoritmul (CC sau CTC)
        JButton runAlgButton = new JButton("Rulează Algoritmul CC/CTC");
        runAlgButton.addActionListener(e -> runAlgorithm());
        this.add(runAlgButton);

        // Buton pentru Sortarea Topologică
        JButton runTopologicalSortButton = new JButton("Sortare Topologică");
        runTopologicalSortButton.addActionListener(e -> runTopologicalSort());
        this.add(runTopologicalSortButton);


        JButton runShortestPathButton = new JButton("Drum Minim (Ex. 4)");
        runShortestPathButton.addActionListener(e -> runShortestPathDAG());
        this.add(runShortestPathButton);

        // Buton pentru afișarea secvențială
        JButton showNextPathButton = new JButton("Afișează Următorul Drum");
        showNextPathButton.addActionListener(e -> showNextPath());
        this.add(showNextPathButton);

        addMouseListener(new MouseAdapter() {
            //evenimentul care se produce la apasarea mousse-ului
            public void mousePressed(MouseEvent e) {
                pointStart = e.getPoint();
                n_start=coleziuniNoduri(e.getX(), e.getY());

                // Resetare stare vizualizare drumuri la interacțiune grafică
                resetPathVisualization();

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
                        Arc arc = new Arc(pointStart, pointEnd, n_start, n_end);
                        listaArce.add(arc);
                        matriceAdiacenta();
                        repaint();
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

    // NOU: Resetare stare vizualizare drumuri
    private void resetPathVisualization() {
        allShortestPaths.clear();
        currentPathIndex = -1;
        spResult = null;
        repaint();
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

    // getter pentru matricea de adiacenta
    public int[][] getAdjacencyMatrix() {
        int n = listaNoduri.size();
        int [][] matrice = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrice[i][j] = 0;
            }
        }

        for(int i=0;i<listaArce.size();i++){
            Arc a=listaArce.get(i);
            int i1=a.getNodStart().getNumber()-1;
            int i2=a.getNodEnd().getNumber()-1;

            matrice[i1][i2]=1;
            if(!grafOrientat){
                matrice[i2][i1] = 1;
            }
        }
        return matrice;
    }


    public void matriceAdiacenta(){
        int n=listaNoduri.size();
        int [][] matrice = getAdjacencyMatrix(); // Foloseste metoda ajutatoare

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



    //afișare info graf condensat în consolă
    private void displayCondensedGraphStructure(GraphResults.CTCResult result) {
        if (result == null) return;

        System.out.println("\n--- REZULTAT: GRAFUL CONDENSAT ---");

        // Afisare CTC + etichete
        for (int i = 0; i < result.sccsList.size(); i++) {
            System.out.println("CTC " + (i + 1) + " (Eticheta: " + result.sccsList.get(i) + ")");
        }

        System.out.println("----------------------------------------------------\n");
    }

    private void runTopologicalSort() {
        int n = listaNoduri.size();
        if (n == 0) return;

        int[][] matrix = getAdjacencyMatrix();
        GraphAlgorithms ga = new GraphAlgorithms(n, matrix);

        if (!grafOrientat) {
            JOptionPane.showMessageDialog(this, "Sortarea Topologică se aplică doar grafurilor orientate.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Resetăm rezultatele vechi
        topologicalResult = null;
        ccResult = null;
        ctcResult = null;
        resetPathVisualization(); // Resetare vizualizare drumuri

        topologicalResult = ga.TopologicalSort();

        if (topologicalResult == null) {
            //graful are circuit
            JOptionPane.showMessageDialog(this, "EROARE: Graful conține circuite. Sortarea topologică nu poate fi efectuată.", "Rezultat Sortare", JOptionPane.ERROR_MESSAGE);
            System.out.println("EROARE: Graful conține circuite.");
        } else {
            //sortarea posibilă
            JOptionPane.showMessageDialog(this, "Graful este aciclic. Sortarea topologică a fost efectuată cu succes.", "Rezultat Sortare", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("\n--- REZULTAT SORTARE TOPOLOGICĂ ---");
            System.out.println("Ordonarea nodurilor: " + topologicalResult);
        }

        repaint();
    }


    // Metodă de inițiere a algoritmului (CC sau CTC)
    private void runAlgorithm() {
        int n = listaNoduri.size();
        if (n == 0) return;

        int[][] matrix = getAdjacencyMatrix();
        GraphAlgorithms ga = new GraphAlgorithms(n, matrix);

        ccResult = null;
        ctcResult = null;
        resetPathVisualization(); // Resetare vizualizare drumuri

        if (!grafOrientat) {
            System.out.println("Ruleaza Algoritmul Componentelor Conexe...");
            ccResult = ga.findConnectedComponents();
        } else {
            System.out.println("Ruleaza Algoritmul Componentelor Tare-Conexe...");
            ctcResult = ga.findStronglyConnectedComponents();
            displayCondensedGraphStructure(ctcResult); //afisare info graf condensat în consolă
            CondensedGraphPanel.display(ctcResult); //afișare grafică graf condensat
        }

        repaint();
    }

    // Afișare rezultate Drum Minim în consolă
    private void displayShortestPathResults(GraphResults.ShortestPathResult result, int source) {
        if (result == null) return;

        System.out.println("\n--- REZULTAT: DRUM MINIM DE LA SURSA S=" + source + " ---");

        for (int i = 1; i <= listaNoduri.size(); i++) {
            String dist = (result.distance[i] == Integer.MAX_VALUE) ? "INF" : String.valueOf(result.distance[i]);
            String pred = (result.predecessor[i] == 0) ? "-" : String.valueOf(result.predecessor[i]);

            System.out.println("Nod " + i + ": d=" + dist + ", P=" + pred);
        }

        System.out.println("----------------------------------------------------\n");
    }

    // Reconstruiește drumul de la start la end folosind array-ul de predecesori
    private List<Integer> reconstructPath(int startNode, int endNode, int[] predecessor) {
        List<Integer> path = new ArrayList<>();
        if (predecessor[endNode] == 0 && endNode != startNode) {
            return path; // Inaccesibil
        }

        int current = endNode;
        while (current != 0) {
            path.add(current);
            if (current == startNode) break;
            current = predecessor[current];
        }

        Collections.reverse(path);
        return path;
    }

    private void runShortestPathDAG() {
        int n = listaNoduri.size();
        if (n == 0) return;

        int[][] matrix = getAdjacencyMatrix();
        GraphAlgorithms ga = new GraphAlgorithms(n, matrix);

        if (!grafOrientat) {
            JOptionPane.showMessageDialog(this, "Algoritmul Drumului Minim pe DAG se aplică doar grafurilor orientate.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Resetăm rezultatele vechi și vizualizarea
        topologicalResult = null;
        ccResult = null;
        ctcResult = null;
        resetPathVisualization();

        // Citirea nodului sursă printr-o fereastră pop-up
        String input = JOptionPane.showInputDialog(this,
                "Introduceți nodul de start (S) [1-" + n + "]:",
                "Nod Sursă",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) {
            // Utilizatorul a anulat sau a lăsat gol
            return;
        }

        try {
            sourceNode = Integer.parseInt(input.trim());
            if (sourceNode < 1 || sourceNode > n) {
                JOptionPane.showMessageDialog(this, "Nod Sursă invalid. Introduceți un nod între 1 și " + n + ".", "Eroare", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduceți un număr valid pentru Nodul Sursă.", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }


        ctcResult = ga.findStronglyConnectedComponents();
        boolean isDAG = true;

        for (List<Integer> ctc : ctcResult.sccsList) {
            if (ctc.size() > 1) {
                isDAG = false;
                break;
            }
        }

        if (!isDAG) {
            JOptionPane.showMessageDialog(this, "EROARE: Graful conține circuite (Verificare CTC). Algoritmul nu poate fi rulat.", "Eroare Graf", JOptionPane.ERROR_MESSAGE);
            System.out.println("EROARE: Graful conține circuite. Componente Tare-Conexe cu mai mult de un nod au fost detectate.");
            return;
        }
        System.out.println("Graful nu conține circuite.");


        // sortare topologică + drum minim
        System.out.println("\n--- 2. Rulare Algoritm Drum Minim pe DAG ---");

        // findShortestPathDAG intern apeleaza și apoi rulează algoritmul drumului minim.
        spResult = ga.findShortestPathDAG(sourceNode);

        if (spResult == null) {
            JOptionPane.showMessageDialog(this, "EROARE: Sortarea topologică nu a putut fi efectuată .", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        displayShortestPathResults(spResult, sourceNode);

        // generare drumuri pentru vizualizare secvențială
        allShortestPaths.clear();
        for (int target = 1; target <= n; target++) {
            if (spResult.distance[target] != Integer.MAX_VALUE) {
                List<Integer> path = reconstructPath(sourceNode, target, spResult.predecessor);

                // Adăugăm doar drumurile cu lungime > 0
                if (path.size() > 1) {
                    allShortestPaths.add(path);
                }
            }
        }

        if (!allShortestPaths.isEmpty()) {
            currentPathIndex = -1; // Setează la -1. Prima apăsare ShowNextPath îl face 0, afișând primul drum.
            JOptionPane.showMessageDialog(this, "Drumurile (v != S) au fost calculate. Apăsați 'Afișează Următorul Drum' pentru vizualizarea secvențială.", "Vizualizare", JOptionPane.INFORMATION_MESSAGE);
        } else {
            currentPathIndex = -1;
            JOptionPane.showMessageDialog(this, "Niciun nod accesibil de la sursa " + sourceNode + " cu drumuri de lungime > 0.", "Vizualizare", JOptionPane.WARNING_MESSAGE);
        }

        repaint();
    }

    // Metodă pentru a afișa drumul următor în secvență
    private void showNextPath() {
        if (allShortestPaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nu există drumuri de afișat. Rulați Algoritmul Drum Minim mai întâi.", "Eroare Vizualizare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentPathIndex++;
        if (currentPathIndex >= allShortestPaths.size()) {
            currentPathIndex = 0; // Buclă, sau se poate opri/reseta
            JOptionPane.showMessageDialog(this, "Vizualizarea a fost reluată de la primul drum.", "Sfârșit Secvență", JOptionPane.INFORMATION_MESSAGE);
        }

        System.out.println("Afișează drumul " + (currentPathIndex + 1) + "/" + allShortestPaths.size() + ": " + allShortestPaths.get(currentPathIndex));
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Desenează toate arcele normal (Roșu), cu excepția celor din drumul curent
        for (Arc a : listaArce)
        {
            // Drum Secvențial
            boolean isPathArc = false;
            if (currentPathIndex != -1) {
                List<Integer> currentPath = allShortestPaths.get(currentPathIndex);
                int start = a.getNodStart().getNumber();
                int end = a.getNodEnd().getNumber();

                for(int i = 0; i < currentPath.size() - 1; i++) {
                    if (currentPath.get(i) == start && currentPath.get(i+1) == end) {
                        isPathArc = true;
                        break;
                    }
                }
            }
            // Dacă arcul NU este pe drumul curent, îl desenăm normal (Roșu)
            if (!isPathArc) {
                a.drawArc(g,grafOrientat);
            }
        }

        if (pointStart != null && pointEnd != null && isDragging)
        {
            g.setColor(Color.RED);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
        }

        // Culori pentru componentele conexe/tare-conexe
        Color[] colors = {Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.GRAY};

        List<List<Integer>> activeComponents = null;
        if (grafOrientat && ctcResult != null && currentPathIndex == -1) { // Ruleaza CTC dar nu si SP
            activeComponents = ctcResult.sccsList;
        } else {
            activeComponents = ccResult;
        }

        //deseneaza lista de noduri
        for(int i=0; i<listaNoduri.size(); i++)
        {
            Node node = listaNoduri.elementAt(i);
            Color nodeColor = Color.RED; // Culoarea implicita
            int nodeId = node.getNumber();

            // Logica de colorare pentru Drum Secvențial
            if (currentPathIndex != -1) {
                List<Integer> currentPath = allShortestPaths.get(currentPathIndex);

                if (nodeId == sourceNode) {
                    nodeColor = Color.BLUE; // Sursa (S) în albastru
                } else if (currentPath.contains(nodeId)) {
                    nodeColor = Color.GREEN; // Nod pe drumul curent în verde
                } else {
                    nodeColor = Color.GRAY; // Nod neimplicat
                }
            }
            // Logica de colorare CC/CTC (Existentă)
            else if (activeComponents != null) {
                for (int j = 0; j < activeComponents.size(); j++) {
                    if (activeComponents.get(j).contains(nodeId)) {
                        nodeColor = colors[j % colors.length];
                        break;
                    }
                }
            }
            node.drawNode(g, node_diam, nodeColor);
        }

        // desenează arcele din drumul curent în VERDE
        if (currentPathIndex != -1) {
            List<Integer> currentPath = allShortestPaths.get(currentPathIndex);
            g.setColor(Color.GREEN);

            for(int i = 0; i < currentPath.size() - 1; i++) {
                Node startNode = null;
                Node endNode = null;

                for (Node n : listaNoduri) {
                    if (n.getNumber() == currentPath.get(i)) startNode = n;
                    if (n.getNumber() == currentPath.get(i+1)) endNode = n;
                }

                if (startNode != null && endNode != null) {
                    // Replicăm desenarea arcului din drawArc, dar cu culoarea Verde
                    double xStart = startNode.getCoordX()+15;
                    double yStart = startNode.getCoordY()+15;
                    double xEnd = endNode.getCoordX()+15;
                    double yEnd = endNode.getCoordY()+15;

                    double unghi = Math.atan2(yEnd - yStart, xEnd - xStart);

                    double dist = Math.sqrt((xEnd - xStart)*(xEnd - xStart) + (yEnd - yStart)*(yEnd - yStart));
                    double xEndCorect = xEnd - (15 * (xEnd - xStart) / dist);
                    double yEndCorect = yEnd - (15 * (yEnd - yStart) / dist);

                    g.drawLine((int)xStart, (int)yStart, (int)xEndCorect, (int)yEndCorect);

                    // Desenarea săgeții
                    if(grafOrientat){
                        int lungimeVarf = 15;
                        int deschidere = 25;

                        double unghi1 = Math.toRadians(deschidere);
                        double unghi2 = -Math.toRadians(deschidere);

                        int x1 = (int)(xEndCorect - lungimeVarf * Math.cos(unghi + unghi1));
                        int y1 = (int)(yEndCorect - lungimeVarf * Math.sin(unghi + unghi1));

                        int x2 = (int)(xEndCorect - lungimeVarf * Math.cos(unghi + unghi2));
                        int y2 = (int)(yEndCorect - lungimeVarf * Math.sin(unghi + unghi2));

                        g.drawLine((int)xEndCorect, (int)yEndCorect, x1, y1);
                        g.drawLine((int)xEndCorect, (int)yEndCorect, x2, y2);
                    }
                }
            }
        }
    }
}