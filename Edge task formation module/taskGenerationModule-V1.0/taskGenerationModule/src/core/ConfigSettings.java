/**
 * configSettings parse the EdgeServer XML file and set all the parameters of the edge server.
 * parse the MicroService XML file to set all the parameyters of the microservices
 * parse the inputGraph XML file to set the input graph
 */

package core;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.EdgeServer;
import utils.InputGraph;
import utils.MicroService;


public class ConfigSettings {
    
    private static ConfigSettings instance = null;
    private Document edgeServerDoc = null;
    private Document microServiceDoc = null;
    private Document microServiceGraphInputDoc = null;

    private int NUM_OF_EDGE_SERVER;
    private int NUM_OF_MICRO_SERVICES;
    private int NUM_OF_VERTEX;
    private int NUM_OF_EDGES;

    private ConfigSettings() {
        NUM_OF_EDGE_SERVER = 0;
        NUM_OF_MICRO_SERVICES = 0;
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
    public boolean initialize(String edgeServerFile, String microServiceFile, String microServiceGraphInputFile) {
        boolean result = false;

        // parse details of Edge Servers
        result = parseEdgeServerXML(edgeServerFile);

        // parse details of Microservices
        result = parseMicroServicesXML(microServiceFile);

        // parse details of Microservice Graph Input File
        result = parseMicroServiceGraphInputXML(microServiceGraphInputFile);

        return result;

    }

    // parse the input graph XML file
    private boolean parseMicroServiceGraphInputXML(String microServiceGraphInputFilePath) {
        
        try {
            int destVertex;
            int edgeWeight;

            File microServiceGraphInputFile = new File(microServiceGraphInputFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            microServiceGraphInputDoc = dBuilder.parse(microServiceGraphInputFile);
            microServiceGraphInputDoc.getDocumentElement().normalize();

            InputGraph graphInstance = InputGraph.getInstance();
            NodeList vertexList = microServiceGraphInputDoc.getElementsByTagName("node");
            for(int i = 0; i < vertexList.getLength(); ++i) {
                NUM_OF_VERTEX++;
                Node inputGraphVertex = vertexList.item(i);

                Element inputGraphVertexElement = (Element) inputGraphVertex;
                isAttributePresent(inputGraphVertexElement, "name");

                NodeList edgeList = inputGraphVertexElement.getElementsByTagName("edge");
                for(int j = 0; j < edgeList.getLength(); ++j) {
                    NUM_OF_EDGES++;
                    Node edge = edgeList.item(j);

                    Element edgeElement = (Element) edge;
                    destVertex = Integer.parseInt(isElementPresent(edgeElement, "vertex"));
                    edgeWeight = Integer.parseInt(isElementPresent(edgeElement, "weight"));
                    graphInstance.addEdge(i, destVertex, edgeWeight);
                }
            }

        } catch(Exception e) {
            System.out.println("Microservice Graph input XML cannot be parsed! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    // parse the microservice XML file
    private boolean parseMicroServicesXML(String microServiceFilePath) {
        
        try {
            int microServiceCore;
            int microServiceMemory;
            double microServiceExecutionTime;

            File microServiceFile = new File(microServiceFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            microServiceDoc = dBuilder.parse(microServiceFile);
            microServiceDoc.getDocumentElement().normalize();

            NodeList microServiceList = microServiceDoc.getElementsByTagName("microservice");
            for(int i = 0; i < microServiceList.getLength(); ++i) {
                NUM_OF_MICRO_SERVICES++;
                Node microServiceNode = microServiceList.item(i);

                Element microServiceElement = (Element) microServiceNode;
                isAttributePresent(microServiceElement, "name");
                microServiceCore = Integer.parseInt(isElementPresent(microServiceElement, "cores"));
                microServiceMemory = Integer.parseInt(isElementPresent(microServiceElement, "memory"));
                microServiceExecutionTime = Double.parseDouble(isElementPresent(microServiceElement, "execution_time"));
                MicroService.createInstance(i, microServiceCore, microServiceMemory, microServiceExecutionTime);
            }

        } catch(Exception e) {
            System.out.println("Microservice XML cannot be parsed! Terminating...");
            e.printStackTrace();
            System.exit(1);
        }

        return true;
    }

    // parse the edge server XML file
    private boolean parseEdgeServerXML(String edgeServerFilePath) {

        try {
            int edgeServerCore;
            int edgeServerMemory;

            File edgeServerFile = new File(edgeServerFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            edgeServerDoc = dBuilder.parse(edgeServerFile);
            edgeServerDoc.getDocumentElement().normalize();

            NodeList edgeServerList = edgeServerDoc.getElementsByTagName("edgeserver");
            for(int i = 0; i < edgeServerList.getLength(); ++i) {
                NUM_OF_EDGE_SERVER++;
                Node edgeServerNode = edgeServerList.item(i);

                Element edgeServerElement = (Element) edgeServerNode;
                isAttributePresent(edgeServerElement, "name");
                edgeServerCore = Integer.parseInt(isElementPresent(edgeServerElement, "cores"));
                edgeServerMemory = Integer.parseInt(isElementPresent(edgeServerElement, "memory"));
                EdgeServer.createInstance(i, edgeServerCore, edgeServerMemory);
                
            }
        } catch(Exception e) {
            System.out.println("Edge server XML cannot be parsed! Terminating...");
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
