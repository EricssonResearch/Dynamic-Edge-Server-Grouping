package utils;

import java.util.ArrayList;

public class Task {

    public class Pair {
        private int taskId;
        private int srcMicroService;
        private int desMicroService;
        private int outputSize;

        public Pair(int _taskId, int _srcMicroService, int _desMicroService, int _outputSize) {
            taskId = _taskId;
            srcMicroService = _srcMicroService;
            desMicroService = _desMicroService;
            outputSize = _outputSize;
        }

        public int getPairTaskId() {
            return taskId;
        }

        public int getPairSrcMicroService() {
            return srcMicroService;
        }

        public int getPairDesMicroService() {
            return desMicroService;
        }

        public int getPairOutputSize() {
            return outputSize;
        }

    }

    public class TaskEdge {
        private int srcTaskId;
        private int desTaskId;
        private int outputSize;

        public TaskEdge(int _srcTaskId, int _desTaskId, int _outputSize) {
            srcTaskId = _srcTaskId;
            desTaskId = _desTaskId;
            outputSize = _outputSize;
        }

        public int getTaskEdgeSrcTaskId() {
            return srcTaskId;
        }

        public int getTaskEdgeDesTaskId() {
            return desTaskId;
        }

        public int getTaskEdgeOutputSize() {
            return outputSize;
        }

        public void addEdgeOutputSize(int _outputSize) {
            outputSize += _outputSize;
        }
    }

    private int id;
    private int cores;
    private int memory;
    private double executionTime;
    private boolean resourceIntensive;
    private boolean executionTimeIntensive;
    private ArrayList<MicroService> microServiceList;
    private ArrayList<Pair> inboundDependencies;
    private ArrayList<Pair> outboundDependencies;

    private static ArrayList<Task> taskList = null;
    private static ArrayList<TaskEdge>[] taskEdgeList = null;

    private Task(int _id) {
        id = _id;
        cores = 0;
        memory = 0;
        executionTime = 0;
        resourceIntensive = false;
        executionTimeIntensive = false;
        microServiceList = new ArrayList<MicroService>();
        inboundDependencies = new ArrayList<Pair>();
        outboundDependencies = new ArrayList<Pair>();
    }

    private static void addToTaskList(Task instance) {
        if(taskList == null){
            taskList = new ArrayList<Task>();
        }
        taskList.add(instance);
    }

    private static void setTaskEdges() {
        InputGraph inputGraphInstance = InputGraph.getInstance();
        taskEdgeList = new ArrayList[taskList.size()];
        for(int i = 0; i < taskList.size(); ++i) {
            taskEdgeList[i] = new ArrayList<TaskEdge>();
        }

        for(Task task : taskList) {
            int srcTaskId = task.getTaskId();
            ArrayList<MicroService> microServiceList = task.getMicroServiceListOfTask();
            for(MicroService srcMicroService: microServiceList) {
                int srcMicroServiceId = srcMicroService.getMicroServiceId();
                int desMicroServiceId;
                ArrayList<InputGraph.Pair> desMicroServiceVertexList = inputGraphInstance.getEdgeList(srcMicroServiceId);
                for(InputGraph.Pair desMicroServiceVertex: desMicroServiceVertexList) {
                    desMicroServiceId = desMicroServiceVertex.getVertexFromPair();
                    int outputSize = desMicroServiceVertex.getWeightFromPair();
                    MicroService desMicroService = MicroService.getMicroService(desMicroServiceId);
                    int desTaskId = desMicroService.getTaskGroupId();
                    if(desTaskId != srcTaskId) {
                        boolean found = false;
                        taskEdgeLoop: for(TaskEdge taskEdge: taskEdgeList[srcTaskId]) {
                            if(taskEdge.desTaskId == desTaskId) {
                                taskEdge.addEdgeOutputSize(outputSize);
                                found = true;
                                break taskEdgeLoop;
                            }
                        }

                        if(!found) {
                            taskEdgeList[srcTaskId].add(task.new TaskEdge(srcTaskId, desTaskId, outputSize));
                        }
                        
                    }
                }

            }

        }
    }

    public static Task createInstance(MicroService microServiceInstance) {
        int taskId;
        if(taskList == null)
            taskId = 0;
        else
            taskId = taskList.size();
        Task instance = new Task(taskId);
        addToTaskList(instance);
        instance.addMicroServiceToTask(microServiceInstance);
        return instance;
    }

    public void addMicroServiceToTask(MicroService microServiceInstance) {
        int microServiceCores = microServiceInstance.getMicroServiceCores();
        int microServiceMemory = microServiceInstance.getMicroServiceMemory();
        double microServiceExecutionTime = microServiceInstance.getMicroServiceExecutionTime();

        microServiceList.add(microServiceInstance);
        cores = Math.max(cores, microServiceCores);
        memory = Math.max(memory, microServiceMemory);
        executionTime += microServiceExecutionTime;
    }

    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    public static Task getTask(int taskId) {
        return taskList.get(taskId);
    }

    public static void setInboundAndOutboundDependencies() {
        InputGraph inputGraphInstance = InputGraph.getInstance();
        int noOfMicroServices = inputGraphInstance.getNoOfVertices();

        for(int i = 0; i < noOfMicroServices; ++i) {
            ArrayList<InputGraph.Pair> edges = inputGraphInstance.getEdgeList(i);
            for(InputGraph.Pair edge: edges) {
                int desVertexId = edge.getVertexFromPair();
                int outputSize = edge.getWeightFromPair();

                MicroService srcMicroServiceInstance = MicroService.getMicroService(i);
                MicroService desMicroServiceInstance = MicroService.getMicroService(desVertexId);
                int srcMicroServiceTaskGroupId = srcMicroServiceInstance.getTaskGroupId();
                int desMicroServiceTaskGroupId = desMicroServiceInstance.getTaskGroupId();

                if(srcMicroServiceTaskGroupId != desMicroServiceTaskGroupId) {
                    Task srcTask = getTask(srcMicroServiceTaskGroupId);
                    Task desTask = getTask(desMicroServiceTaskGroupId);

                    srcTask.outboundDependencies.add(srcTask.new Pair(desMicroServiceTaskGroupId, i, desVertexId, outputSize));
                    desTask.inboundDependencies.add(desTask.new Pair(srcMicroServiceTaskGroupId, i, desVertexId, outputSize));
                }
                
            }
        }
        setTaskEdges();
    }

    public static ArrayList<TaskEdge>[] getTaskEdgeList() {
        return taskEdgeList;
    }

    public void setResourceIntensive() {
        resourceIntensive = true;
    }

    public void setExecutionTimeIntensive() {
        executionTimeIntensive = true;
    }

    public int getTaskId() {
        return id;
    }

    public int getTaskCores() {
        return cores;
    }

    public int getTaskMemory() {
        return memory;
    }

    public double getTaskExecutionTime() {
        return executionTime;
    }

    public boolean isResourceIntensive() {
        return resourceIntensive;
    }

    public boolean isExecutionTimeIntensive() {
        return executionTimeIntensive;
    }

    public ArrayList<MicroService> getMicroServiceListOfTask() {
        return microServiceList;
    }


    public static void printTasks() {
        
        for(Task task: taskList) {
            System.out.println("Task" + task.id);
            System.out.println("Cores: " + task.cores);
            System.out.println("Memory: " + task.memory);
            System.out.println("Execution Time: " + task.executionTime);
            System.out.print("Microservices: ");
            for(MicroService microService: task.microServiceList) {
                System.out.print("m" + microService.getMicroServiceId() + " ");
            }
            System.out.println();
            System.out.println("-----------------------------------------------");
        }
    }

    public static void printTaskDependencies() {

        for(Task task: taskList) {
            System.out.println("Task" + task.id);
            for(Pair dependency: task.inboundDependencies) {
                System.out.println("Task" + dependency.getPairTaskId() + " (m" + dependency.getPairSrcMicroService() + ") with output size " + dependency.getPairOutputSize());
            }
            System.out.println("-----------------------------------------------");
        }
    }

}
