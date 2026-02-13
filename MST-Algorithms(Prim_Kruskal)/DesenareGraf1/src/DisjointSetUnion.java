public class DisjointSetUnion {
    private int[] parent; // stochează părintele fiecărui nod
    private int[] rank;
    private int n;

    public DisjointSetUnion(int n) {
        this.n = n;
        parent = new int[n];
        rank = new int[n];
        //fiecare nod este părintele propriului set
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        // gasește radacina
        int root = i;
        while (parent[root] != root) {
            root = parent[root];
        }

        // mapează fiecare nod direct la rădăcină
        while (parent[i] != root) {
            int next = parent[i];
            parent[i] = root;
            i = next;
        }

        return root;
    }

    //union by rank
    public boolean union(int i, int j) {
        int root_i = find(i);
        int root_j = find(j);

        if (root_i != root_j) {
            // conectează nodurile mai mici la cele mai mari
            if (rank[root_i] < rank[root_j]) {
                parent[root_i] = root_j;
            } else if (rank[root_i] > rank[root_j]) {
                parent[root_j] = root_i;
            } else {
                parent[root_j] = root_i;
                rank[root_i]++;
            }
            return true; //nu a format ciclu
        }
        return false;
    }
}