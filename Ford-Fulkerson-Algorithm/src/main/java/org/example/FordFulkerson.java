package org.example;

import java.util.*;

public class FordFulkerson {
    private int numNodes;
    private List<List<EdgeFF>> graph;//lista de adiacenta
    private Map<Arc, Integer> flows; //rez final

    //leaga un arc direct de cel invers în rețeaua reziduală.
    static class EdgeFF {
        int to;
        int capacity;
        int flow;
        EdgeFF reverse; //pt a putea anula fluxul daca se gaseste un drum mai bun

        EdgeFF(int to, int capacity, int flow) {
            this.to = to;
            this.capacity = capacity;
            this.flow = flow;
        }
    }

    public FordFulkerson(int numNodes) {
        this.numNodes = numNodes;
        this.graph = new ArrayList<>();
        this.flows = new HashMap<>();

        for (int i = 0; i < numNodes; i++) {
            graph.add(new ArrayList<>());
        }
    }

    //creeaza graful rezidual
    public void addEdge(int from, int to, int capacity, int initialFlow) {
        EdgeFF forward = new EdgeFF(to, capacity, initialFlow);
        EdgeFF backward = new EdgeFF(from, 0, 0);

        forward.reverse = backward;
        backward.reverse = forward;

        //legare arcuri
        graph.get(from).add(forward);
        graph.get(to).add(backward);
    }

    public int getMaxFlow(int source, int sink) {
        int maxFlow = 0;

        while (true) {
            // BFS pt a gasi un drum nou
            int[] parent = new int[numNodes];//retine drumul gasit
            EdgeFF[] parentEdge = new EdgeFF[numNodes];//retine arcul folosit pt a ajunge la nod
            Arrays.fill(parent, -1);//initial toate nodurile sunt nevizitate

            Queue<Integer> queue = new LinkedList<>(); //pt bfs
            queue.offer(source);
            parent[source] = source;

            while (!queue.isEmpty() && parent[sink] == -1) {
                int u = queue.poll();

                for (EdgeFF edge : graph.get(u)) {
                    //vecinul nu a fost vizitat si exista capacitate rezid mai mare
                    if (parent[edge.to] == -1 && edge.capacity > edge.flow) {
                        parent[edge.to] = u;
                        parentEdge[edge.to] = edge;
                        queue.offer(edge.to);
                    }
                }
            }

            //nu se ajunge la stoc
            if (parent[sink] == -1) {
                break;
            }

            // capacitatea reziduală min
            int pathFlow = Integer.MAX_VALUE;
            //parcurge drumul invers de la stoc  la sursa
            for (int v = sink; v != source; v = parent[v]) {
                EdgeFF edge = parentEdge[v];
                pathFlow = Math.min(pathFlow, edge.capacity - edge.flow);
            }

            // actualizăm fluxurile in tot drumul
            //crește pe arcul folosit și scade pe cel invers.
            for (int v = sink; v != source; v = parent[v]) {
                EdgeFF edge = parentEdge[v];
                edge.flow += pathFlow;
                edge.reverse.flow -= pathFlow;
            }

            maxFlow += pathFlow;
        }

        // fluxurile finale
        for (int u = 0; u < numNodes; u++) {
            for (EdgeFF edge : graph.get(u)) {
                if (edge.capacity > 0 && edge.flow > 0) {
                    flows.put(new Arc(u, edge.to, edge.capacity, 0), edge.flow);
                }
            }
        }

        return maxFlow;
    }

    public Set<Arc> getMinCut(int source) {
        Set<Arc> minCut = new HashSet<>();
        boolean[] reachable = new boolean[numNodes];//

        // BFS pentru a găsi nodurile accesibile din sursa în graful rezidual
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(source);
        reachable[source] = true;

        //parcurgere graf rezidual
        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (EdgeFF edge : graph.get(u)) {
                if (!reachable[edge.to] && edge.capacity > edge.flow) {
                    reachable[edge.to] = true;
                    queue.offer(edge.to);
                }
            }
        }

        // taietura minimă constă din arcele care merg din partea accesibilă
        // în partea neaccesibilă
        for (int u = 0; u < numNodes; u++) {
            if (reachable[u]) {
                for (EdgeFF edge : graph.get(u)) {
                    if (!reachable[edge.to] && edge.capacity > 0) {
                        minCut.add(new Arc(u, edge.to, edge.capacity, 0));
                    }
                }
            }
        }

        return minCut;
    }

    public int getMinCutCapacity(Set<Arc> minCut) {
        int capacity = 0;
        for (Arc arc : minCut) {
            capacity += arc.capacity;
        }
        return capacity;
    }

    public Map<Arc, Integer> getFlows() {
        return new HashMap<>(flows);
    }
}