package task_allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import utils.Application;
import utils.EdgeServer;
import utils.Task;

class EdgeServerPair {
    public double executionTime;
    public EdgeServer edgeServer;

    public EdgeServerPair(EdgeServer _edgeServer, double _executionTime) {
        executionTime = _executionTime;
        edgeServer = _edgeServer;
    }
}

public class TaskAllocation {
    private static TaskAllocation instance = null;

    private TaskAllocation() {}

    public static TaskAllocation getInstance() {
        if(instance == null) {
            instance = new TaskAllocation();
        }
        return instance;
    }

    private Queue<Application> sortApplications(ArrayList<Application> applicationList) {
        Queue<Application> tempAppQueue = new LinkedList<Application>();
        ArrayList<Application> tempAppList = new ArrayList<Application>();

        /*
         * find the applications which are delayed and put them in temp list and
         * sort them in decreasing order according to the delay amount
         * insert them into the queue in that sorted order
         */
        for(int i = 0; i < applicationList.size(); ++i) {
            Application appInstance = applicationList.get(i);
            if(appInstance.isDelayed()) {
                tempAppList.add(appInstance);
            }
        }
        for(int i = 0; i < tempAppList.size(); ++i) {
            Application appInstance = tempAppList.get(i);
            applicationList.remove(appInstance);
        }
        Collections.sort(tempAppList, new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return Double.compare(app2.getDelayTime(), app1.getDelayTime());
            }
        });
        for(int i = 0; i < tempAppList.size(); ++i) {
            tempAppQueue.add(tempAppList.get(i));
        }
        tempAppList.clear();

        /*
         * sort the non-delayed applications increasing order of their deadlines
         * insert them into the queue in that sorted order
         */
        Collections.sort(applicationList, new Comparator<Application>() {
            @Override
            public int compare(Application app1, Application app2) {
                return Double.compare(app1.getExecutionTimeLimit(), app2.getExecutionTimeLimit());
            }
        });
        for(Application appInstance: applicationList) {
            tempAppQueue.add(appInstance);
        }
        applicationList.clear();

        return tempAppQueue;
    }

    private double calculateCurrentTimeStamp(ArrayList<EdgeServer> edgeServerList) {

        double minTimeStamp = Double.MAX_VALUE;
        for(EdgeServer edgeServerInstance: edgeServerList) {
            if(!edgeServerInstance.isFree())
                minTimeStamp = Math.min(minTimeStamp, edgeServerInstance.getReleaseTime());
        }

        if(minTimeStamp == Double.MAX_VALUE)
            return 0;

        return minTimeStamp;
    }

    private double caculateNextTimeStamp(ArrayList<EdgeServer> edgeServerList) {
        return calculateCurrentTimeStamp(edgeServerList);
    }

    private ArrayList<EdgeServer> findAvailableEdgeServers(ArrayList<EdgeServer> edgeServerList, double timeStamp) {

        ArrayList<EdgeServer> availableEdgeServerList = new ArrayList<EdgeServer>();
        for(int i = 0; i < edgeServerList.size(); ++i) {
            EdgeServer edgeServerInstance = edgeServerList.get(i);
            if(edgeServerInstance.getReleaseTime() <= timeStamp) {
                edgeServerInstance.setFree(true);
                availableEdgeServerList.add(edgeServerInstance);
            }
        }
        return availableEdgeServerList;
    }

    private boolean allocateTaskToEdgeServer(Application appInstance, ArrayList<EdgeServer> availableEdgeServerList, double timeStamp) {

        if(availableEdgeServerList.isEmpty())
            return false;

        if(appInstance.getTimeElapsed() > timeStamp)
            return false;
        
        int nextTaskId = appInstance.getCurrentExecutedTaskId() + 1;
        Task taskInstance = appInstance.getTaskFromTaskList(nextTaskId);
        double queuingTime = 0, processingTime = 0, networkTime = 0, totalTime;
        ArrayList<EdgeServerPair> selectedEdgeServerList = new ArrayList<EdgeServerPair>();

        for(EdgeServer edgeServerInstance: availableEdgeServerList) {
            queuingTime = (double) taskInstance.getTaskDataSize() / (double) edgeServerInstance.getDownlinkDatarate();
            processingTime = (taskInstance.getTaskDataSize() * appInstance.getComputationalComplexity()) / edgeServerInstance.getComputationalCapacity();
            if(nextTaskId - 1 == 0) {
                networkTime = 0;
            }
            else {
                int lastTaskId = nextTaskId - 1;
                Task prevTaskInstance = appInstance.getTaskFromTaskList(lastTaskId);
                if(prevTaskInstance.getAssignedEdgeServerId() == edgeServerInstance.getEdgeServerId()) {
                    networkTime = 0;
                }
                else {
                    EdgeServer prevEdgeServerInstance = EdgeServer.getEdgeServerFromList(prevTaskInstance.getAssignedEdgeServerId());
                    networkTime = ((double) prevTaskInstance.getTaskOutputDataSize() / (double) prevEdgeServerInstance.getUplinkDatarate()) +
                                  ((double) prevTaskInstance.getTaskOutputDataSize() / (double) edgeServerInstance.getDownlinkDatarate());
                }
            }
            totalTime = queuingTime + processingTime + networkTime;
            
            if(taskInstance.getTaskDataSize() + taskInstance.getTaskOutputDataSize() <= edgeServerInstance.getMemoryCapacity()) {
                selectedEdgeServerList.add(new EdgeServerPair(edgeServerInstance, totalTime));
            }
        }

        if(selectedEdgeServerList.size() == 0)
            return false;
        
        Collections.sort(selectedEdgeServerList, new Comparator<EdgeServerPair>() {
            @Override
            public int compare(EdgeServerPair ep1, EdgeServerPair ep2) {
                return Double.compare(ep1.executionTime, ep2.executionTime);
            }
        });
        EdgeServer selectedEdgeServerInstance = selectedEdgeServerList.get(0).edgeServer;
        totalTime = selectedEdgeServerList.get(0).executionTime;
        selectedEdgeServerInstance.setReleaseTime(appInstance.getTimeElapsed() + totalTime);
        selectedEdgeServerInstance.setFree(false);
        availableEdgeServerList.remove(selectedEdgeServerInstance);
        taskInstance.setAssignedEdgeServerId(selectedEdgeServerInstance.getEdgeServerId());
        taskInstance.setExecutionTime(totalTime);
        appInstance.setTimeElapsed(appInstance.getTimeElapsed() + totalTime);
        if(appInstance.getTimeElapsed() > appInstance.getExecutionTimeLimit()) {
            appInstance.setDelayed();
            appInstance.setDelayTime();
        }
        appInstance.setCurrentExecutedTaskId(nextTaskId);
        return true;
    }

    public void init() {
        ArrayList<Application> availableApplicationList = Application.getApplicationList();
        double timeStamp;
        
        while(!availableApplicationList.isEmpty()) {
            
            ArrayList<EdgeServer> availableEdgeServerList;
            ArrayList<EdgeServer> edgeServerList = EdgeServer.getEdgeServerList();

            // Calculate current time stamp
            timeStamp = calculateCurrentTimeStamp(edgeServerList);
            System.out.println("Timestamp: " + String.format("%.2f", timeStamp));
            System.out.println("---------------------");

            // set free the edge servers and make them available at this timestamp
            availableEdgeServerList = findAvailableEdgeServers(edgeServerList, timeStamp);

            // form the queue of the applications
            Queue<Application> applicationQueue = sortApplications(availableApplicationList);
            availableApplicationList.clear();

            // choose applications from queue and try to allocate them in available edge servers
            ArrayList<Application> notAllocatedAppList = new ArrayList<Application>();
            while(applicationQueue.isEmpty() == false) {
                
                Application appInstance = applicationQueue.remove();
                if(!allocateTaskToEdgeServer(appInstance, availableEdgeServerList, timeStamp)) {
                    notAllocatedAppList.add(appInstance);
                }
                else {
                    if(!appInstance.isNextTaskAvailable()) {
                        appInstance.setComplete();
                        appInstance.setTotalExecutionTime();
                    }
                    else {
                        availableApplicationList.add(appInstance);
                    }
                    System.out.println("A" + appInstance.getApplicationId() + "T" + appInstance.getCurrentExecutedTaskId() + " -> Edge " +
                                       appInstance.getTaskFromTaskList(appInstance.getCurrentExecutedTaskId()).getAssignedEdgeServerId() +
                                       "(" + String.format("%.2f", appInstance.getTimeElapsed()) + ")");
                }
            }
            double nextTimeStamp = caculateNextTimeStamp(edgeServerList);
            for(Application appInstance: notAllocatedAppList) {
                appInstance.setTimeElapsed(nextTimeStamp);
                if(appInstance.getTimeElapsed() > appInstance.getExecutionTimeLimit()) {
                    appInstance.setDelayed();
                    appInstance.setDelayTime();
                }
                availableApplicationList.add(appInstance);
            }
            System.out.println();
        }

        
    }
}
