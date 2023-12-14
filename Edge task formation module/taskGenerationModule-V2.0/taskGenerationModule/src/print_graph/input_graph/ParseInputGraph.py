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
    

def parseXMLFile():
    xml_file = 'config/inputgraph.xml'
    tree = ET.parse(xml_file)
    root = tree.getroot()

    noOfNodes = len(root.findall('node'))
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
