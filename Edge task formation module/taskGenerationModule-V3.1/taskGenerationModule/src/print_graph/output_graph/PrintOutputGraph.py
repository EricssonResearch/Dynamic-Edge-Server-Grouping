import time
import networkx as nx
import matplotlib.pyplot as plt
import random

import sys
import os

import ParseOutputGraph
import PrintTaskOutputGraph

# Get the path to the parent directory
parent_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "input_graph"))
sys.path.append(parent_dir)
import ParseInputGraph

def drawMicroserviceOutputGraph():

    edgeList = ParseInputGraph.parseXMLFile()
    taskList = ParseOutputGraph.parseXMLFile()

    # Create a Directed Acyclic Graph (DAG)
    G = nx.DiGraph()

    # Add nodes to the graph
    nodeList = []
    for i in range (0, len(edgeList)):
        vertexName = "m" + str(i)
        nodeList.append(vertexName)
    G.add_nodes_from(nodeList)

    # Add edges (connections) between nodes to form a DAG with edge weights
    for edges in edgeList:
        srcVertex = edges.getVertexId()
        pairList = edges.getEdgeList()
        for pair in pairList:
            G.add_edge("m"+srcVertex, "m"+pair[0], weight=int(pair[1]))

    # Use spring layout for better graph layout with different random seed each time
    random_seed = random.randint(1, 1000000)
    k = 500.0
    scale=0.1
    pos = nx.spring_layout(G, seed=random_seed, k=k, scale=scale)

    # Extract edge weights as labels for visualization
    edge_labels = {(u, v): d["weight"] for u, v, d in G.edges(data=True)}

    colorList = mapTaskToColor(taskList)
    microserviceTaskDict = mapMicroserviceTask(taskList)

    # Draw the graph with customized node styles
    node_colors = setNodeColors(colorList, microserviceTaskDict, G)
    node_shapes = {node: "o" for node in G.nodes()}

    # Draw the graph
    nx.draw_networkx_nodes(G, pos, node_color=node_colors, node_shape='o', node_size=450, nodelist=G.nodes())
    nx.draw_networkx_labels(G, pos, font_size=14, font_weight="bold")
    nx.draw_networkx_edges(G, pos)
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=10, font_color="red")

    # Create a legend with color boxes and string labels
    legend_labels = [f'{label}' for label, color in zip(list(colorList.keys()), list(colorList.values()))]
    legend_handles = [plt.Line2D([0], [0], marker='o', color='w', markerfacecolor=color, label=label, markersize=12) for color, label in zip(list(colorList.values()), legend_labels)]

    # Display the graph
    plt.title("Microservice Output Graph")
    plt.axis("off")
    plt.legend(handles=legend_handles, labels=legend_labels, loc='upper left',fontsize=12)
    plt.savefig('output/microservice_task_group.jpg', dpi=300, bbox_inches='tight')
    plt.show()
    plt.clf()

    PrintTaskOutputGraph.drawGraph(colorList)

def mapTaskToColor(taskList):
    colorList = {}
    for task in taskList:
        # Generate random RGB values between 0 and 255
        red = random.randint(0, 255)
        green = random.randint(0, 255)
        blue = random.randint(0, 255)
        colorList[task.getTaskName()] = (red/255, green/255, blue/255, 1)
    return colorList

def mapMicroserviceTask(taskList):
    microserviceTaskDict = {}
    for task in taskList:
        for microserviceName in task.getMicroServiceList():
            microserviceTaskDict[microserviceName] = task.getTaskName()
    
    return microserviceTaskDict 


def setNodeColors(colorList, microserviceTaskDict, G):
    nodeColors = []
    for node in G.nodes():
        task = microserviceTaskDict[node]
        nodeColors.append(colorList[task])

    return nodeColors


if __name__ == "__main__":
    drawMicroserviceOutputGraph()