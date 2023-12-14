package core;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.MicroService;
import utils.Task;

public class TaskXMLGenerator {
    
    private static TaskXMLGenerator instance = null;

    private TaskXMLGenerator() {}

    private void addTaskElement(Document doc, Element parentElement, String taskName, ArrayList<MicroService> microServiceList) {
        Element taskElement = doc.createElement("task");
        taskElement.setAttribute("name", taskName);

        for(MicroService microService: microServiceList) {
            Element microserviceElement = doc.createElement("microservice");
            microserviceElement.appendChild(doc.createTextNode("m" + microService.getMicroServiceId()));
            taskElement.appendChild(microserviceElement);
        }
        parentElement.appendChild(taskElement);
    }

    public static TaskXMLGenerator getInstance() {
        if(instance == null) {
            instance = new TaskXMLGenerator();
        }
        return instance;
    }

    public void generateTaskXMLFile(String outputFolder) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // create root element
            Document doc = docBuilder.newDocument();
            Element tasksElement = doc.createElement("tasks");
            doc.appendChild(tasksElement);

            // create task elements
            ArrayList<Task> taskList = Task.getTaskList();
            for(Task task : taskList) {
                ArrayList<MicroService> microServiceList = task.getMicroServiceListOfTask();
                addTaskElement(doc, tasksElement, "task_" + task.getTaskId(), microServiceList);
            }

            // Write the XML content to a file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputFolder + "/tasks.xml");
            transformer.transform(source, result);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
