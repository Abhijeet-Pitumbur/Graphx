package com.abhijeet;

import javafx.animation.StrokeTransition;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Edge {

	private final Line line;
	private final Node firstNode;
	private final Node secondNode;
	private final double weight;

	public Edge(Node firstNode, Node secondNode) {

		// Set nodes
		this.firstNode = firstNode;
		this.secondNode = secondNode;

		// Draw line between the two nodes
		line = new Line(firstNode.getX(), firstNode.getY(), secondNode.getX(), secondNode.getY());
		line.setStrokeWidth(3);
		line.setStroke(Color.web("#E0E0E0"));

		// Set weight using Pythagoras' theorem
		double x1 = firstNode.getX();
		double y1 = firstNode.getY();
		double x2 = secondNode.getX();
		double y2 = secondNode.getY();
		weight = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

		// Hover to display weight
		Tooltip tooltip = new Tooltip(this.toString());
		tooltip.setShowDelay(Duration.seconds(0.25));
		Tooltip.install(line, tooltip);

	}

	public Node getFirstNode() {
		return firstNode;
	}

	public Node getSecondNode() {
		return secondNode;
	}

	public Line getLine() {
		return line;
	}

	public void highlight() {

		// Multiple threads
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				// Update GUI in JavaFX thread
				new Thread(() -> Platform.runLater(() -> {

					// Fade in the shortest path
					StrokeTransition fade = new StrokeTransition(Duration.millis(500), line, Color.web("#E0E0E0"), Color.web("#1D35B4"));
					fade.play();

					// Increase line width
					line.setStrokeWidth(5);

					// Move to front of the Z-axis
					line.toFront();
					getFirstNode().toFront();
					getSecondNode().toFront();

				})).start();

			}

		};

		// Delay for fade animation to finish
		Timer timer = new Timer();

		// Run thread in parallel after delay
		timer.schedule(task, 25);

	}

	@Override
	public String toString() {

		Node firstNode = getFirstNode();
		Node secondNode = getSecondNode();
		String firstNodeIndex;
		String secondNodeIndex;

		// If edge has to be read from right to left on the graph, reverse it
		if (firstNode.getX() < secondNode.getX()) {
			firstNodeIndex = firstNode.getIndex();
			secondNodeIndex = secondNode.getIndex();
		} else {
			firstNodeIndex = secondNode.getIndex();
			secondNodeIndex = firstNode.getIndex();
		}
		DecimalFormat formatter = new DecimalFormat("###,###");

		// Display when hovering over edge
		return "Edge " + firstNodeIndex + "-" + secondNodeIndex + " (" + formatter.format(weight) + " px)";

	}

	@Override
	public boolean equals(Object object) {

		if (object == null) {
			return false;
		}

		// Check object equality
		if (this == object) {
			return true;
		}
		if (!(object instanceof Edge otherEdge)) {
			return false;
		}

		// Get coordinates
		int x1 = (int) getLine().getStartX();
		int y1 = (int) getLine().getStartY();
		int x2 = (int) getLine().getEndX();
		int y2 = (int) getLine().getEndY();
		int otherX1 = (int) otherEdge.getLine().getStartX();
		int otherY1 = (int) otherEdge.getLine().getStartY();
		int otherX2 = (int) otherEdge.getLine().getEndX();
		int otherY2 = (int) otherEdge.getLine().getEndY();

		// Two edges are the same object if their nodes have the same coordinates
		if ((x1 == otherX1) && (y1 == otherY1) && (x2 == otherX2) && (y2 == otherY2)) {
			return true;
		} else {
			return (x1 == otherX2) && (y1 == otherY2) && (x2 == otherX1) && (y2 == otherY1);
		}

	}

	@Override
	public int hashCode() {
		// Instance ID
		return (getFirstNode().hashCode() + getSecondNode().hashCode());
	}

}
