package utils;

import java.util.ArrayList;

public class EdgeServer {
    private int id;
    private int memoryCapacity;
    private int computationalCapacity;
    private int downlinkDatarate;
    private int uplinkDatarate;
    private double releaseTime;
    private boolean free;

    private static ArrayList<EdgeServer> edgeServerList = null;

    private EdgeServer(int _id, int _memoryCapacity, int _computationalCapacity, int _downlinkDatarate, int _uplinkDatarate) {
        id = _id;
        memoryCapacity = _memoryCapacity;
        computationalCapacity = _computationalCapacity;
        downlinkDatarate = _downlinkDatarate;
        uplinkDatarate = _uplinkDatarate;
        releaseTime = 0;
        free = true;
    }

    private static void addToEdgeServerList(EdgeServer instance) {
        if(edgeServerList == null) {
            edgeServerList = new ArrayList<EdgeServer>();
        }
        edgeServerList.add(instance);
    }

    public static void createInstance(int _id, int _memoryCapacity, int _computationalCapacity, int _downlinkDatarate, int _uplinkDatarate) {
        EdgeServer instance = new EdgeServer(_id, _memoryCapacity, _computationalCapacity, _downlinkDatarate, _uplinkDatarate);
        addToEdgeServerList(instance);
    }

    public static ArrayList<EdgeServer> getEdgeServerList() {
        return edgeServerList;
    }

    public static EdgeServer getEdgeServerFromList(int _id) {
        return edgeServerList.get(_id - 1);
    }

    public int getEdgeServerId() {
        return id;
    }

    public int getMemoryCapacity() {
        return memoryCapacity;
    }

    public int getComputationalCapacity() {
        return computationalCapacity;
    }

    public int getDownlinkDatarate() {
        return downlinkDatarate;
    }

    public int getUplinkDatarate() {
        return uplinkDatarate;
    }

    public void setReleaseTime(double _time) {
        releaseTime = _time;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setFree(boolean flag) {
        free = flag;
    }

    public boolean isFree() {
        return free;
    }
    
}
