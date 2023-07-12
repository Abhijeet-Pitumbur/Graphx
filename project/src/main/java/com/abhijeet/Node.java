package com.abhijeet;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Node {

	private final String index;
	private final Circle circle;
	private final StackPane pane;
	private final int radius = 20;

	public Node(String index, double x, double y) {

		// Set index
		this.index = index;
		Label indexLabel = new Label(index);
		indexLabel.setId("node-label");

		// Draw circle at coordinates
		circle = new Circle(x, y, radius);
		circle.setStrokeWidth(4);
		circle.setFill(Color.web("#1D35B4"));
		circle.setStroke(Color.web("#FFFFFF"));

		// Add index on circle
		pane = new StackPane();
		pane.getChildren().addAll(circle, indexLabel);
		pane.setTranslateX(x - radius);
		pane.setTranslateY(y - radius);
		pane.toFront();

		// Hover to display coordinates
		Tooltip tooltip = new Tooltip(this.toString());
		tooltip.setShowDelay(Duration.seconds(0.25));
		Tooltip.install(pane, tooltip);

	}

	public String getIndex() {
		// Alphabetic index
		return index;
	}

	public double getX() {
		// X-coordinate
		return circle.getCenterX();
	}

	public double getY() {
		// Y-coordinate
		return circle.getCenterY();
	}

	public StackPane getCircle() {
		return pane;
	}

	public int getRadius() {
		return radius;
	}

	public void toFront() {
		// Move to front of the Z-axis
		pane.toFront();
	}

	@Override
	public String toString() {
		// Display when hovering over node
		return "Node " + getIndex() + " (" + (int) circle.getCenterX() + ", " + (int) circle.getCenterY() + ")";
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
		if (!(object instanceof Node other)) {
			return false;
		}

		// Two nodes are the same object if they have the same coordinates
		return ((getX() == other.getX()) && (getY() == other.getY()));

	}

	@Override
	public int hashCode() {
		// Instance ID
		return (int) (getX() + getY());
	}

}
