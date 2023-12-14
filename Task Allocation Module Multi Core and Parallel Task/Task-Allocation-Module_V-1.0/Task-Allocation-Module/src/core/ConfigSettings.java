package core;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Application;
import utils.EdgeServer;
import utils.Task;

public class ConfigSettings {
    
    private static ConfigSettings instance = null;
    private Document edgeServerDoc = null;
    private Document applicationDoc = null;

    public static ConfigSettings getInstance() {
        if(instance == null) {
            instance = new ConfigSettings();
        }
        return instance;
    }

    /*
     * Reads configuration files and stores the information to local variables
     */
    public boolean initialize(String edgeServerFile, String applicationFile) {
        boolean result = false;

        // parse details of edge servers
        result = parseEdgeServerXML(edgeServerFile);

        // parse details of applications
        result = parseApplicationXML(applicationFile);

        return result;

    }

    // parse edgeservers.xml file
    private boolean parseEdgeServerXML(String edgeServerFilePath) {

        try {
            int edgeServerMemoryCapacity;
            int edgeServerDownlinkDataRate;
            int edgeServerUplinkDataRate;
            int edgeServerId;
            int coreId;
            int coreComputationalCapacity;

            File edgeServerFile = new File(edgeServerFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            edgeServerDoc = dBuilder.parse(edgeServerFile);
            edgeServerDoc.getDocumentElement().normalize();
            
            NodeList edgeServerList = edgeServerDoc.getElementsByTagName("edgeserver");
            for(int i = 0; i < edgeServerList.getLength(); ++i) {
                Node edgeServerNode = edgeServerList.item(i);
                
                Element edgeServerElement = (Element) edgeServerNode;
                edgeServerId = getNumericPart(isAttributePresent(edgeServerElement, "name"));
                edgeServerMemoryCapacity = Integer.parseInt(isElementPresent(edgeServerElement, "memory"));
                edgeServerDownlinkDataRate = Integer.parseInt(isElementPresent(edgeServerElement, "downlink_datarate"));
                edgeServerUplinkDataRate = Integer.parseInt(isElementPresent(edgeServerElement, "uplink_datarate"));
                EdgeServer edgeServerInstance = EdgeServer.createEdgeServer(edgeServerId-1, edgeServerMemoryCapacity, edgeServerDownlinkDataRate, edgeServerUplinkDataRate);
                
                NodeList coreNodeList = edgeServerElement.getElementsByTagName("core");
                for(int j = 0; j < coreNodeList.getLength(); ++j) {
                    Node coreNode = coreNodeList.item(j);
                    Element coreElement = (Element) coreNode;
                    coreId = getNumericPart(isAttributePresent(coreElement, "name"));
                    coreComputationalCapacity = Integer.parseInt(isElementPresent(coreElement, "computational_capacity"));
                    edgeServerInstance.addCore(coreId-1, coreComputationalCapacity);
                }

            }

        } catch(Exception e) {
            System.out.println("Edge server XML cannot be parsed! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    // parse application.xml file
    private boolean parseApplicationXML(String applicationFilePath) {

        try {
            int appId;
            double appExecutionTimeLimit;
            double appComputationalComplexity;
            int taskId;
            boolean taskHighPriority;
            int taskCore;
            int taskDataSize;
            int nextTaskId;
            int taskOutputDataSize;

            File applicationFile = new File(applicationFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            applicationDoc = dBuilder.parse(applicationFile);
            applicationDoc.getDocumentElement().normalize();

            NodeList applicationList = applicationDoc.getElementsByTagName("application");
            for(int i = 0; i < applicationList.getLength(); ++i) {
                Node applicationNode = applicationList.item(i);

                Element applicationElement = (Element) applicationNode;
                appId = getNumericPart(isAttributePresent(applicationElement, "name"));
                appExecutionTimeLimit = Double.parseDouble(isElementPresent(applicationElement, "execution_time_limit"));
                appComputationalComplexity = Double.parseDouble(isElementPresent(applicationElement, "computational_complexity"));
                Application appInstance = Application.createApplication(appId-1, appExecutionTimeLimit, appComputationalComplexity);

                NodeList taskList = applicationElement.getElementsByTagName("task");
                for(int j = 0; j < taskList.getLength(); ++j) {
                    Node taskNode = taskList.item(j);

                    Element taskElement = (Element) taskNode;
                    taskId = getNumericPart(isAttributePresent(taskElement, "name"));
                    if(Integer.parseInt(isElementPresent(taskElement, "high_priority")) == 0) {
                        taskHighPriority = false;
                    }
                    else {
                        taskHighPriority = true;
                    }
                    taskCore = Integer.parseInt(isElementPresent(taskElement, "core"));
                    taskDataSize = Integer.parseInt(isElementPresent(taskElement, "data_size"));
                    Task taskInstance = appInstance.addTask(taskId-1, taskHighPriority, taskCore, taskDataSize);

                    NodeList edgeList = taskElement.getElementsByTagName("edge");
                    for(int k = 0; k < edgeList.getLength(); ++k) {
                        Node edgeNode = edgeList.item(k);

                        Element edgeElement = (Element) edgeNode;
                        nextTaskId = Integer.parseInt(isElementPresent(edgeElement, "vertex"));
                        taskOutputDataSize = Integer.parseInt(isElementPresent(edgeElement, "weight"));

                        taskInstance.addEdge(nextTaskId-1, taskOutputDataSize);
                    }

                }
            }

        } catch(Exception e) {
            System.out.println("Application XML cannot be parsed! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    private int getNumericPart(String name) {
        String pattern = "\\d+"; // This regular expression matches one or more digits
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(name);

        m.find();
        String result = m.group();
        int num = Integer.parseInt(result);

        return num;
    }

    private String isAttributePresent(Element element, String key) {
        String value = element.getAttribute(key);
        if(value.isEmpty() || value == null) {
            throw new IllegalArgumentException("Attribute '" + key + "' is not found in '" + element.getNodeName() + "'");
        }
        return value;
    }

    private String isElementPresent(Element element, String key) {
        try {
            String value = element.getElementsByTagName(key).item(0).getTextContent();
            if(value.isEmpty() || value == null) {
                throw new IllegalArgumentException("Element '" + key + "' is not found in '" + element.getNodeName() + "'");
            }
            return value;
        } catch(Exception e) {
            throw new IllegalArgumentException("Element '" + key + "' is not found in '" + element.getNodeName() + "'");
        }
    }
}
