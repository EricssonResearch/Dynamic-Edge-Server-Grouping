package utils;

import java.util.ArrayList;

public class Task {

    public class Edge {
        private int nextTaskId;
        private int outputDataSize;

        public Edge(int _nextTaskId, int _outputDataSize) {
            nextTaskId = _nextTaskId;
            outputDataSize = _outputDataSize;
        }

        public int getNextTaskId() {
            return nextTaskId;
        }

        public int getOutputDataSize() {
            return outputDataSize;
        }
    }

    private int id;
    private boolean highPriority;
    private int core;
    private int dataSize;
    private ArrayList<Edge> edgeList;
    private int currentInDegree;
    private boolean complete;
    private boolean running;
    private ArrayList<Task> parentTaskList;
    private double completionTime;
    private EdgeServer allocatedEdgeServer;
    private ArrayList<Integer> allocatedCoreIdList;

    private Task(int _id, boolean _highPriority, int _core, int _dataSize) {
        id = _id;
        highPriority = _highPriority;
        core = _core;
        dataSize = _dataSize;
        edgeList = new ArrayList<Edge>();
        currentInDegree = 0;
        complete = false;
        running = false;
        parentTaskList = new ArrayList<Task>();
        completionTime = 0;
        allocatedCoreIdList = new ArrayList<Integer>();
    }

    public static Task createTask(int _id, boolean _highPriority, int _core, int _dataSize) {
        Task taskInstance = new Task(_id, _highPriority, _core, _dataSize);
        return taskInstance;
    }

    public int getId() {
        return id;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public int getCore() {
        return core;
    }

    public int getDataSize() {
        return dataSize;
    }

    public ArrayList<Edge> getEdgeList() {
        return edgeList;
    }

    public int getCurrentInDegree() {
        return currentInDegree;
    }

    public EdgeServer getAllocatedEdgeServer() {
        return allocatedEdgeServer;
    }

    public void setAllocatedCoreIds(ArrayList<Integer> coreIds) {
        for(int id: coreIds) {
            allocatedCoreIdList.add(id);
        }
    }

    public ArrayList<Integer> getAllocatedCoreIdList() {
        return allocatedCoreIdList;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunningStatus(boolean status) {
        running = status;
    }

    public void setCompletionStatus(boolean status) {
        complete = status;
    }

    public void addEdge(int _taskId, int _outputDataSize) {
        Edge edgeInstance = new Edge(_taskId, _outputDataSize);
        edgeList.add(edgeInstance);
    }

    public void addParentTask(Task parentTaskInstance) {
        parentTaskList.add(parentTaskInstance);
    }

    public void increaseInDegree() {
        currentInDegree++;
    }

    public void decreaseInDegree() {
        currentInDegree--;
    }

    public double getTaskCompletionTime() {
        return completionTime;
    }

    public void setTaskCompletionTime(double time) {
        completionTime = time;
    }

    public int getMaximumOutputDataSize() {
        int dataSize = Integer.MIN_VALUE;
        for(Edge edge: edgeList) {
            dataSize = Math.max(dataSize, edge.outputDataSize);
        }
        return dataSize;
    }

    public double calculateAvgOutputDataFromParentTask() {
        double totalSize = 0;
        int count = 0;
        for(Task parentTaskInstance: parentTaskList) {
            ArrayList<Edge> parentTaskEdgeList = parentTaskInstance.getEdgeList();
            for(Edge edge: parentTaskEdgeList) {
                if(this.id == edge.nextTaskId) {
                    totalSize += edge.outputDataSize;
                    count++;
                }
            }
        }
        return totalSize / (double)count;
    }

    public ArrayList<Task> getParentTaskList() {
        return parentTaskList;
    }

    public void setAllocatedEdgeServer(EdgeServer edgeServerInstance) {
        allocatedEdgeServer = edgeServerInstance;
    }

}
