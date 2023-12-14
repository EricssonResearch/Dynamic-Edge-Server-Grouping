package utils;

public class Task {
    private int id;
    private int dataSize;
    private int outputDataSize;
    private int assignedEdgeServerId;
    private double executionTime;

    private Task(int _id, int _dataSize, int _outputDataSize) {
        id = _id;
        dataSize = _dataSize;
        outputDataSize = _outputDataSize;
    }

    public static Task createInstance(int _id, int _dataSize, int _outputDataSize) {
        Task instance = new Task(_id, _dataSize, _outputDataSize);
        return instance;
    }

    public int getTaskId() {
        return id;
    }

    public int getTaskDataSize() {
        return dataSize;
    }

    public int getTaskOutputDataSize() {
        return outputDataSize;
    }

    public void setAssignedEdgeServerId(int _id) {
        assignedEdgeServerId = _id;
    }

    public int getAssignedEdgeServerId() {
        return assignedEdgeServerId;
    }

    public void setExecutionTime(double _time) {
        executionTime = _time;
    }

    public double getExecutionTime() {
        return executionTime;
    }
}
