import java.util.*;



    static void separator(String title) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
    }


    // TASK 3 – UNWEIGHTED GRAPH  (adjacency list)


    static class Graph {
        private final int V;
        private final LinkedList<Integer>[] adj;

        @SuppressWarnings("unchecked")
        Graph(int V) {
            this.V = V;
            adj = new LinkedList[V];
            for (int i = 0; i < V; i++) adj[i] = new LinkedList<>();
        }

        int V() { return V; }
        void addNeighbour(int v, int w) { adj[v].add(w); }
        Iterable<Integer> adj(int v)    { return adj[v]; }
    }

    // DFS

    static boolean[]  dfsMarked;
    static int[]      dfsEdgeTo;
    static List<Integer> dfsOrder;

    static void dfs(Graph G, int v) {
        dfsMarked[v] = true;
        dfsOrder.add(v);
        for (int w : G.adj(v)) {
            if (!dfsMarked[w]) {
                dfsEdgeTo[w] = v;
                dfs(G, w);
            }
        }
    }

    static void runDFS(Graph G, int source, char[] labels) {
        separator("TASK 3a – DEPTH-FIRST SEARCH  (source = " + labels[source] + ")");

        dfsMarked = new boolean[G.V()];
        dfsEdgeTo = new int[G.V()];
        dfsOrder  = new ArrayList<>();
        Arrays.fill(dfsEdgeTo, -1);

        dfs(G, source);

        System.out.println();
        System.out.print("DFS visit order: ");
        for (int v : dfsOrder) System.out.print(labels[v] + " ");
        System.out.println();

        System.out.println();
        System.out.println("Expected (Task 1): A C B E G F D");
        System.out.print  ("Computed         : ");
        for (int v : dfsOrder) System.out.print(labels[v] + " ");
        System.out.println();

        System.out.println();
        System.out.println("DFS tree paths from " + labels[source] + ":");
        for (int v = 0; v < G.V(); v++) {
            if (v == source) continue;
            System.out.print("  " + labels[source] + " to " + labels[v] + ": ");
            Deque<Integer> path = new ArrayDeque<>();
            for (int x = v; x != source; x = dfsEdgeTo[x]) path.push(x);
            path.push(source);
            for (int x : path) System.out.print(labels[x] + " ");
            System.out.println();
        }
    }

    // BFS

    static void runBFS(Graph G, int source, char[] labels) {
        separator("TASK 3b – BREADTH-FIRST SEARCH  (source = " + labels[source] + ")");

        boolean[] marked = new boolean[G.V()];
        int[]     edgeTo = new int[G.V()];
        int[]     distTo = new int[G.V()];
        Arrays.fill(edgeTo, -1);
        Arrays.fill(distTo, Integer.MAX_VALUE);

        List<Integer> bfsOrder = new ArrayList<>();
        Queue<Integer> queue   = new ArrayDeque<>();

        marked[source] = true;
        distTo[source] = 0;
        queue.add(source);
        bfsOrder.add(source);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    queue.add(w);
                    bfsOrder.add(w);
                }
            }
        }

        System.out.println();
        System.out.print("BFS visit order: ");
        for (int v : bfsOrder) System.out.print(labels[v] + " ");
        System.out.println();

        System.out.println();
        System.out.println("Expected (Task 2): A C B D E G F");
        System.out.print  ("Computed         : ");
        for (int v : bfsOrder) System.out.print(labels[v] + " ");
        System.out.println();

        System.out.println();
        System.out.println("BFS shortest paths from " + labels[source] + " (with distances):");
        for (int v = 0; v < G.V(); v++) {
            if (v == source) continue;
            System.out.printf("  %s to %-2s (dist=%d): ",
                    labels[source], labels[v], distTo[v]);
            Deque<Integer> path = new ArrayDeque<>();
            for (int x = v; x != source; x = edgeTo[x]) path.push(x);
            path.push(source);
            for (int x : path) System.out.print(labels[x] + " ");
            System.out.println();
        }
    }


    // TASK 5 – WEIGHTED GRAPH


    static class WeightedEdge {
        final int to, weight;
        WeightedEdge(int to, int weight) { this.to = to; this.weight = weight; }
    }

    static class WeightedGraph {
        private final int V;
        private final LinkedList<WeightedEdge>[] adj;

        @SuppressWarnings("unchecked")
        WeightedGraph(int V) {
            this.V = V;
            adj = new LinkedList[V];
            for (int i = 0; i < V; i++) adj[i] = new LinkedList<>();
        }

        int V() { return V; }
        void addEdge(int u, int v, int w) {
            adj[u].add(new WeightedEdge(v, w));
            adj[v].add(new WeightedEdge(u, w));
        }
        Iterable<WeightedEdge> adj(int v) { return adj[v]; }
    }

    static void runDijkstra(WeightedGraph G, int source, int target, String[] labels) {
        separator("TASK 5 – DIJKSTRA'S SHORTEST PATH");
        System.out.println("Source: " + labels[source] + "   Target: " + labels[target]);
        System.out.println();

        int[]     distTo  = new int[G.V()];
        int[]     edgeTo  = new int[G.V()];
        boolean[] settled = new boolean[G.V()];

        Arrays.fill(distTo, Integer.MAX_VALUE);
        Arrays.fill(edgeTo, -1);
        distTo[source] = 0;

        // min-heap: int[]{distance, vertex}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, source});

        System.out.println("Dijkstra trace:");
        System.out.println("-".repeat(55));

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0], u = top[1];
            if (settled[u]) continue;
            settled[u] = true;
            System.out.printf("  Extract %-12s dist=%d%n", labels[u], d);
            for (WeightedEdge e : G.adj(u)) {
                if (settled[e.to]) continue;
                int newDist = distTo[u] + e.weight;
                if (newDist < distTo[e.to]) {
                    distTo[e.to] = newDist;
                    edgeTo[e.to] = u;
                    pq.offer(new int[]{newDist, e.to});
                    System.out.printf("    Relax %s→%s : dist[%s]=%d%n",
                            labels[u], labels[e.to], labels[e.to], newDist);
                }
            }
        }

        //Print distance table
        System.out.println();
        System.out.println("Shortest distances from " + labels[source] + ":");
        for (int v = 0; v < G.V(); v++) {
            System.out.printf("  %-12s : %d miles%n", labels[v], distTo[v]);
        }

        //Reconstruct shortest path
        Deque<Integer> pathDeque = new ArrayDeque<>();
        for (int x = target; edgeTo[x] != -1; x = edgeTo[x]) pathDeque.push(x);
        pathDeque.push(source);
        int[] path = pathDeque.stream().mapToInt(i -> i).toArray();

        System.out.println();
        System.out.println("Shortest path: " + labels[source] + " → " + labels[target]);
        System.out.print  ("  Route : ");
        for (int i = 0; i < path.length; i++) {
            if (i > 0) System.out.print(" → ");
            System.out.print(labels[path[i]]);
        }
        System.out.println();

        System.out.println();
        System.out.println("  Segment breakdown:");
        for (int i = 0; i < path.length - 1; i++) {
            int segDist = distTo[path[i + 1]] - distTo[path[i]];
            System.out.printf("    %-12s → %-12s : %d miles%n",
                    labels[path[i]], labels[path[i + 1]], segDist);
        }
        System.out.println("    " + "-".repeat(38));
        System.out.printf("    %-28s : %d miles%n", "Total", distTo[target]);

        System.out.println();
        System.out.println("Comparison with Task 4 (manual trace):");
        System.out.println("  Expected : Edinburgh → Stirling → Perth → Dundee = 93 miles");
        System.out.printf ("  Computed : %d miles%n", distTo[target]);
        System.out.println("  Match    : " + (distTo[target] == 93 ? "YES ✓" : "NO ✗"));
    }


    // MAIN


    public static void main(String[] args) {

        // TASK 3 SETUP
        // Vertex labels: A=0, B=1, C=2, D=3, E=4, F=5, G=6
        char[] vertexLabels = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
        final int A=0, B=1, C=2, D=3, E=4, F=5, G=6;

        Graph unweighted = new Graph(7);
        unweighted.addNeighbour(A, C); unweighted.addNeighbour(A, B); unweighted.addNeighbour(A, D);
        unweighted.addNeighbour(B, A); unweighted.addNeighbour(B, C); unweighted.addNeighbour(B, E); unweighted.addNeighbour(B, G);
        unweighted.addNeighbour(C, A); unweighted.addNeighbour(C, B); unweighted.addNeighbour(C, D);
        unweighted.addNeighbour(D, C); unweighted.addNeighbour(D, A);
        unweighted.addNeighbour(E, G); unweighted.addNeighbour(E, F); unweighted.addNeighbour(E, B);
        unweighted.addNeighbour(F, G); unweighted.addNeighbour(F, E);
        unweighted.addNeighbour(G, F); unweighted.addNeighbour(G, B);

        separator("TASK 3 – GRAPH ADJACENCY LISTS");
        System.out.println();
        for (int v = 0; v < unweighted.V(); v++) {
            StringBuilder sb = new StringBuilder(vertexLabels[v] + ": ");
            for (int w : unweighted.adj(v)) sb.append(vertexLabels[w]).append(" ");
            System.out.println(sb.toString().trim());
        }

        runDFS(unweighted, A, vertexLabels);
        runBFS(unweighted, A, vertexLabels);

        // TASK 5 SETUP
        // City labels: 0=Edinburgh, 1=Stirling, 2=Perth, 3=Dundee, 4=Glasgow, 5=Falkirk
        String[] cityLabels = {"Edinburgh", "Stirling", "Perth", "Dundee", "Glasgow", "Falkirk"};
        final int EDINBURGH=0, STIRLING=1, PERTH=2, DUNDEE=3, GLASGOW=4, FALKIRK=5;

        WeightedGraph scottish = new WeightedGraph(6);
        scottish.addEdge(EDINBURGH, STIRLING, 36);
        scottish.addEdge(EDINBURGH, FALKIRK,  24);
        scottish.addEdge(EDINBURGH, GLASGOW,  46);
        scottish.addEdge(STIRLING,  PERTH,    35);
        scottish.addEdge(STIRLING,  FALKIRK,  14);
        scottish.addEdge(STIRLING,  GLASGOW,  28);
        scottish.addEdge(PERTH,     DUNDEE,   22);
        scottish.addEdge(FALKIRK,   GLASGOW,  23);

        runDijkstra(scottish, EDINBURGH, DUNDEE, cityLabels);

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("All tasks complete.");
        System.out.println("=".repeat(60));
    }
