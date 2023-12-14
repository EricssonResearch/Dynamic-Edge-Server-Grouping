package utils;

import java.util.ArrayList;

public class Application {
    private int id;
    private double executionTimeLimit;
    private double computationalComplexity;
    private ArrayList<Task> taskList;
    private boolean delayed;
    private double delayTime;
    private int NO_OF_TASKS;
    private int currentExecutedTaskId;
    private double timeElapsed;
    private double totalExecutionTime;
    private boolean complete;

    private static ArrayList<Application> applicationList = null;

    private Application(int _id, double _executionTimeLimit, double _computationalComplexity) {
        id = _id;
        executionTimeLimit = _executionTimeLimit;
        computationalComplexity = _computationalComplexity;
        taskList = new ArrayList<Task>();
        delayed = false;
        NO_OF_TASKS = 0;
        timeElapsed = 0;
        totalExecutionTime = 0;
        complete = false;
        currentExecutedTaskId = 0;
        delayTime = 0;
    }

    public static Application createInstance(int _id, double _executionTimeLimit, double _computationalComplexity) {
        Application instance = new Application(_id, _executionTimeLimit, _computationalComplexity);
        return instance;
    }

    public void addTask(int _id, int _dataSize, int _outputDataSize) {
        Task taskInstance = Task.createInstance(_id, _dataSize, _outputDataSize);
        taskList.add(taskInstance);
        NO_OF_TASKS++;
    }

    public static void addToApplicationList(Application instance) {
        if(applicationList == null) {
            applicationList = new ArrayList<Application>();
        }
        applicationList.add(instance);
    }

    public static ArrayList<Application> getApplicationList() {
        return applicationList;
    }

    public int getApplicationId() {
        return id;
    }

    public double getExecutionTimeLimit() {
        return executionTimeLimit;
    }

    public double getComputationalComplexity() {
        return computationalComplexity;
    }

    public ArrayList<Task> getTaskListOfApplication() {
        return taskList;
    }

    public void setDelayed() {
        delayed = true;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public int getNoOfTasks() {
        return NO_OF_TASKS;
    }

    public void setCurrentExecutedTaskId(int _id) {
        currentExecutedTaskId = _id;
    }

    public int getCurrentExecutedTaskId() {
        return currentExecutedTaskId;
    }

    public Task getTaskFromTaskList(int _id) {
        return taskList.get(_id - 1);
    }

    public void setTimeElapsed(double _time) {
        if(_time > timeElapsed)
            timeElapsed = _time;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public boolean isNextTaskAvailable() {
        if(currentExecutedTaskId == NO_OF_TASKS)
            return false;
        else
            return true;
    }

    public void setTotalExecutionTime() {
        totalExecutionTime = timeElapsed;
    }

    public double getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setComplete() {
        complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setDelayTime() {
        delayTime = timeElapsed - executionTimeLimit;
    }

    public double getDelayTime() {
        return delayTime;
    }

}
