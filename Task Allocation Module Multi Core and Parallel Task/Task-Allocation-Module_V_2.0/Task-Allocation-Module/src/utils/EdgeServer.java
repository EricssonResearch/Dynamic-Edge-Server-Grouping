package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class EdgeServer {

    private static ArrayList<EdgeServer> edgeServerList = null;

    public class Core {
        private int id;
        private int computationalCapacity;
        private boolean free;
        private double releaseTime;
        Core(int _id, int _computationalCapacity) {
            id = _id;
            computationalCapacity = _computationalCapacity;
            free = true;
            releaseTime = 0;
        }

        public int getComputationalCapacity() {
            return computationalCapacity;
        }

        public int getCoreId() {
            return id;
        }

        public boolean isFree() {
            return free;
        }

        public double getReleaseTime() {
            return releaseTime;
        }

    }
    private int id;
    private int memoryCapacity;
    private int downlinkDataRate;
    private int uplinkDataRate;
    private ArrayList<Core> coreList;
    private ArrayList<Core> availableCoreList;

    private EdgeServer(int _id, int _memoryCapacity, int _downlinkDataRate, int _uplinkDataRate) {
        id = _id;
        memoryCapacity = _memoryCapacity;
        downlinkDataRate = _downlinkDataRate;
        uplinkDataRate = _uplinkDataRate;
        coreList = new ArrayList<Core>();
        availableCoreList = new ArrayList<Core>();
    }

    private static void addToEdgeServerList(EdgeServer instance) {
        if(edgeServerList == null) {
            edgeServerList = new ArrayList<EdgeServer>();
        }
        edgeServerList.add(instance);
    }

    public static EdgeServer createEdgeServer(int _id, int _memoryCapacity, int _downlinkDataRate, int _uplinkDataRate) {
        EdgeServer instance = new EdgeServer(_id, _memoryCapacity, _downlinkDataRate, _uplinkDataRate);
        addToEdgeServerList(instance);
        return instance;
    }

    public void addCore(int _id, int _computationalCapacity) {
        Core coreInstance = new Core(_id, _computationalCapacity);
        coreList.add(coreInstance);
    }

    public static ArrayList<EdgeServer> getEdgeServerList() {
        return edgeServerList;
    }

    public static EdgeServer getEdgeServerFromList(int _id) {
        return edgeServerList.get(_id);
    }

    public int getEdgeServerId() {
        return id;
    }

    public int getMemoryCapacity() {
        return memoryCapacity;
    }

    public int getDownlinkDataRate() {
        return downlinkDataRate;
    }

    public int getuplinkDataRate() {
        return uplinkDataRate;
    }

    public ArrayList<EdgeServer.Core> getAvailableCores() {
        return availableCoreList;
    }

    public void setCoresBusy(ArrayList<Integer> coreIds, double _releaseTime) {
        for(int coreId: coreIds) {
            coreList.get(coreId).releaseTime = _releaseTime;
            coreList.get(coreId).free = false;
        }
    }

    public void occupyMemoryCapacity(int _memoryCapacity) {
        memoryCapacity -= _memoryCapacity;
    }

    public void releaseMemoryCapacity(int _memoryCapacity) {
        memoryCapacity += _memoryCapacity;
    }

    public void releaseCores(ArrayList<Integer> coreIds) {
        for(int coreId: coreIds) {
            coreList.get(coreId).free = true;
        }
    }

    public boolean isCoreAvailable(int _cores) {
        availableCoreList.clear();
        for(Core core: coreList) {
            if(core.free == true) {
                availableCoreList.add(core);
            }
        }
        if(availableCoreList.size() >= _cores) {
            return true;
        }
        return false;
    }

    public boolean isMemoryAvailable(int totalMemory) {
        if(memoryCapacity >= totalMemory) {
            return true;
        }
        return false;
    }

    public ArrayList<Core> findBestCores(int k) {
        ArrayList<Core> bestCoreList = new ArrayList<Core>();
        PriorityQueue<Core> maxCoreHeap = new PriorityQueue<>(Comparator.comparingInt(Core::getComputationalCapacity).reversed());
        for(Core core: coreList) {
            if(core.free) {
                maxCoreHeap.offer(core);
            }
        }
        while(k > 0) {
            bestCoreList.add(maxCoreHeap.poll());
            k--;
        }
        return bestCoreList;
    }




    public static void printAll() {
        for(EdgeServer edgeServer: edgeServerList) {
            System.out.println("Server id: " + (edgeServer.id + 1));
            System.out.println("Memory capacity: " + edgeServer.memoryCapacity);
            System.out.println("Downlink Data rate: " + edgeServer.downlinkDataRate);
            System.out.println("Uplink Data rate: " + edgeServer.uplinkDataRate);
            for(Core core: edgeServer.coreList) {
                System.out.println("Core " + (core.id + 1) + " / " + core.computationalCapacity);
            }
        }
    }


}
