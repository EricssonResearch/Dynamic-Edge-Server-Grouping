/**
 * creates microservice object and returns that object
 * stores all microservice objects in a list
 * four object attributes are considered here:
 *  - id
 *  - no. of cores
 *  - memory
 *  - execution time
 *  - task group id
 */

package utils;

import java.util.ArrayList;

public class MicroService {
    private int id;
    private int cores;
    private int memory;
    private double executionTime;
    private int taskGroupId;
    private boolean assignedToTask;

    private static ArrayList<MicroService> microServiceList = null;

    private MicroService(int _id, int _cores, int _memory, double _executionTime) {
        id = _id;
        cores = _cores;
        memory = _memory;
        executionTime = _executionTime;
        assignedToTask = false;
    }

    private static void addToMicroServiceList(MicroService instance) {
        if(microServiceList == null) {
            microServiceList = new ArrayList<MicroService>();
        }
        microServiceList.add(instance);
    }

    public static void createInstance(int _id, int _cores, int _memory, double _executionTime) { 
        MicroService instance = new MicroService(_id, _cores, _memory, _executionTime);
        addToMicroServiceList(instance);
    }

    public static int getMicroServiceListSize() {
        if(microServiceList == null)
            return 0;
        return microServiceList.size();
    }

    public static ArrayList<MicroService> getMicroServiceList() {
        return microServiceList;
    }

    public static MicroService getMicroService(int microServiceId) {
        return microServiceList.get(microServiceId);
    }

    public int getMicroServiceId() {
        return id;
    }

    public int getMicroServiceCores() {
        return cores;
    }

    public int getMicroServiceMemory() {
        return memory;
    }

    public double getMicroServiceExecutionTime() {
        return executionTime;
    }

    public void assignTaskGroupId(int _taskGroupId) {
        taskGroupId = _taskGroupId;
        assignedToTask = true;
    }

    public int getTaskGroupId() {
        return taskGroupId;
    }

    public boolean isAssignToTask() {
        return assignedToTask;
    }
}
