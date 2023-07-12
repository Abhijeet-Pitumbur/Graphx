package com.abhijeet;

import com.jfoenix.controls.*;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

	// Add VM Options: --add-opens=java.base/java.lang.reflect=com.jfoenix

	private static ArrayList<String> pathIndices;
	private static ArrayList<String> pathList;
	private static ArrayList<String> pathWeights;
	private int minNodes, maxNodes;
	private Graph graph;
	private Pane graphPane;
	private JFXTextField minNodesField;
	private JFXTextField maxNodesField;
	private Label numNodesLabel;
	private Label numEdgesLabel;
	private Label numPathsLabel;
	private Label shortestPathLabel;
	private Label shortestPathWeightLabel;
	private VBox paths;
	private ScrollPane pathsPane;
	private StackPane aboutPane;
	private boolean processing = false;

	// Java first calls the "main" function
	public static void main(String[] args) {
		// Launch JavaFX
		launch();
	}

	// JavaFX then calls the "start" function
	@Override
	public void start(Stage primaryStage) {

		// Set window title
		primaryStage.setTitle("Graphx");

		// Set panes
		BorderPane primaryPane = new BorderPane();
		graphPane = new Pane();
		primaryPane.setCenter(graphPane);
		VBox sidePane = newSidePane();
		primaryPane.setLeft(sidePane);
		aboutPane = new StackPane();
		primaryPane.getChildren().add(aboutPane);
		primaryPane.setId("background");

		// Material design window
		JFXDecorator primaryDecorator = new JFXDecorator(primaryStage, primaryPane, false, true, true);
		primaryDecorator.setTitle("");
		primaryDecorator.setMaximized(true);
		Scene primaryScene = new Scene(primaryDecorator, 1500, 725);

		// CSS stylesheet
		primaryScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

		// Application icon
		primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

		// Press "Esc" to exit
		primaryScene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				exit();
			}
		});

		// Stop all threads before exit
		primaryStage.setOnCloseRequest(event -> exit());

		// Set window contents
		primaryStage.setScene(primaryScene);

		// Run algorithm and show window
		run();
		primaryStage.show();

		// Focus fix
		pathsPane.requestFocus();

	}

	private void run() {

		// Do not run if processing
		if (processing) {
			return;
		}

		// Start processing
		processing = true;

		// Fade out graph
		fadeGraphPane(1, 0);

		// Clear graph if one already exists
		if (graph != null) {
			graph.clear();
			graphPane.getChildren().clear();
		}

		// Get input from text fields
		getInput();

		// Get new complete random graph
		graph = new Graph(minNodes, maxNodes);

		// Add nodes to pane
		for (Node node : graph.getNodes()) {
			graphPane.getChildren().add(node.getCircle());
		}

		// Add edges to pane
		for (Edge edge : graph.getEdges()) {
			graphPane.getChildren().add(edge.getLine());
		}

		// Move nodes to front of pane
		for (Node node : graph.getNodes()) {
			node.toFront();
		}

		// Fade in graph
		fadeGraphPane(0, 1);

		// Show graph info on info pane
		int numNodes = graph.getNumNodes();
		int numEdges = graph.getNumEdges();
		int numPaths = graph.getNumPaths();
		DecimalFormat formatter = new DecimalFormat("###,###,###");
		setNumNodes(formatter.format(numNodes));
		setNumEdges(formatter.format(numEdges));
		setNumPaths(formatter.format(numPaths));

		// Show "Processing..." if there is a lot of paths to process
		if (numPaths > 500) {
			setShortestPath("Processing...", "");
			clearPaths(true);
		} else {
			clearPaths(false);
		}

		// Multiple threads
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				new Thread(() -> {

					// Array with ASCII-encoded paths
					graph.findPaths(0);

					// Array with nodes forming the shortest path
					graph.findShortestPath();

					// Update GUI in JavaFX thread
					Platform.runLater(() -> {

						// Highlight the shortest path on pane
						graph.highlightShortestPath();

						// Show the shortest path on side pane
						setShortestPath(graph.getShortestPath(), formatter.format(graph.getShortestPathWeight()) + " px");

						// Show list of paths on side pane
						setPaths();

						// Stop processing
						processing = false;

					});

				}).start();

			}

		};

		// Delay for fade animation to finish
		Timer timer = new Timer();

		// Run thread in parallel after delay if there is a lot of paths to process
		if (numPaths > 500) {
			timer.schedule(task, 500);
		} else {
			timer.schedule(task, 25);
		}

	}

	private void getInput() {

		String minNodesText = minNodesField.getText();
		String maxNodesText = maxNodesField.getText();
		int lowerBound = 1;
		int upperBound = 10;

		try {

			// Try to get valid integers from text fields
			minNodes = Integer.parseInt(minNodesText);
			maxNodes = Integer.parseInt(maxNodesText);
			if (minNodes < lowerBound || maxNodes < lowerBound || maxNodes > upperBound || minNodes > maxNodes) {
				throw new Exception();
			}

		} catch (Exception exception) {

			// Reset values to boundaries
			minNodes = lowerBound;
			setMinNodes(Integer.toString(minNodes));
			maxNodes = upperBound;
			setMaxNodes(Integer.toString(maxNodes));

		}

	}

	private VBox newSidePane() {

		VBox sidePane = new VBox();
		sidePane.setId("side-pane");

		// Header
		newLabel(sidePane, "Shortest Hamiltonian", "title");
		newLabel(sidePane, "Path in a Complete Graph", "subtitle");

		// Text fields
		newLabel(sidePane, "Random Number of Nodes", "nodes-header");
		minNodesField = newTextField(sidePane, "Minimum Nodes", "4", "min-nodes");
		maxNodesField = newTextField(sidePane, "Maximum Nodes", "8", "max-nodes");

		// "Run" button
		JFXButton runButton = newButton(sidePane, "Run", "run-button", true, true);
		runButton.setOnAction(event -> run());

		// Info pane
		numNodesLabel = newInfoPane(sidePane, "Nodes");
		numEdgesLabel = newInfoPane(sidePane, "Edges");
		numPathsLabel = newInfoPane(sidePane, "Paths");

		// Shortest path
		newLabel(sidePane, "Shortest Hamiltonian Path", "path-header");
		shortestPathLabel = newLabel(sidePane, "Processing...", "shortest-path");
		shortestPathLabel.setMaxWidth(200);
		shortestPathWeightLabel = newLabel(sidePane, "", "shortest-path-weight");

		// List of paths
		newLabel(sidePane, "List of Hamiltonian Paths", "path-header");
		pathsPane = newPathsPane(sidePane);

		// "About" button
		JFXButton aboutButton = newButton(sidePane, "About", "about-button", false, false);
		aboutButton.setOnAction(event -> openAboutPane());

		return sidePane;

	}

	private Label newLabel(Pane pane, String text, String id) {
		Label label = new Label(text);
		label.setId(id);
		pane.getChildren().add(label);
		return label;
	}

	private JFXTextField newTextField(Pane pane, String promptText, String defaultText, String id) {
		JFXTextField textField = new JFXTextField(defaultText);
		textField.setPromptText(promptText);
		textField.setLabelFloat(true);
		textField.setFocusTraversable(false);
		textField.setId(id);
		pane.getChildren().add(textField);
		return textField;
	}

	private JFXButton newButton(Pane pane, String text, String id, boolean defaultButton, boolean focusTraversable) {
		JFXButton button = new JFXButton(text);
		button.setDefaultButton(defaultButton);
		button.setFocusTraversable(focusTraversable);
		button.setId(id);
		pane.getChildren().add(button);
		return button;
	}

	private Label newInfoPane(Pane pane, String text) {
		HBox infoPane = new HBox();
		infoPane.setMaxWidth(200);
		Label headerLabel = newLabel(pane, text, "info-header");
		headerLabel.setMinSize(75, 1);
		Pane spacingPane = new Pane();
		HBox.setHgrow(spacingPane, Priority.ALWAYS);
		spacingPane.setMinSize(10, 1);
		Label numLabel = new Label("0");
		numLabel.setId("info-num");
		infoPane.getChildren().addAll(headerLabel, spacingPane, numLabel);
		pane.getChildren().add(infoPane);
		return numLabel;
	}

	private ScrollPane newPathsPane(Pane pane) {
		pathsPane = new ScrollPane();
		pathsPane.setMaxSize(200, 197);
		pathsPane.setMinSize(200, 197);
		pathsPane.setFocusTraversable(true);
		pane.getChildren().add(pathsPane);
		return pathsPane;
	}

	private void openAboutPane() {

		// Do not open if processing
		if (processing) {
			return;
		}

		// Align to center
		aboutPane.setTranslateX(768);
		aboutPane.setTranslateY(416);
		aboutPane.toFront();

		// Header
		JFXDialogLayout content = new JFXDialogLayout();
		Label label = newAboutLabel("Shortest Hamiltonian Path in a Complete Graph", "about-header", 40);
		content.setHeading(label);
		VBox vBox = new VBox();

		// Paragraphs
		label = newAboutLabel("This algorithm randomly generates a complete graph with a given number of nodes in the range 1 to 10.", "about-text", 50);
		vBox.getChildren().add(label);
		label = newAboutLabel("Hover over a node or an edge to display the node coordinates or the edge weight respectively.", "about-text", 50);
		vBox.getChildren().add(label);
		label = newAboutLabel("The shortest Hamiltonian path is highlighted.", "about-text", 40);
		vBox.getChildren().add(label);

		// Developers
		label = newAboutLabel("""
				Abhijeet Pitumbur
				Deevesh Ramdawor
				Azhar Mamodeally
				Hiranyadaa Omrawoo
				""", "about-names", 110);
		vBox.getChildren().add(label);
		label = newAboutLabel("ADAH © 2022", "about-names", 30);
		vBox.getChildren().add(label);

		// Material design dialog
		content.setBody(vBox);
		JFXDialog aboutDialog = new JFXDialog(aboutPane, content, JFXDialog.DialogTransition.TOP);

		// "Close" button
		JFXButton closeButton = new JFXButton("Close");
		closeButton.setId("about-close-button");
		closeButton.setFocusTraversable(false);

		// Add action to button
		closeButton.setOnAction(event -> {

			aboutDialog.close();

			// Do not allow opening fo another dialog
			processing = false;

		});

		// Add button to dialog
		content.setActions(closeButton);

		// Show dialog
		aboutDialog.show();

		// Do not allow opening fo another dialog
		processing = true;

	}

	private Label newAboutLabel(String text, String id, int minHeight) {
		Label label = new Label(text);
		label.setId(id);
		label.setMinSize(485, minHeight);
		return label;
	}

	private void setMinNodes(String minNodes) {
		minNodesField.setText(minNodes);
	}

	private void setMaxNodes(String maxNodes) {
		maxNodesField.setText(maxNodes);
	}

	private void setNumNodes(String numNodes) {
		numNodesLabel.setText(numNodes);
	}

	private void setNumEdges(String numEdges) {
		numEdgesLabel.setText(numEdges);
	}

	private void setNumPaths(String numPaths) {
		numPathsLabel.setText(numPaths);
	}

	private void setShortestPath(String path, String weight) {
		shortestPathLabel.setText(path);
		shortestPathWeightLabel.setText(weight);
	}

	public static void addPath(String index, String path, String weight) {
		pathIndices.add(index);
		pathList.add(path);
		pathWeights.add(weight);
	}

	private void setPaths() {
		for (int i = 0; i < pathList.size(); i++) {
			if (!pathIndices.get(i).equals("")) {
				newLabel(paths, pathIndices.get(i), "path-list-index");
			}
			if (!pathList.get(i).equals("")) {
				Label pathLabel = newLabel(paths, "  " + pathList.get(i), "path-list-item");
				pathLabel.setMaxWidth(175);
				pathLabel.setWrapText(true);
			}
			if (!pathWeights.get(i).equals("")) {
				newLabel(paths, "    " + pathWeights.get(i), "path-list-item-weight");
			}
		}
		pathsPane.setContent(paths);
	}

	private void clearPaths(boolean showProcessing) {
		paths = new VBox();
		pathIndices = new ArrayList<>();
		pathList = new ArrayList<>();
		pathWeights = new ArrayList<>();
		if (showProcessing) {
			Label pathLabel = new Label("Processing...");
			pathLabel.setId("path-list-item");
			pathLabel.setMaxWidth(175);
			pathLabel.setWrapText(true);
			pathsPane.setContent(pathLabel);
		}
	}

	private void fadeGraphPane(int from, int to) {
		FadeTransition fade = new FadeTransition(Duration.millis(500), graphPane);
		fade.setFromValue(from);
		fade.setToValue(to);
		fade.play();
	}

	private void exit() {
		Platform.exit();
		System.exit(0);
	}

}
