import java.util.*;

public class GraphAlgorithms {
    private int numNodes;
    private int[][] adjMatrix;

    public GraphAlgorithms(int numNodes, int[][] adjMatrix) {
        this.numNodes = numNodes;
        this.adjMatrix = adjMatrix;
    }

    // inversare matrice adiacenta => graf ^(-1)
    public int[][] inverseGraph(int[][] matrix) {
        int count = matrix.length;
        int[][] inverseMatrix = new int[count][count];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                inverseMatrix[j][i] = matrix[i][j];
            }
        }
        return inverseMatrix;
    }

    // PTDF
    public GraphResults.PTDFResult PTDF(int[][] matrix, Set<Integer> startNodes) {
        int count = matrix.length;
        GraphResults.PTDFResult result = new GraphResults.PTDFResult(count);

        Set<Integer> allNodes = new HashSet<>();
        for (int i = 1; i <= count; i++) allNodes.add(i);

        Set<Integer> unvisitedNodes = new HashSet<>(allNodes); //noduri nevizitate
        Set<Integer> fullyExploredNodes = new HashSet<>(); //noduri complet explorate

        Stack<Integer> stack = new Stack<>();

        for (Integer s : startNodes) {
            if (fullyExploredNodes.contains(s) || !unvisitedNodes.contains(s)) continue;

            Set<Integer> visitingNodes = new HashSet<>();
            unvisitedNodes.remove(s);
            visitingNodes.add(s);
            result.discoveryTime[s] = result.timeCounter++;
            stack.push(s);

            while (!visitingNodes.isEmpty() && !stack.isEmpty()) {

                // Se selectează cel mai nou nod x introdus
                Integer currentNode = stack.peek();

                boolean foundUnvisitedNeighbor = false;
                for (int neighbor = 1; neighbor <= count; neighbor++) {
                    // IF există arc (x, y) ∈ A și y ∈ U
                    if (matrix[currentNode - 1][neighbor - 1] == 1 && unvisitedNodes.contains(neighbor)) {
                        unvisitedNodes.remove(neighbor);
                        visitingNodes.add(neighbor);
                        result.predecessor.put(neighbor, currentNode);
                        result.discoveryTime[neighbor] = result.timeCounter++;

                        stack.push(neighbor);
                        foundUnvisitedNeighbor = true;
                        break;
                    }
                }

                if (!foundUnvisitedNeighbor) {
                    visitingNodes.remove(currentNode);
                    fullyExploredNodes.add(currentNode);
                    result.finishTime[currentNode] = result.timeCounter++;
                    stack.pop();
                }
            }
        }

        return result;
    }

    // verifica dacă graful este aciclic
    public boolean isAcyclic() {
        if (adjMatrix.length == 0) return true;

        Set<Integer> allNodes = new LinkedHashSet<>();
        for (int i = 1; i <= numNodes; i++) allNodes.add(i);

        GraphResults.PTDFResult result = PTDF(adjMatrix, allNodes);

        for (int u = 1; u <= numNodes; u++) {
            for (int v = 1; v <= numNodes; v++) {
                if (adjMatrix[u - 1][v - 1] == 1) { // Există arc (u, v)

                    if (result.discoveryTime[u] > 0 && result.discoveryTime[v] > 0 &&
                            result.finishTime[u] < result.finishTime[v] && result.discoveryTime[v] < result.discoveryTime[u]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Sortare Topologică
    public List<Integer> TopologicalSort() {
        if (!isAcyclic()) {
            return null;
        }

        Set<Integer> allNodes = new LinkedHashSet<>();
        for (int i = 1; i <= numNodes; i++) allNodes.add(i);

        // Rulăm PTDF-ul original (care calculează t1 și t2)
        GraphResults.PTDFResult result = PTDF(adjMatrix, allNodes);

        // Extragem nodurile care au fost vizitate (t1>0)
        List<Integer> sortedNodes = new ArrayList<>();
        for (int i = 1; i <= numNodes; i++) {
            if (result.discoveryTime[i] > 0) {
                sortedNodes.add(i);
            }
        }

        // Sortăm nodurile descrescător după finishTime (t2)
        sortedNodes.sort((a, b) -> Integer.compare(result.finishTime[b], result.finishTime[a]));

        return sortedNodes;
    }


    //CC
    public List<List<Integer>> findConnectedComponents() {
        Set<Integer> allNodes = new HashSet<>();
        for (int i = 1; i <= numNodes; i++) allNodes.add(i);

        Set<Integer> visitedGlobally = new HashSet<>();  // Noduri deja incluse în componentele conexe
        List<List<Integer>> components = new ArrayList<>();  // Lista de componente conexe

        for (Integer startNode : allNodes) {
            if (!visitedGlobally.contains(startNode)) {
                Set<Integer> currentStartNode = new HashSet<>();
                currentStartNode.add(startNode);

                GraphResults.PTDFResult result = PTDF(adjMatrix, currentStartNode);

                List<Integer> component = new ArrayList<>();
                for(int i = 1; i <= numNodes; i++) {
                    if (result.discoveryTime[i] > 0) {
                        component.add(i);
                        visitedGlobally.add(i);
                    }
                }

                components.add(component);
                System.out.println("Componenta Conexă: " + component);
            }
        }
        return components;
    }

    //CTC
    public GraphResults.CTCResult findStronglyConnectedComponents() {
        Set<Integer> allNodes = new LinkedHashSet<>();
        for (int i = 1; i <= numNodes; i++) allNodes.add(i);

        // PTDF(G)
        GraphResults.PTDFResult ptdfG = PTDF(adjMatrix, allNodes);

        //Inversarea G
        int[][] inverseMatrix = inverseGraph(adjMatrix);

        // Sortarea nodurilor după finishTime descrescător din primul PTDF
        List<Integer> sortedNodesByFinishTime = new ArrayList<>();
        for (int i = 1; i <= numNodes; i++) sortedNodesByFinishTime.add(i);
        sortedNodesByFinishTime.sort((a, b) -> Integer.compare(ptdfG.finishTime[b], ptdfG.finishTime[a]));

        Set<Integer> startNodesGInv = new LinkedHashSet<>(sortedNodesByFinishTime);

        // PTDF(G ^(-1))
        GraphResults.PTDFResult ptdfGInv = PTDF(inverseMatrix, startNodesGInv);

        // Extragerea CTC-urilor
        List<List<Integer>> sccsList = new ArrayList<>(); //lista ctc finala
        Set<Integer> visitedInSCC = new HashSet<>(); //noduri deja incluse in ctc

        for (Integer startNode : sortedNodesByFinishTime) {
            if (!visitedInSCC.contains(startNode)) {
                List<Integer> scc = new ArrayList<>();  //ctc curenta
                Stack<Integer> stack = new Stack<>();

                stack.push(startNode);
                visitedInSCC.add(startNode);
                scc.add(startNode);

                while (!stack.isEmpty()) {
                    Integer current = stack.pop();

                    for (int neighbor = 1; neighbor <= numNodes; neighbor++) {
                        // Verificam daca 'current' este predecesorul lui 'neighbor' in arborele DFS din G^ (-1)
                        if (ptdfGInv.predecessor.containsKey(neighbor) && ptdfGInv.predecessor.get(neighbor).equals(current)) {
                            if (!visitedInSCC.contains(neighbor)) {
                                visitedInSCC.add(neighbor);
                                scc.add(neighbor);
                                stack.push(neighbor);
                            }
                        }
                    }
                }

                System.out.println("Componenta Tare-Conexă: " + scc);
                sccsList.add(scc);
            }
        }

        // Construirea grafului condensat
        Map<Integer, Integer> nodeToSccIndex = new HashMap<>();//
        for (int i = 0; i < sccsList.size(); i++) {
            for (Integer node : sccsList.get(i)) {
                nodeToSccIndex.put(node, i);
            }
        }

        Set<GraphResults.CTCResult.Pair<Integer, Integer>> condensedEdges = new HashSet<>();

        // Parcurgem toate arcele din graful original G
        for (int u = 1; u <= numNodes; u++) {
            for (int v = 1; v <= numNodes; v++) {
                if (adjMatrix[u - 1][v - 1] == 1) { // Există arc (u, v) în G

                    if (!nodeToSccIndex.containsKey(u) || !nodeToSccIndex.containsKey(v)) continue;

                    int scc_u_index = nodeToSccIndex.get(u);
                    int scc_v_index = nodeToSccIndex.get(v);

                    // Dacă nodurile u și v fac parte din CTC-uri diferite, adăugăm arcul
                    if (scc_u_index != scc_v_index) {
                        condensedEdges.add(new GraphResults.CTCResult.Pair<>(scc_u_index, scc_v_index));
                    }
                }
            }
        }

        return new GraphResults.CTCResult(sccsList, condensedEdges);
    }


    public GraphResults.ShortestPathResult findShortestPathDAG(int sourceNode) {
        // Verificare ciclu și Sortare Topologică
        List<Integer> topologicalOrder = TopologicalSort();

        if (topologicalOrder == null) {
            // Graful nu este aciclic
            return null;
        }

        GraphResults.ShortestPathResult result = new GraphResults.ShortestPathResult(numNodes);

        // Inițializare (d(y)=inf, p(y)=0)
        for (int y = 1; y <= numNodes; y++) {
            result.distance[y] = Integer.MAX_VALUE;
            result.predecessor[y] = 0; // 0 = INVALID
        }

        // d(S) = 0
        if (sourceNode >= 1 && sourceNode <= numNodes) {
            result.distance[sourceNode] = 0;
        } else {
            return null;
        }

        // Parcurge nodurile în ordinea sortării topologice
        System.out.println("Sortare Topologică (T): " + topologicalOrder);

        for (int x : topologicalOrder) {
            // Se asigură că nodul este accesibil (d(x) nu este infinit)
            if (result.distance[x] == Integer.MAX_VALUE) {
                continue;
            }

            for (int y = 1; y <= numNodes; y++) {
                if (adjMatrix[x - 1][y - 1] == 1) { // arc (x, y)
                    // d(x) + 1 < d(y)
                    int newDist = result.distance[x] + 1;

                    if (newDist < result.distance[y]) {
                        result.distance[y] = newDist;
                        result.predecessor[y] = x; // p(y) = x
                    }
                }
            }
        }

        return result;
    }
}