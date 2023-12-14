/**
 * Creates Edge Server objects.
 * Parameters considered:
 *  - no. of cores
 *  - memory
 */

package utils;

import java.util.ArrayList;

public class EdgeServer {
    private int id;
    private int cores;
    private int memory;

    private static ArrayList<EdgeServer> edgeServerList = null;

    private EdgeServer(int _id, int _cores, int _memory) {
        id = _id;
        cores = _cores;
        memory = _memory;
    }

    private static void addToEdgeServerList(EdgeServer instance) {
        if(edgeServerList == null) {
            edgeServerList = new ArrayList<EdgeServer>();
        }
        edgeServerList.add(instance);
    }

    public static void createInstance(int _id, int _cores, int _memory) {
        EdgeServer instance = new EdgeServer(_id, _cores, _memory);
        addToEdgeServerList(instance);
    }

    public static int getEdgeServerListSize() {
        if(edgeServerList == null)
            return 0;
        return edgeServerList.size();
    }

    public static ArrayList<EdgeServer> getEdgeServerList() {
        return edgeServerList;
    }
    
    public int getEdgeServerId() {
        return id;
    }

    public int getEdgeServerCores() {
        return cores;
    }

    public int getEdgeServerMemory() {
        return memory;
    }
}
