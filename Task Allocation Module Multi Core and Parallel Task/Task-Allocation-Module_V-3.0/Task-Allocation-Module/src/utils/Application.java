package utils;

import java.util.ArrayList;

import utils.Task.Edge;

public class Application {

    private static ArrayList<Application> applicationList = null;

    private int id;
    private double executionTimeLimit;
    private double computationalComplexity;
    private ArrayList<Task> taskList;
    private boolean delayed;
    private double delayTime;
    private int NO_OF_TASKS;
    private ArrayList<Task> currentExecutedTaskList;
    private double timeElapsed;
    private double totalExecutionTime;
    private boolean complete;

    public Application(int _id, double _executionTimeLimit, double _computationalComplexity) {
        id = _id;
        executionTimeLimit = _executionTimeLimit;
        computationalComplexity = _computationalComplexity;
        taskList = new ArrayList<Task>();
        delayed = false;
        NO_OF_TASKS = 0;
        currentExecutedTaskList = new ArrayList<Task>();
        complete = false;
    }

    public static Application createApplication(int _id, double _executionTimeLimit, double _computationalComplexity) {
        Application appInstance = new Application(_id, _executionTimeLimit, _computationalComplexity);
        if(applicationList == null) {
            applicationList = new ArrayList<Application>();
        }
        applicationList.add(appInstance);
        return appInstance;
    }

    public Task addTask(int _id, boolean _highPriority, int _core, int _dataSize) {
        Task taskInstance = Task.createTask(_id, _highPriority, _core, _dataSize);
        taskList.add(taskInstance);
        NO_OF_TASKS++;
        return taskInstance;
    }

    public int getAppId() {
        return id;
    }

    public double getExecutionTimeLimit() {
        return executionTimeLimit;
    }

    public double getComputationalComplexity() {
        return computationalComplexity;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public int getNoOfTasks() {
        return NO_OF_TASKS;
    }

    public Task getTask(int taskId) {
        Task taskInstance = null;
        for(Task task: taskList) {
            if(task.getId() == taskId) {
                taskInstance = task;
                break;
            }
        }
        return taskInstance;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public void checkTasksStatus(double time) {
        for(Task taskInstance: taskList) {
            if(taskInstance.isRunning() == true && taskInstance.getTaskCompletionTime() <= time) {
                taskInstance.setRunningStatus(false);
                taskInstance.setCompletionStatus(true);
                ArrayList<Edge> tempEdgeList = taskInstance.getEdgeList();
                for(Edge edge: tempEdgeList) {
                    int childTaskId = edge.getNextTaskId();
                    Task childTaskInstance = getTask(childTaskId);
                    childTaskInstance.decreaseInDegree();
                }
                EdgeServer edgeServerInstance = taskInstance.getAllocatedEdgeServer();
                edgeServerInstance.releaseCores(taskInstance.getAllocatedCoreIdList());
                int taskTotalDataSize = taskInstance.getDataSize() + taskInstance.getMaximumOutputDataSize();
                edgeServerInstance.releaseMemoryCapacity(taskTotalDataSize);
            }
        }
        // boolean completeStatus = true;
        // for(Task taskInstance: taskList) {
        //     completeStatus = completeStatus && taskInstance.isComplete();
        // }
        // complete = completeStatus;
    }

    public static ArrayList<Application> getApplicationList() {
        return applicationList;
    }

    public static ArrayList<Application> getAvailableApplicationList(double time) {
        ArrayList<Application> availableApplicationList = new ArrayList<Application>();
        for(Application app: applicationList) {
            app.checkTasksStatus(time);
            if(app.getCurrentExecutedTaskList().size() != 0) {
                availableApplicationList.add(app);
            }
        }
        return availableApplicationList;
    }

    public static void setTaskIndegrees() {
        for(Application app: applicationList) {
            for(Task taskInstance: app.taskList) {
                ArrayList<Edge> tempEdgeList = taskInstance.getEdgeList();
                for(Edge edgeInstance: tempEdgeList) {
                    int nextTaskId = edgeInstance.getNextTaskId();
                    Task nextTaskInstance = app.getTask(nextTaskId);
                    nextTaskInstance.increaseInDegree();
                    nextTaskInstance.addParentTask(taskInstance);
                }
            }
        }
    }

    public ArrayList<Task> getCurrentExecutedTaskList() {
        currentExecutedTaskList.clear();
        for(Task taskInstance: taskList) {
            if(taskInstance.isComplete() == false && taskInstance.isRunning() == false && taskInstance.getCurrentInDegree() == 0) {
                currentExecutedTaskList.add(taskInstance);
            }
        }
        return currentExecutedTaskList;
    }

    public static void printAll() {
        for(Application app: applicationList) {
            System.out.println("Application: " + (app.id + 1));
            System.out.println("-----------------------");
            System.out.println("Execution Time Limit: " + app.executionTimeLimit);
            System.out.println("Computational Complexity: " + app.computationalComplexity);
            for(Task task: app.taskList) {
                System.out.println("Task " + (task.getId() + 1));
                System.out.println("High Priority: " + task.isHighPriority());
                System.out.println("Core: " + task.getCore());
                System.out.println("Data size: " + task.getDataSize());

                for(Edge edge: task.getEdgeList()) {
                    System.out.println("(" + (edge.getNextTaskId() + 1) + ", " + edge.getOutputDataSize() + ")");
                }
            }
        }
    }


}
