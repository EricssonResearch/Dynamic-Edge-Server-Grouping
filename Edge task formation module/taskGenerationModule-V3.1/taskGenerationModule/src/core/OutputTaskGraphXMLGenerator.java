package core;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.Task;

public class OutputTaskGraphXMLGenerator {
     
    private static OutputTaskGraphXMLGenerator instance = null;

    private OutputTaskGraphXMLGenerator() {}

    public static OutputTaskGraphXMLGenerator getInstance() {
        if(instance == null) {
            instance = new OutputTaskGraphXMLGenerator();
        }
        return instance;
    }

    public void generateOutputTaskGraphXML(String outputFolder) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            
            // Create the root element
            Element rootElement = document.createElement("graph");
            document.appendChild(rootElement);

            // Create nodes and edges
            ArrayList<Task.TaskEdge>[] taskEdgeList = Task.getTaskEdgeList();
            for(int i = 0; i < taskEdgeList.length; ++i) {
                addNode(document, rootElement, "task_" + i, taskEdgeList[i]);
            }

            // Write the document to a file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(outputFolder + "/task_output_graph.xml"));
            transformer.transform(source, result);

        } catch(ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private void addNode(Document doc, Element parent, String name, ArrayList<Task.TaskEdge> edgeList) {
        Element node = doc.createElement("node");
        node.setAttribute("name", name);

        Element edgesElement = doc.createElement("edges");
        for(Task.TaskEdge edge: edgeList) {
            Element edgeElement = doc.createElement("edge");

            Element vertex = doc.createElement("vertex");
            vertex.appendChild(doc.createTextNode(Integer.toString(edge.getTaskEdgeDesTaskId())));

            Element weight = doc.createElement("weight");
            weight.appendChild(doc.createTextNode(Integer.toString(edge.getTaskEdgeOutputSize())));

            edgeElement.appendChild(vertex);
            edgeElement.appendChild(weight);
            edgesElement.appendChild(edgeElement);
        }

        node.appendChild(edgesElement);
        parent.appendChild(node);
    }
}
