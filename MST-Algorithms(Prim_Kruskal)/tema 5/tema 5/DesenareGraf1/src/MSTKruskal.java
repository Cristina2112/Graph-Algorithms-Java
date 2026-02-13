import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MSTKruskal {

    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    private Map<String, Arc> arcMap; // cautare O(1) muchii inverse

    public MSTKruskal(Vector<Node> listaNoduri, Vector<Arc> listaArce) {
        this.listaNoduri = listaNoduri;
        this.listaArce = listaArce;
        buildArcMap();
    }

    private void buildArcMap() {
        arcMap = new HashMap<>();
        for (Arc arc : listaArce) {
            int u = arc.getNodStart().getNumber();
            int v = arc.getNodEnd().getNumber();

            String inverseKey = v + "-" + u;
            arcMap.put(inverseKey, arc);
        }
    }


    public double calculateMST() {
        int n = listaNoduri.size();

        if (n == 0) return 0.0;

        double minCostTotal = 0.0;

        // resetare stare MST pentru toate muchiile
        for (Arc a : listaArce) {
            a.setIsMSTEdge(false);
        }

        DisjointSetUnion dsu = new DisjointSetUnion(n);

        // lista de muchii UNICE (inttr-un singur sens)
        Vector<Arc> muchiiUnice = new Vector<>();

        for (Arc a : listaArce) {
            int u = a.getNodStart().getNumber();
            int v = a.getNodEnd().getNumber();

            // evita duplicarea
            if (u < v) {
                muchiiUnice.add(a);
            }
        }

        // sortare după cost cresc
        Collections.sort(muchiiUnice, new Comparator<Arc>() {
            @Override
            public int compare(Arc a1, Arc a2) {
                return Integer.compare(a1.getCost(), a2.getCost());
            }
        });

        int muchiiAdaugate = 0;

        for (Arc a : muchiiUnice) {

            // MST complet
            if (muchiiAdaugate == n - 1) {
                break;
            }

            //obt indici noduri
            int u = a.getNodStart().getNumber() - 1;
            int v = a.getNodEnd().getNumber() - 1;

            // verifica dacă u și v sunt în același set O(1)
            if (dsu.find(u) != dsu.find(v)) {

                // muchia nu formează ciclu,adaugare in MST
                dsu.union(u, v);

                a.setIsMSTEdge(true); // u -> v
                minCostTotal += a.getCost();
                muchiiAdaugate++;

                markInverseArc(a); // v -> u
            }
            // muchia formează ciclu, este ignorata
        }

        return minCostTotal;
    }


    private void markInverseArc(Arc selectedArc) {
        int u = selectedArc.getNodStart().getNumber();
        int v = selectedArc.getNodEnd().getNumber();
        String inverseKey = v + "-" + u;

        Arc inverseArc = arcMap.get(inverseKey);
        if (inverseArc != null && inverseArc != selectedArc) {
            inverseArc.setIsMSTEdge(true);
        }
    }
}