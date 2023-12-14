
/**
 * Describes the task formation algorithm working flow
 */

package task_formation;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import utils.EdgeServer;
import utils.InputGraph;
import utils.MicroService;
import utils.Task;


class VertexPair {
    public int vertex;
    public int weight;

    public VertexPair(int _vertex, int _weight){
        vertex = _vertex;
        weight = _weight;
    }

    public int getWeight() {
        return weight;
    }
}

public class TaskFormation {
    private static TaskFormation instance = null;

    private double avgExecutionTime;
    private int minEdgeServerCore;
    private int minEdgeServerMemory;

    private TaskFormation() {}

    // calculates average execution time of the tasks
    private void calculateAvgExecutionTime() {
        ArrayList<MicroService> microServiceList = MicroService.getMicroServiceList();
        double sumExecutionTime = 0;
        int noOfMicroService = MicroService.getMicroServiceListSize();
        for(MicroService microServiceInstance: microServiceList) {
            sumExecutionTime += microServiceInstance.getMicroServiceExecutionTime();
        }
        avgExecutionTime = (double)sumExecutionTime / (double)noOfMicroService;
    }

    // calculates minimum no. of cores available among all edge servers
    private void calculateMinEdgeServerCore() {
        ArrayList<EdgeServer> edgeServerList = EdgeServer.getEdgeServerList();
        minEdgeServerCore = Integer.MAX_VALUE;

        for(EdgeServer edgeServer: edgeServerList) {
            minEdgeServerCore = Math.min(minEdgeServerCore, edgeServer.getEdgeServerCores());
        }
    }

    // calculates minimum memory avaialble among edge servers.
    private void calculateMinEdgeServerMemory() {
        ArrayList<EdgeServer> edgeServerList = EdgeServer.getEdgeServerList();
        minEdgeServerMemory = Integer.MAX_VALUE;

        for(EdgeServer edgeServer: edgeServerList) {
            minEdgeServerMemory = Math.min(minEdgeServerMemory, edgeServer.getEdgeServerMemory());
        }        
    }

    // method for checking the server constraints
    private boolean checkServerConstraints(int parentVertexIndex, int vertexIndex) {
        MicroService parentMicroServiceInstance = MicroService.getMicroService(parentVertexIndex);
        MicroService childMicroServiceInstance = MicroService.getMicroService(vertexIndex);
        int parentVertexTaskId = parentMicroServiceInstance.getTaskGroupId();
        Task parentVertexTask = Task.getTask(parentVertexTaskId);

        if(Math.max(childMicroServiceInstance.getMicroServiceCores(), parentVertexTask.getTaskCores()) <= minEdgeServerCore &&
           Math.max(childMicroServiceInstance.getMicroServiceMemory(), parentVertexTask.getTaskMemory()) <= minEdgeServerMemory) {
            return true;
        }
        return false;
        
    }

    // method for checking execution time constraints for a task
    private boolean checkExecutionTimeConstraint(int parentVertexIndex, int vertexIndex) {
        MicroService parentMicroServiceInstance = MicroService.getMicroService(parentVertexIndex);
        MicroService childMicroServiceInstance = MicroService.getMicroService(vertexIndex);
        int parentVertexTaskId = parentMicroServiceInstance.getTaskGroupId();
        Task parentVertexTask = Task.getTask(parentVertexTaskId);

        if(childMicroServiceInstance.getMicroServiceExecutionTime() + parentVertexTask.getTaskExecutionTime() <= avgExecutionTime) {
            return true;
        }
        return false;
    }

    public static TaskFormation getInstance() {
        if(instance == null) {
            instance = new TaskFormation();
        }
        return instance;
    }

    public void init() {
        
        // calculate all the constratints
        calculateAvgExecutionTime();
        calculateMinEdgeServerCore();
        calculateMinEdgeServerMemory();

        InputGraph inputGraphInstance = InputGraph.getInstance();
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[inputGraphInstance.getNoOfVertices()];

        ArrayList<VertexPair> vertexList = new ArrayList<VertexPair>();

        // extract the nodes whose inDegree = 0
        int[] inDegree = inputGraphInstance.getIndegree();
        int maxEdgeWeight;
        for(int i = 0; i < inDegree.length; ++i) {
            if(inDegree[i] == 0) {
                maxEdgeWeight = inputGraphInstance.getMaxOutEdgeWeight(i);
                vertexList.add(new VertexPair(i, maxEdgeWeight));
            }
            visited[i] = false;
        }

        // sort the extracted nodes in descending order according to their max outgoing edge weight
        // push all the nodes into the queue
        Comparator<VertexPair> edgeWeighComparator = (p1, p2) -> Integer.compare(p2.weight, p1.weight);
        Collections.sort(vertexList, edgeWeighComparator);
        for(int i = 0; i < vertexList.size(); ++i) {
            queue.offer(vertexList.get(i).vertex);
            visited[vertexList.get(i).vertex] = true;
        }
        vertexList.clear();

        int vertexIndex, parentVertexIndex;
        while(!queue.isEmpty()) {

            while(!queue.isEmpty()) {
                vertexIndex = queue.poll();
                vertexList.add(new VertexPair(vertexIndex, 0));
            }
            
            Map<Integer, Boolean> parentStatusMap = new HashMap<>();
            for(VertexPair vertexPair: vertexList) {
                vertexIndex = vertexPair.vertex;
                ArrayList<InputGraph.Pair> parentNodeList = inputGraphInstance.getParentList(vertexIndex);
                PriorityQueue<VertexPair> maxHeapParent = new PriorityQueue<>(Comparator.comparing(VertexPair::getWeight).reversed());

                if(parentNodeList.size() != 0) {

                    // put all the parents of the vertex into a max heap
                    for(InputGraph.Pair pair: parentNodeList) {
                        int parentVertex = pair.getVertexFromPair();
                        int weight = pair.getWeightFromPair();

                        maxHeapParent.offer(new VertexPair(parentVertex, weight)); // max heap contains <parentVertexId, weight>
                    }

                    heapLoop: while(!maxHeapParent.isEmpty()) {
                        parentVertexIndex = maxHeapParent.poll().vertex;

                        if(checkServerConstraints(parentVertexIndex, vertexIndex) &&
                        checkExecutionTimeConstraint(parentVertexIndex, vertexIndex) && 
                        parentStatusMap.get(parentVertexIndex) == null) {

                            // add the vertex to parent task group.
                            parentStatusMap.put(parentVertexIndex, true);
                            int parentVertexTaskId = MicroService.getMicroService(parentVertexIndex).getTaskGroupId();
                            Task parentVertexTaskInstance = Task.getTask(parentVertexTaskId);
                            MicroService childMicroServiceInstance = MicroService.getMicroService(vertexIndex);
                            parentVertexTaskInstance.addMicroServiceToTask(childMicroServiceInstance);
                            childMicroServiceInstance.assignTaskGroupId(parentVertexTaskId);
                            break heapLoop;
                            
                        }
                    }

                }
            }

            // Assign new task to microservices that are not assigned to parent
            // microservice task group
            for(VertexPair vertexPair: vertexList) {
                vertexIndex = vertexPair.vertex;
                MicroService microServiceInstance = MicroService.getMicroService(vertexIndex);

                if(!microServiceInstance.isAssignToTask()) {
                    Task newTaskInstance = Task.createInstance(microServiceInstance);
                    microServiceInstance.assignTaskGroupId(newTaskInstance.getTaskId());

                    if(newTaskInstance.getTaskCores() > minEdgeServerCore || newTaskInstance.getTaskMemory() > minEdgeServerMemory) {
                        newTaskInstance.setResourceIntensive();
                    }
                    if(newTaskInstance.getTaskExecutionTime() > avgExecutionTime) {
                        newTaskInstance.setExecutionTimeIntensive();
                    }
                }
            }

            // decrease inDegree of the child nodes by 1
            for(VertexPair vertexPair: vertexList) {
                parentVertexIndex = vertexPair.vertex;
                ArrayList<InputGraph.Pair> edges = inputGraphInstance.getEdgesFromVertex(parentVertexIndex);

                for(InputGraph.Pair edge: edges) {
                    inDegree[edge.getVertexFromPair()]--;
                }
            }

            // put the newly become inDegree = 0 nodes in the queue in the sorted
            // descending order of their edge weight with parent vertex
            ArrayList<VertexPair> childVertexList = new ArrayList<VertexPair>();
            for(int i = 0; i < inputGraphInstance.getNoOfVertices(); ++i) {
                if(inDegree[i] == 0 && visited[i] == false) {
                    ArrayList<InputGraph.Pair> parentList = inputGraphInstance.getParentList(i);
                    for(InputGraph.Pair parentPair: parentList) {
                        childVertexList.add(new VertexPair(i, parentPair.getWeightFromPair()));
                    }
                }
            }

            Collections.sort(childVertexList, edgeWeighComparator);

            for(VertexPair vertexPair: childVertexList) {
                if(visited[vertexPair.vertex] == false) {
                    queue.offer(vertexPair.vertex);
                    visited[vertexPair.vertex] = true;
                }
            }

            vertexList.clear();

        }
        
        // set the dependencies among the tasks
        // print the task dependency graph
        Task.setInboundAndOutboundDependencies();

        // Task.printTasks();
        // Task.printTaskDependencies();
    }
}
