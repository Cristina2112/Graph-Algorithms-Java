import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

public class MSTPrim {

    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    private Vector<Edge>[] adjacencyList;  //stocheaza munchhile adiacente fiecarui nod

    private Map<String, Arc> inverseArcMap; // pt cautare eficienta O(1) arc simetric

    private double[] v;      // cost minim
    private Arc[] e;         // stocheaza arcul care face cost min
    private boolean[] inMST; // verificare daca nodul face parte dintr-un MST

    private static final double INFINITY = Double.MAX_VALUE;

    private static class Edge {
        int neighbor;
        Arc arc;

        Edge(int neighbor, Arc arc) {
            this.neighbor = neighbor;
            this.arc = arc;
        }
    }

    public MSTPrim(Vector<Node> listaNoduri, Vector<Arc> listaArce) {
        this.listaNoduri = listaNoduri;
        this.listaArce = listaArce;
        buildAdjacencyList();
        buildInverseArcMap();
    }


    private void buildAdjacencyList() {
        int n = listaNoduri.size();
        adjacencyList = new Vector[n];

        for (int i = 0; i < n; i++) {
            adjacencyList[i] = new Vector<>();
        }

        for (Arc arc : listaArce) {
            int start = arc.getNodStart().getNumber() - 1;
            int end = arc.getNodEnd().getNumber() - 1;
            adjacencyList[start].add(new Edge(end, arc));
        }
    }


    private void buildInverseArcMap() {
        inverseArcMap = new HashMap<>();
        for (Arc arc : listaArce) {
            int u = arc.getNodStart().getNumber();
            int v = arc.getNodEnd().getNumber();
            String key = u + "-" + v;
            inverseArcMap.put(key, arc);
        }
    }


    public double calculateMST() {
        int n = listaNoduri.size();
        if (n == 0) return 0.0;

        // resetare
        for (Arc a : listaArce) {
            a.setIsMSTEdge(false);
        }

        // inițializare
        v = new double[n];
        e = new Arc[n];
        inMST = new boolean[n];

        for (int i = 0; i < n; i++) {
            v[i] = INFINITY;
            e[i] = null;
            inMST[i] = false;
        }

        double minCostTotal = 0.0;
        v[0] = 0;

        for (int k = 0; k < n; k++) {

            //gasire nod min care nu apartine MST
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!inMST[i] && (u == -1 || v[i] < v[u])) {
                    u = i;
                }
            }

            if (u == -1 || v[u] == INFINITY) {
                break;
            }

            //adaugare nod la MST și marcare arce
            inMST[u] = true;
            if (e[u] != null) {

                Arc selectedArc = e[u];

                // marcheaza arc  u -> v
                selectedArc.setIsMSTEdge(true);
                minCostTotal += selectedArc.getCost();

                //marchează arc simetric v -> u în O(1)
                int startNum = selectedArc.getNodStart().getNumber();
                int endNum = selectedArc.getNodEnd().getNumber();

                // cheia pentru arcul invers este "end-start" (ex: "2-1")
                String inverseKey = endNum + "-" + startNum;

                Arc inverseArc = inverseArcMap.get(inverseKey);

                // verificare dacă arcul simetric există și nu este același obiect
                if (inverseArc != null && inverseArc != selectedArc) {
                    inverseArc.setIsMSTEdge(true);
                }
            }

            // actualizare cost vecini
            for (Edge edge : adjacencyList[u]) {
                int j = edge.neighbor;
                int costArc = edge.arc.getCost();

                if (!inMST[j] && costArc < v[j]) {
                    v[j] = costArc;
                    e[j] = edge.arc;
                }
            }
        }

        return minCostTotal;
    }
}