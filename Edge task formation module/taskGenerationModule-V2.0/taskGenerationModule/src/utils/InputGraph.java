/**
 * creates a graph object where object contains vertex and edjacency list of 
 * edges. Each edge is directed and each edge having some weights which denotes
 * output size from one microservice to another microservice.
 */

package utils;

import java.util.ArrayList;

public class InputGraph {

    public class Pair {
        private int vertex;
        private int weight;

        public Pair(int _vertex, int _weight){
            vertex = _vertex;
            weight = _weight;
        }

        public int getVertexFromPair() {
            return vertex;
        }

        public int getWeightFromPair() {
            return weight;
        }
    }

    private static InputGraph instance = null;

    private int noOfVertices;
    private int noOfEdges;
    private ArrayList<Integer> vertices;
    private ArrayList<Pair>[] edges;
    private ArrayList<Pair>[] parents;
    private int[] inDegree;

    private InputGraph() {
        noOfVertices = MicroService.getMicroServiceListSize();
        vertices = new ArrayList<Integer>(noOfVertices);
        edges = new ArrayList[noOfVertices];
        parents = new ArrayList[noOfVertices];
        inDegree = new int[noOfVertices];
        noOfEdges = 0;

        for (int i = 0; i < noOfVertices; i++) {
            edges[i] = new ArrayList<Pair>();
            parents[i] = new ArrayList<Pair>();
            inDegree[i] = 0;
        }
    }

    public static InputGraph getInstance() {
        if(instance == null) {
            instance = new InputGraph();
        }
        return instance;
    }

    public void addEdge(int sourceVertex, int destinantionVertex, int weight) {
        edges[sourceVertex].add(new Pair(destinantionVertex, weight));
        parents[destinantionVertex].add(new Pair(sourceVertex, weight));
        inDegree[destinantionVertex]++;
        vertices.add(sourceVertex, 1);
        noOfEdges++;
    }

    public ArrayList<Pair> getEdgesFromVertex(int sourceVertex){
        return edges[sourceVertex];
    }

    public int[] getIndegree() {
        return inDegree;
    }

    public ArrayList<Pair> getParentList(int vertex) {
        return parents[vertex];
    }

    public ArrayList<Pair> getEdgeList(int vertex) {
        return edges[vertex];
    }

    public int getMaxOutEdgeWeight(int vertex) {

        int maxWeight = 0;
        for(int i = 0; i < edges[vertex].size(); ++i) {
            Pair tempPair = edges[vertex].get(i);
            if(tempPair.weight > maxWeight)
                maxWeight = tempPair.weight;
        }
        return maxWeight;
    }

    public int getNoOfVertices() {
        return noOfVertices;
    }

}
