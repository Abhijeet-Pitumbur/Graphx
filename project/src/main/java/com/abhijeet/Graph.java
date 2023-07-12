package com.abhijeet;

import java.text.DecimalFormat;
import java.util.*;

public class Graph {

	private Map<Node, Set<Edge>> nodeMap;
	private Set<Edge> edgeSet;
	private int[] asciiNodes;
	private ArrayList<Integer> asciiPaths;
	private double[] x, y;
	private StringBuilder shortestPath;
	private double shortestPathWeight;
	private Node[] shortestPathNodes;

	public Graph(int minNodes, int maxNodes) {

		edgeSet = new LinkedHashSet<>();
		nodeMap = new LinkedHashMap<>();

		// Random number of nodes
		final Random random = new Random();
		final int numNodes = random.nextInt(maxNodes - minNodes + 1) + minNodes;

		// Random node coordinates
		generateNodes(numNodes);

		// Add all possible edges
		completeGraph();

	}

	private void generateNodes(final int numNodes) {

		Node node;
		Random random = new Random();

		// Lower and upper bounds for coordinates
		int minX, minY, maxX, maxY;
		minX = minY = 35;
		maxX = 1250;
		maxY = 790;

		// ASCII code for letter "A"
		int index = 65;

		do {

			node = new Node(Character.toString((char) index), random.nextInt(maxX - minX + 1) + minX, random.nextInt(maxY - minY + 1) + minY);

			// Increment index if node can be added
			if (addNode(node)) {
				index++;
			}

		}
		while (getNumNodes() < numNodes);

	}

	private void completeGraph() {
		for (Node firstNode : getNodes()) {
			for (Node secondNode : getNodes()) {
				if (firstNode.equals(secondNode)) {
					continue;
				}
				if (!containsEdge(firstNode, secondNode)) {
					Edge edge = new Edge(firstNode, secondNode);
					addEdge(firstNode, secondNode, edge);
				}
			}
		}
	}

	private boolean addNode(Node node) {

		// Add node if it is not overlapping another node
		if (getClosestNode(node.getX(), node.getY()) == null) {
			nodeMap.put(node, new LinkedHashSet<>());
			return true;
		}

		return false;

	}

	private void addEdge(Node firstNode, Node secondNode, Edge edge) {
		if (edgeSet.add(edge)) {
			nodeMap.get(firstNode).add(edge);
			nodeMap.get(secondNode).add(edge);
		}
	}

	public Set<Node> getNodes() {
		return new LinkedHashSet<>(nodeMap.keySet());
	}

	public Set<Edge> getEdges() {
		return edgeSet;
	}

	public int getNumNodes() {
		return nodeMap.keySet().size();
	}

	public int getNumEdges() {
		return edgeSet.size();
	}

	public int getNumPaths() {
		return factorial(getNumNodes());
	}

	public void findPaths(int start) {

		// Initialise array with ASCII-encoded nodes
		if (asciiNodes == null) {
			int numNodes = getNumNodes();
			asciiNodes = new int[numNodes];
			for (int index = 0; index < numNodes; index++) {
				asciiNodes[index] = index + 65;
			}
		}

		// Initialise array with ASCII-encoded paths
		if (asciiPaths == null) {
			asciiPaths = new ArrayList<>();
		}

		// If end is reached, nothing is left to permute
		if (start >= getNumNodes() - 1) {

			// Add paths to array
			for (int index : asciiNodes) {
				asciiPaths.add(index);
			}

		} else {

			int swap;

			for (int index = start; index <= getNumNodes() - 1; index++) {

				// Swap node indices
				swap = asciiNodes[start];
				asciiNodes[start] = asciiNodes[index];
				asciiNodes[index] = swap;

				// Recurse on the sub-array to find permutations
				findPaths(start + 1);

				// Backtrack
				swap = asciiNodes[start];
				asciiNodes[start] = asciiNodes[index];
				asciiNodes[index] = swap;

			}

		}

	}

	public void findShortestPath() {

		int counter = 0;
		shortestPath = new StringBuilder();
		shortestPathWeight = 100000;
		StringBuilder path;
		double pathWeight;
		int numNodes = getNumNodes();
		int numPaths = getNumPaths();
		double x1, y1, x2, y2;
		int pathDisplayLimit = 1000;
		DecimalFormat formatter = new DecimalFormat("###,###");

		// Array with nodes created from node set
		Node[] nodes = new Node[numNodes];
		int index = 0;
		for (Node node : getNodes()) {
			nodes[index] = node;
			index++;
		}

		// Array with coordinates of all nodes
		findCoordinates();

		for (int i = 0; i < numPaths; i++) {

			pathWeight = 0;
			path = new StringBuilder();

			// Append to path for each node in ASCII-encoded paths array
			for (int j = 0; j < numNodes - 1; j++) {

				// Append node to path
				path.append((char) (int) (asciiPaths.get(counter))).append("-");

				// Set weight so far
				x1 = x[(asciiPaths.get(counter)) - 65];
				y1 = y[(asciiPaths.get(counter)) - 65];
				x2 = x[(asciiPaths.get(counter + 1)) - 65];
				y2 = y[(asciiPaths.get(counter + 1)) - 65];
				pathWeight += Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

				// Next node in path
				counter++;

			}

			// Append last node to path
			path.append((char) (int) (asciiPaths.get(counter)));

			// Set shortest path so far
			if (pathWeight < shortestPathWeight) {
				shortestPath = new StringBuilder(path.toString());
				shortestPathWeight = pathWeight;
			}

			// Add to list of paths if display limit is not reached yet
			if (i < pathDisplayLimit) {
				App.addPath(formatter.format(i + 1) + ".", path.toString(), formatter.format(pathWeight) + " px");
			} else if (i == pathDisplayLimit) {
				App.addPath("", "Showing " + formatter.format(pathDisplayLimit) + " paths", "");
			}

			// Next path in array
			counter++;

		}

		// Array with the shortest path nodes created from the shortest path string
		shortestPathNodes = getShortestPathNodes(nodes, shortestPath);

		// If shortest path has to be read from right to left on the graph, reverse it
		if ((numNodes > 1) && (shortestPathNodes[0].getX() > shortestPathNodes[numNodes - 1].getX())) {
			shortestPath = new StringBuilder(reverseString(shortestPath.toString()));
		}

	}

	public void findCoordinates() {
		int numNodes = getNumNodes();
		x = new double[numNodes];
		y = new double[numNodes];
		int index = 0;
		for (Node node : getNodes()) {
			x[index] = node.getX();
			y[index] = node.getY();
			index++;
		}
	}

	public String getShortestPath() {
		return shortestPath.toString();
	}

	public double getShortestPathWeight() {
		return shortestPathWeight;
	}

	public void highlightShortestPath() {
		for (Edge edge : edgeSet) {
			Node firstNode = edge.getFirstNode();
			Node secondNode = edge.getSecondNode();
			for (int i = 0; i < getNumNodes() - 1; i++) {
				if ((firstNode == shortestPathNodes[i] && secondNode == shortestPathNodes[i + 1]) || (secondNode == shortestPathNodes[i] && firstNode == shortestPathNodes[i + 1])) {
					edge.highlight();
					break;
				}
			}
		}
	}

	private Node[] getShortestPathNodes(Node[] nodes, StringBuilder shortestPath) {
		int numNodes = getNumNodes();
		Node[] shortestPathNodes = new Node[numNodes];
		String[] shortestPathNodeIndices = shortestPath.toString().split("-");
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				if (shortestPathNodeIndices[i].equals(nodes[j].getIndex())) {
					shortestPathNodes[i] = nodes[j];
					break;
				}
			}
		}
		return shortestPathNodes;
	}

	private Node getClosestNode(double x, double y) {
		Node node = new Node("", x, y);
		for (Node otherNode : getNodes()) {
			if (areNodesOverlapping(node, otherNode)) {
				return otherNode;
			}
		}
		return null;
	}

	private boolean areNodesOverlapping(Node node, Node otherNode) {
		double distance = Math.sqrt((Math.abs((node.getX() - otherNode.getX())) * Math.abs((node.getX() - otherNode.getX()))) + (Math.abs(node.getY() - otherNode.getY())) * (Math.abs(node.getY() - otherNode.getY())));
		return (distance <= (node.getRadius() * 3));
	}

	private boolean containsEdge(Node firstNode, Node secondNode) {
		for (Edge edge : nodeMap.get(firstNode)) {
			if (nodeMap.get(secondNode).contains(edge)) {
				return true;
			}
		}
		return false;
	}

	private int factorial(int num) {
		if (num == 0) {
			return 1;
		} else {
			return (num * factorial(num - 1));
		}
	}

	private String reverseString(String string) {
		StringBuilder reversedString = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			reversedString.insert(0, string.charAt(i));
		}
		return reversedString.toString();
	}

	public void clear() {
		edgeSet = new LinkedHashSet<>();
		nodeMap = new LinkedHashMap<>();
	}

}
