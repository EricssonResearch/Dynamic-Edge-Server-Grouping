import networkx as nx
import matplotlib.pyplot as plt
import random

import ParseInputGraph

def drawGraph():

    edgeList = ParseInputGraph.parseXMLFile()

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
    k = 100.0
    scale=0.1
    pos = nx.spring_layout(G, seed=random_seed, k=k, scale=scale)

    # Extract edge weights as labels for visualization
    edge_labels = {(u, v): d["weight"] for u, v, d in G.edges(data=True)}

    # Draw the graph with customized node styles
    node_colors = ["skyblue" for node in G.nodes()]
    node_shapes = {node: "o" for node in G.nodes()}

    # Draw the graph
    nx.draw_networkx_nodes(G, pos, node_color=node_colors, node_shape='o', node_size=400, nodelist=G.nodes())
    nx.draw_networkx_labels(G, pos, font_size=12, font_weight="bold")
    nx.draw_networkx_edges(G, pos)
    nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=7, font_color="red")


    # Display the graph
    plt.title("Input Graph")
    plt.axis("off")
    plt.savefig('output/input_graph.jpg', dpi=300, bbox_inches='tight')
    plt.show()
    plt.clf()


if __name__ == "__main__":
    # draw_dag_with_weights()
    drawGraph()
