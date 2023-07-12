# Graphx

Graphx is an application that finds the shortest Hamiltonian path in a random complete graph.

When the 'Run' button is clicked, the program:

- Generates a random number in the given inclusive range to use as the number of nodes.
- Generates that number of nodes, each node having random coordinates.
- Completes the graph by connecting every pair of distinct nodes with one unique edge.
- Finds the list of all Hamiltonian paths in the complete graph generated.
- Finds the shortest Hamiltonian path in that list.
- Displays the list and highlights the shortest Hamiltonian path on the graph.

<p align="center"><img src="https://github.com/Abhijeet-Pitumbur/Graphx/blob/main/project/demo.gif"/></p>

##### [View PDF Report](https://bit.ly/abhijt-graphx-report)  · Google Drive
##### [Download Repository](https://github.com/Abhijeet-Pitumbur/Graphx/archive/refs/heads/main.zip)  · GitHub

## Installation Instructions
- Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).
- Open the project folder in IntelliJ IDEA.
- Go to *Run > Edit Configurations > Application > Modify Options > Add VM Options*.
- Add this VM option:
```
--add-opens=java.base/java.lang.reflect=com.jfoenix
```

## Languages, Frameworks and Tools
- Java 18.0
- JavaFX 18.0
- JFoenix 9.0
- IntelliJ Ultimate IDEA 2022.2

## Credits
- Abhijeet Pitumbur
- Azhar Mamodeally
- Deevesh Ramdawor
- Hiranyadaa Omrawoo
