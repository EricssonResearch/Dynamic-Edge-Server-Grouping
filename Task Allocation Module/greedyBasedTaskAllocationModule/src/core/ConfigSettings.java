package core;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Application;
import utils.EdgeServer;

public class ConfigSettings {

    private static ConfigSettings instance = null;
    private Document edgeServerDoc = null;
    private Document applicationDoc = null;

    private int NUM_OF_EDGE_SERVER;
    private int NUM_OF_APPLICATION;

    private ConfigSettings() {
        NUM_OF_EDGE_SERVER = 0;
        NUM_OF_APPLICATION = 0;
    }

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
            int edgeServerComputationalCapacity;
            int edgeServerDownlinkDatarate;
            int edgeServerUplinkDatarate;

            File edgeServerFile = new File(edgeServerFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            edgeServerDoc = dBuilder.parse(edgeServerFile);
            edgeServerDoc.getDocumentElement().normalize();

            NodeList edgeServerList = edgeServerDoc.getElementsByTagName("edgeserver");
            for(int i = 0; i < edgeServerList.getLength(); ++i) {
                NUM_OF_EDGE_SERVER++;
                Node EdgeServerNode = edgeServerList.item(i);

                Element edgeServerElement = (Element) EdgeServerNode;
                isAttributePresent(edgeServerElement, "name");
                edgeServerMemoryCapacity = Integer.parseInt(isElementPresent(edgeServerElement, "memory_capacity"));
                edgeServerComputationalCapacity = Integer.parseInt(isElementPresent(edgeServerElement, "computational_capacity"));
                edgeServerDownlinkDatarate = Integer.parseInt(isElementPresent(edgeServerElement, "downlink_datarate"));
                edgeServerUplinkDatarate = Integer.parseInt(isElementPresent(edgeServerElement, "uplink_datarate"));
                EdgeServer.createInstance(i+1, edgeServerMemoryCapacity, edgeServerComputationalCapacity, edgeServerDownlinkDatarate, edgeServerUplinkDatarate);
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
            double applicationExecutionTimeLimit;
            double applicationComputationalComplexity;
            int taskDataSize;
            int taskOutputDataSize;

            File applicationFile = new File(applicationFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            applicationDoc = dBuilder.parse(applicationFile);
            applicationDoc.getDocumentElement().normalize();

            NodeList applicationList = applicationDoc.getElementsByTagName("application");
            for(int i = 0; i < applicationList.getLength(); ++i) {
                NUM_OF_APPLICATION++;
                Node applicationNode = applicationList.item(i);

                Element applicationElement = (Element) applicationNode;
                isAttributePresent(applicationElement, "name");
                applicationExecutionTimeLimit = Double.parseDouble(isElementPresent(applicationElement, "execution_time_limit"));
                applicationComputationalComplexity = Double.parseDouble(isElementPresent(applicationElement, "computational_complexity"));
                Application applicationInstance = Application.createInstance(i+1, applicationExecutionTimeLimit, applicationComputationalComplexity);

                NodeList taskList = applicationElement.getElementsByTagName("task");
                for(int j = 0; j < taskList.getLength(); ++j) {
                    Node taskNode = taskList.item(j);

                    Element taskElement = (Element) taskNode;
                    isAttributePresent(taskElement, "name");
                    taskDataSize = Integer.parseInt(isElementPresent(taskElement, "datasize"));
                    taskOutputDataSize = Integer.parseInt(isElementPresent(taskElement, "output_datasize"));
                    applicationInstance.addTask(j+1, taskDataSize, taskOutputDataSize);
                }

                Application.addToApplicationList(applicationInstance);
            }

        } catch(Exception e) {
            System.out.println("Application XML cannot be parsed! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    private void isAttributePresent(Element element, String key) {
        String value = element.getAttribute(key);
        if(value.isEmpty() || value == null) {
            throw new IllegalArgumentException("Attribute '" + key + "' is not found in '" + element.getNodeName() + "'");
        }
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
