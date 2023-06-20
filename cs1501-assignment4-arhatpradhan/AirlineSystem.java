import java.util.*;
import java.io.*;

public class AirlineSystem implements AirlineInterface {

    private String[] cityNames;
    private Digraph G;
    private static final int INFINITY = Integer.MAX_VALUE;
    // for tripWithin
    private ArrayList<List<String>> tripList = new ArrayList<List<String>>();

    /**
     * reads the city names and the routes from a file
     * 
     * @param fileName the String file name
     * @return true if routes loaded successfully and false otherwise
     */
    public boolean loadRoutes(String fileName) {
        try {
            // gets the file name
            Scanner fileScan = new Scanner(new FileInputStream(fileName));
            // # of verticies coming in
            int v = Integer.parseInt(fileScan.nextLine());
            // create a new graph with # of verticies
            G = new Digraph(v);

            // read all the city names
            cityNames = new String[v];
            for (int i = 0; i < v; i++) {
                cityNames[i] = fileScan.nextLine();
            }

            while (fileScan.hasNext()) {
                // read start vert
                int from = fileScan.nextInt();
                // end vert
                int to = fileScan.nextInt();
                // weighted edge 1
                int miles = fileScan.nextInt();
                // weighted edge 2
                double cost = fileScan.nextDouble();
                // create a new edge
                G.addEdge(new WeightedDirectedEdge(from - 1, to - 1, miles, cost));
                if (fileScan.hasNextLine()) {
                    fileScan.nextLine();
                } else
                    break;
            }
            fileScan.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * returns the set of city names in the Airline system
     * 
     * @return a (possibly empty) Set<String> of city names
     */
    public Set<String> retrieveCityNames() {
        Set<String> cNames = new LinkedHashSet<String>();
        for (String city : cityNames) {
            cNames.add(city);
        }
        return cNames;

    }

    /**
     * returns the set of direct routes out of a given city
     * 
     * @param city the String city name
     * @return a (possibly empty) Set<Route> of Route objects representing the
     *         direct routes out of city
     * @throws CityNotFoundException if the city is not found in the Airline
     *                               system
     */
    public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException {

        Set<Route> directRoute = new LinkedHashSet<Route>();
        // for each city get their direct edges and return //for each verticies create a
        // new Route object and store it into the Set
        // create new route for each edge it has
        for (WeightedDirectedEdge e : G.adj[returnVert(city)]) {
            Route route = new Route(city, cityNames[e.to()], e.miles(), e.cost());
            directRoute.add(route);

        }

        return directRoute;
    }

    /**
     * finds fewest-stops path(s) between two cities
     * 
     * @param source      the String source city name
     * @param destination the String destination city name
     * @return a (possibly empty) Set<ArrayList<String>> of fewest-stops paths.
     *         Each path is an ArrayList<String> of city names that includes the
     *         source
     *         and destination city names.
     * @throws CityNotFoundException if any of the two cities are not found in the
     *                               Airline system
     */
    // use BFS
    public Set<ArrayList<String>> fewestStopsItinerary(String source, String destination) throws CityNotFoundException {

        Set<ArrayList<String>> hops = new LinkedHashSet<ArrayList<String>>();
        ArrayList<String> stops = new ArrayList<String>();
        // use bfs algo
        int start = returnVert(source);
        int end = returnVert(destination);
        // if the destination isn't marked return empty set of arrayList
        G.bfs(start);
        if (!G.marked[end]) {
            return hops;
        } else {
            Stack<Integer> shortestPath = new Stack<Integer>();
            int dest = end;

            while (dest != start) {
                shortestPath.add(dest);
                dest = G.edgeTo[dest];
            }
            stops.add(source);
            while (!shortestPath.isEmpty()) {
                stops.add(cityNames[shortestPath.pop()]);
            }
            hops.add(stops);
        }

        // return the ArrayList of string that has the shortest paths for each city
        return hops;
    }

    private int returnVert(String city) {

        for (int i = 0; i < cityNames.length; i++) {
            if (cityNames[i].equals(city))
                return i;
        }
        return 0;
    }

    /**
     * finds shortest distance path(s) between two cities
     * 
     * @param source      the String source city name
     * @param destination the String destination city name
     * @return a (possibly empty) Set<ArrayList<Route>> of shortest-distance
     *         paths. Each path is an ArrayList<Route> of Route objects that
     *         includes a
     *         Route out of the source and a Route into the destination.
     * @throws CityNotFoundException if any of the two cities are not found in the
     *                               Airline system
     */
    public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String destination)
            throws CityNotFoundException {
        // uses dijkstra's
        Set<ArrayList<Route>> shortestDistance = new LinkedHashSet<ArrayList<Route>>();
        ArrayList<Route> shortDis = new ArrayList<Route>();

        int start = returnVert(source);
        int end = returnVert(destination);

        if (G == null) {
            return shortestDistance;
        }

        G.dijkstras(start, end, true);
        if (!G.marked[end]) {
            return shortestDistance;
        }

        Stack<Integer> path = new Stack<>();
        for (int x = end; x != start; x = G.edgeTo[x]) {
            path.push(x);
        }

        int prevVertex = start;
        while (!path.empty()) {
            int v = path.pop();

            int difference = G.distTo[v] - G.distTo[prevVertex];

            // need to change the to the string of the city
            double price = 0;
            for(WeightedDirectedEdge e: G.adj[returnVert(cityNames[v])]){
                if(cityNames[e.to()] == cityNames[prevVertex]){
                    price = e.cost();
                    break;
                } 
            }
            Route distance = new Route(cityNames[prevVertex], cityNames[v], difference, price);

            shortDis.add(distance);

            prevVertex = v;
        }
        shortestDistance.add(shortDis);
        return shortestDistance;
    }

    /**
     * finds cheapest path(s) between two cities
     * 
     * @param source      the String source city name
     * @param destination the String destination city name
     * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
     *         paths. Each path is an ArrayList<Route> of Route objects that
     *         includes a
     *         Route out of the source and a Route into the destination.
     * @throws CityNotFoundException if any of the two cities are not found in the
     *                               Airline system
     */
    public Set<ArrayList<Route>> cheapestItinerary(String source, String destination) throws CityNotFoundException {

        Set<ArrayList<Route>> cheapestItnerary = new LinkedHashSet<ArrayList<Route>>();
        ArrayList<Route> cheapestDis = new ArrayList<Route>();

        int start = returnVert(source);
        int end = returnVert(destination);

        G.dijkstras(start, end, false);
        if (!G.marked[end]) {
            return cheapestItnerary;
        }

        Stack<Integer> path = new Stack<>();
        for (int x = end; x != start; x = G.edgeTo[x]) {
            path.push(x);
        }

        int prevVertex = start;
        while (!path.empty()) {
            int v = path.pop();

            int Costdifference = G.distTo[v] - G.distTo[prevVertex];

            // need to change the to the string of the city
            int d = 0;
            for(WeightedDirectedEdge e: G.adj[returnVert(cityNames[v])]){
                if(cityNames[e.to()] == cityNames[prevVertex]){
                    d = e.miles();
                    break;
                } 
            }
            Route distance = new Route(cityNames[prevVertex], cityNames[v], d, Costdifference);

            cheapestDis.add(distance);

            prevVertex = v;
        }
        cheapestItnerary.add(cheapestDis);

        return cheapestItnerary;
    }

    /**
     * finds cheapest path(s) between two cities going through a third city
     * 
     * @param source      the String source city name
     * @param transit     the String transit city name
     * @param destination the String destination city name
     * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
     *         paths. Each path is an ArrayList<Route> of city names that includes
     *         a Route out of source, into and out of transit, and into destination.
     * @throws CityNotFoundException if any of the three cities are not found in
     *                               the Airline system
     */
    public Set<ArrayList<Route>> cheapestItinerary(String source, String transit, String destination)
            throws CityNotFoundException {

        // dijkstra's twice?
        Set<ArrayList<Route>> totalTrip = new LinkedHashSet<ArrayList<Route>>();

        // for Johnstown to Pitt
        Set<ArrayList<Route>> sourceToTransit = cheapestItinerary(source, transit);
        ArrayList<Route> s = new ArrayList<Route>();

        for (ArrayList<Route> t : sourceToTransit) {
            for (Route r : t) {
                s.add(r);
            }
        }
        Set<ArrayList<Route>> transitToDestination = cheapestItinerary(transit, destination);

        for (ArrayList<Route> t : transitToDestination) {
            for (Route r : t) {
                s.add(r);
            }
        }
        totalTrip.add(s);

        return totalTrip;
    }

    /**
     * finds one Minimum Spanning Tree (MST) for each connected component of
     * the graph
     * 
     * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
     *         of Route objects representing the MST edges.
     */

    // use prim's
    // similar to dijkstra's
    // can use dijkstra's code
    // call prim's multiple times in every vertices
    public Set<Set<Route>> getMSTs() {

        Set<Set<Route>> mst = new LinkedHashSet<Set<Route>>();

        // each time return a different mst for each vertices
        for (int i = 0; i < cityNames.length; i++) {
            Set<Route> routes = new LinkedHashSet<>();
            G.prims(i);
            // just get the best edge and the edge to
            for (int j = 0; j < G.v; j++) {
                int source = G.edgeTo[j];
                int dest = j;
                int path = G.bestEdge[j];

                if(j == i) continue;

                double price = 0;
                for(WeightedDirectedEdge e: G.adj[returnVert(cityNames[source])]){
                    if(cityNames[e.to()] == cityNames[dest]){
                        price = e.cost();
                        break;
                        } 
                }
                Route r = new Route(cityNames[source], cityNames[dest], path, price);
                routes.add(r);

            }
            mst.add(routes);
        }
        return mst;
    }

    /**
     * finds all itineraries starting out of a source city and within a given
     * price
     * 
     * @param city   the String city name
     * @param budget the double budget amount in dollars
     * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
     *         less than or equal to the budget. Each path is an ArrayList<Route> of
     *         Route
     *         objects starting with a Route object out of the source city.
     */
    public Set<ArrayList<Route>> tripsWithin(String city, double budget) throws CityNotFoundException {
        Set<ArrayList<Route>> trips = tripsFromCity(city, budget);

        return trips;
    }

    // private method that uses DFS
    private Set<ArrayList<Route>> tripsFromCity(String city, double budget) throws CityNotFoundException {

        Set<ArrayList<Route>> tripsWithin = new LinkedHashSet<ArrayList<Route>>();
        tripList.clear();
        for (int i = 0; i < cityNames.length; i++) {
            G.DFS(returnVert(city), i);
        }
        // loop through list of all possible routes out of the city
        for (List<String> list : tripList) {
            // System.out.print(list);
            // System.out.println(count + " ");
            // count ++;
            ArrayList<Route> fRoute = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                // get city name
                String cName = list.get(i);

                if (i == list.size() - 1) {
                    break;
                }
                // get destination name
                String dest = list.get(i + 1);

                double price;
                int distance;

                Set<Route> routes = retrieveDirectRoutesFrom(cName);

                for (Route r : routes) {
                    if (r.destination == dest) {
                        price = r.price;
                        distance = r.distance;
                        // new route
                        Route finalRoute = new Route(cName, dest, distance, price);
                        fRoute.add(finalRoute);
                    } else
                        continue;

                }
            }
            // checks for if the path matches the budget
            double totalPrice = 0;
            for (Route r : fRoute) {
                totalPrice = totalPrice + r.price;

            }
            if (totalPrice <= budget && fRoute.size() > 0)
                tripsWithin.add(fRoute);
            else
                continue;
        }

        return tripsWithin;
    }

    /**
     * finds all itineraries within a given price regardless of the
     * starting city
     * 
     * @param budget the double budget amount in dollars
     * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
     *         less than or equal to the budget. Each path is an ArrayList<Route> of
     *         Route
     *         objects.
     */
    public Set<ArrayList<Route>> tripsWithin(double budget) {

        // use trip within city, budget to get every single route from each city that
        // matches the budget
        Set<ArrayList<Route>> trips = new LinkedHashSet<ArrayList<Route>>();
        try {
            for (int i = 0; i < cityNames.length; i++) {
                String city = cityNames[i];
                trips.addAll(tripsWithin(city, budget));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trips;
    }

    private class Digraph {
        private final int v;
        private int e;
        private LinkedList<WeightedDirectedEdge>[] adj;
        private boolean[] marked; // marked[v] = is there an s-v path
        private int[] edgeTo; // edgeTo[v] = previous edge on shortest s-v path
        private int[] distTo; // distTo[v] = number of edges shortest s-v path
        private int[] bestEdge; // bestEdge[v] = best edge on the mst path

        /**
         * Create an empty digraph with v vertices.
         */
        public Digraph(int v) {
            if (v < 0)
                throw new RuntimeException("Number of vertices must be nonnegative");
            // size of verticies for graph
            this.v = v;
            this.e = 0;
            @SuppressWarnings("unchecked")
            // creates adj list for every vert
            LinkedList<WeightedDirectedEdge>[] temp = (LinkedList<WeightedDirectedEdge>[]) new LinkedList[v];
            adj = temp;
            for (int i = 0; i < v; i++)
                adj[i] = new LinkedList<WeightedDirectedEdge>();
        }

        /**
         * Add the edge e to this digraph.
         */
        public void addEdge(WeightedDirectedEdge edge) {
            // where the edge starts
            int from = edge.from();
            // where the edge goes to
            int to = edge.to();
            adj[from].add(edge);

            // checking if both directions are connected
            // if empty
            if (adj[to].size() == 0) {
                // the destination's list
                WeightedDirectedEdge edge2 = new WeightedDirectedEdge(edge.to(), edge.from(), edge.miles, edge.cost);
                adj[to].add(edge2);
            }
            // if not empty check if the edge is connected
            if (!isConnected(edge)) {
                WeightedDirectedEdge edge2 = new WeightedDirectedEdge(edge.to(), edge.from(), edge.miles, edge.cost);
                adj[to].add(edge2);
            }
            e++;
        }

        // private method is connected
        private boolean isConnected(WeightedDirectedEdge edge) {

            for (WeightedDirectedEdge e : G.adj[edge.to()]) {
                if (e.to() == edge.from())
                    return true;
            }
            return false;
        }

        /**
         * Return the edges leaving vertex v as an Iterable.
         * To iterate over the edges leaving vertex v, use foreach notation:
         * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
         */
        public Iterable<WeightedDirectedEdge> adj(int v) {
            return adj[v];
        }

        public void DFSPath(int v, int d,
                boolean[] visited,
                List<String> Path) {

            if (v == d) {
                // System.out.println(localPathList);
                // System.out.println(localPathList.size());
                ArrayList<String> StringList = new ArrayList<String>(Path);
                tripList.add(StringList);
                return;
            }
            visited[v] = true;

            for (WeightedDirectedEdge w : adj(v)) {
                if (!visited[w.to()]) {
                    Path.add(cityNames[w.to()]);
                    DFSPath(w.to(), d, visited, Path);

                    Path.remove(cityNames[w.to()]);
                }
            }
            visited[v] = false;
        }

        public void DFS(int v, int d) {
            boolean visited[] = new boolean[cityNames.length];

            ArrayList<String> pathList = new ArrayList<>();

            pathList.add(cityNames[v]);
            // goes through paths using recursion
            DFSPath(v, d, visited, pathList);

        }

        public void bfs(int source) {
            marked = new boolean[this.v];
            distTo = new int[this.e];
            edgeTo = new int[this.v];

            Queue<Integer> q = new LinkedList<Integer>();
            for (int i = 0; i < v; i++) {
                distTo[i] = INFINITY;
                marked[i] = false;
            }
            distTo[source] = 0;
            marked[source] = true;
            q.add(source);

            while (!q.isEmpty()) {
                int v = q.remove();
                for (WeightedDirectedEdge w : adj(v)) {
                    if (!marked[w.to()]) {
                        edgeTo[w.to()] = v;
                        distTo[w.to()] = distTo[v] + 1;
                        marked[w.to()] = true;
                        q.add(w.to());
                    }
                }
            }
        }

        public void dijkstras(int source, int destination, boolean whichWeight) {

            // if true then calculate cost if false then caluclate cost
            if (!whichWeight) {
                marked = new boolean[this.v];
                distTo = new int[this.v];
                edgeTo = new int[this.v];

                for (int i = 0; i < v; i++) {
                    distTo[i] = INFINITY;
                    marked[i] = false;
                }
                distTo[source] = 0;
                marked[source] = true;
                int nMarked = 1;

                int current = source;

                while (nMarked < this.v) {
                    for (WeightedDirectedEdge w : adj(current)) {
                        if (distTo[current] + w.cost() < distTo[w.to()]) {
                            // TODO:update edgeTo and distTo
                            edgeTo[w.to()] = current;
                            distTo[w.to()] = distTo[current] + (int) w.cost;

                        }
                    }
                    // Find the vertex with minimim path distance
                    // This can be done more effiently using a priority queue!
                    int min = INFINITY;
                    current = -1;

                    for (int i = 0; i < distTo.length; i++) {
                        if (marked[i])
                            continue;
                        if (distTo[i] < min) {
                            min = distTo[i];
                            current = i;
                        }
                    }

                    // TODO: Update marked[] and nMarked. Check for disconnected graph.
                    if (current == -1)
                        break;
                    marked[current] = true;
                    nMarked++;
                }
            } else if (whichWeight) {

                marked = new boolean[this.v];
                distTo = new int[this.v];
                edgeTo = new int[this.v];

                for (int i = 0; i < v; i++) {
                    distTo[i] = INFINITY;
                    marked[i] = false;
                }
                distTo[source] = 0;
                marked[source] = true;
                int nMarked = 1;

                int current = source;

                while (nMarked < this.v) {
                    for (WeightedDirectedEdge w : adj(current)) {
                        if (distTo[current] + w.miles() < distTo[w.to()]) {
                            // TODO:update edgeTo and distTo
                            edgeTo[w.to()] = current;
                            distTo[w.to()] = distTo[current] + w.miles;

                        }
                    }
                    // Find the vertex with minimim path distance
                    // This can be done more effiently using a priority queue!
                    int min = INFINITY;
                    current = -1;

                    for (int i = 0; i < distTo.length; i++) {
                        if (marked[i])
                            continue;
                        if (distTo[i] < min) {
                            min = distTo[i];
                            current = i;
                        }
                    }

                    // TODO: Update marked[] and nMarked. Check for disconnected graph.
                    if (current == -1)
                        break;
                    marked[current] = true;
                    nMarked++;
                }

            }
        }

        // prims algorithim
        public void prims(int source) {
            // initalizes the size of the adj list
            // keeps track of if we visited
            marked = new boolean[this.v];
            // best edge
            bestEdge = new int[this.v];
            // previous/parent edge
            edgeTo = new int[this.v];

            // sets up the adj list
            for (int i = 0; i < v; i++) {
                bestEdge[i] = INFINITY;
                marked[i] = false;
            }
            // starts from source
            bestEdge[source] = source;
            // source marked as true
            marked[source] = true;
            // number of verticies marked
            int nMarked = 1;

            int current = source;
            // while it does not have all the verticies marked
            while (nMarked < this.v) {
                // so get edges from each of the neightbors and pick the smallest one
                for (WeightedDirectedEdge w : adj(current)) {

                    if (w.miles < bestEdge[w.to()] && !marked[w.to()]) {
                        bestEdge[w.to()] = w.miles;
                        edgeTo[w.to()] = w.from();
                    }
                }
                // Find the vertex with minimim path distance
                // This can be done more effiently using a priority queue!
                int min = INFINITY;
                current = -1;

                for (int i = 0; i < bestEdge.length; i++) {
                    if (marked[i])
                        continue;
                    if (bestEdge[i] < min) {
                        min = bestEdge[i];
                        current = i;
                    }
                }

                // TODO: Update marked[] and nMarked. Check for disconnected graph.
                if (current == -1)
                    break;
                marked[current] = true;
                nMarked++;
            }
        }
    }

    /**
     * The <tt>WeightedDirectedEdge</tt> class represents a weighted edge in an
     * directed graph.
     */
    private class WeightedDirectedEdge {
        private final int v;
        private final int w;
        private int miles;
        private double cost;

        /**
         * Create a directed edge from v to w with given weight.
         */
        public WeightedDirectedEdge(int v, int w, int miles, double cost) {
            this.v = v;
            this.w = w;
            this.miles = miles;
            this.cost = cost;
        }

        public int from() {
            return v;
        }

        public int to() {
            return w;
        }

        public int miles() {
            return miles;
        }

        public double cost() {
            return cost;
        }
    }
}
