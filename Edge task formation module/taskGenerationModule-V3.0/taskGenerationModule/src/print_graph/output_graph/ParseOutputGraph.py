import xml.etree.ElementTree as ET


class Task:
    def __init__(self, taskName):
        self.taskName = taskName
        self.microServiceList = []

    def addMicroServiceToList(self, microService):
        self.microServiceList.append(microService)

    def addTaskName(self, taskName):
        self.taskName = taskName

    def getMicroServiceList(self):
        return self.microServiceList
    
    def getTaskName(self):
        return self.taskName
    

def parseXMLFile():
    xml_file = 'output/tasks.xml'
    tree = ET.parse(xml_file)
    root = tree.getroot()

    tasks = []
    for taskElem in root.findall('task'):
        taskName = taskElem.get('name')
        head, sep, tail = taskName.partition('_')
        taskObj = Task("t" + tail)

        for microServiceEle in taskElem.findall('microservice'):
            microService = microServiceEle.text
            taskObj.addMicroServiceToList(microService)
        tasks.append(taskObj)

    return tasks