import java.util.*;
public class GraphResults {

    // Structura ce stocheaza rezultatul procedurii PTDF
    public static class PTDFResult {
        public int[] discoveryTime; // t1: Timpul de descoperire
        public int[] finishTime;    // t2: Timpul de finalizare
        public Map<Integer, Integer> predecessor; // pred: Predecesorul
        public int timeCounter; // time: Contorul de timp
        public LinkedList<Integer> topologicalOrder; // Ordinea Topologica tema 4

        public PTDFResult(int numNodes) {
            discoveryTime = new int[numNodes + 1];
            finishTime = new int[numNodes + 1];
            predecessor = new HashMap<>();
            timeCounter = 1;
            topologicalOrder = new LinkedList<>();
        }
    }

    public static class ShortestPathResult {
        public int[] distance; // distanța minimă de la sursă
        public int[] predecessor;    // predecesorul pe drumul minim

        public ShortestPathResult(int numNodes) {
            distance = new int[numNodes + 1];
            predecessor = new int[numNodes + 1];
        }
    }

    public static class CTCResult {
        public List<List<Integer>> sccsList; // sccs: Lista de Componente Tare-Conexe
        public Set<Pair<Integer, Integer>> condensedEdges; // Arcele Grafului Condensat


        public static class Pair<A, B> {
            public final A source;
            public final B destination;

            public Pair(A source, B destination) {
                this.source = source;
                this.destination = destination;
            }
            @Override
            //verifică egalitatea între două perechi
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;//verifică dacă obiectul este nul sau de alt tip
                Pair<?, ?> pair = (Pair<?, ?>) o;  //casting obiectului la tipul Pair
                return source.equals(pair.source) && destination.equals(pair.destination);
            }
            @Override
            //asigura un hash unic pentru fiecare pereche ce alcatuiește un arc
            public int hashCode() {
                return Objects.hash(source, destination);
            }
        }

        public CTCResult(List<List<Integer>> sccsList, Set<Pair<Integer, Integer>> condensedEdges) {
            this.sccsList = sccsList;
            this.condensedEdges = condensedEdges;
        }
    }
}