import xml.etree.ElementTree as ET

class Edge:
    def __init__(self):
        self.vertexId = 0
        self.edgeList = []

    def setVertexId(self, value):
        self.vertexId = value

    def setEdge(self, vertex, weight):
        self.edgeList.append((vertex, weight))

    def getVertexId(self):
        return self.vertexId
    
    def getEdgeList(self):
        return self.edgeList

def parseXMLOutputFile():
    # Load the XML file
    tree = ET.parse('output/task_output_graph.xml')
    root = tree.getroot()

    edges = []
    for nodeElem in root.findall('node'):
        nodeName = nodeElem.get('name')
        head, sep, tail = nodeName.partition('_')
        myObj = Edge()
        myObj.setVertexId(tail)

        for edgeEle in nodeElem.findall('edges/edge'):
            vertex = edgeEle.find('vertex').text
            weight = edgeEle.find('weight').text
            myObj.setEdge(vertex, weight)
        
        edges.append(myObj)
    
    return edges
