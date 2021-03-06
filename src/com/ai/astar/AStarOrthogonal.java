package com.ai.astar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A Star Algorithm in Orthogonal Directions
 * A modified version of the A Star Algorithm by Marcelo Surriabre that
 * only allows horizontal and vertical movement using one cost and a 
 * penalty cost for changing directions.
 * 
 * A Star selects the path that minimizes
 * f(n)=g(n)+h(n)
 * where
 * n = next node on the path
 * g(n) = cost from the start point to the node
 * h(n) = heuristic cost from node to the end point
 * 
 *
 * @author Marcelo Surriabre
 * @version 2.1, 2017-02-23
 * @author Mark Gottschling on Sept 26, 2020
 * 
 */

public class AStarOrthogonal {
	/*
	 *  cost of movement
	 */
    private static int ORTHOGONAL_COST = 10;
    
    /*
     * penalty cost of changing direction
     */
    private static int DIRECTION_CHANGE_PENALTY = 10;

    /*
     * matrix of nodes used to search for path
     */
    private Node[][] searchArea;
    
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;

    /*
     * the map that is to be traversed
     */
    private boolean[][] map;
    
    /**
     * 
     * @param map
     */
    public AStarOrthogonal(boolean[][] map) {
        setMap(map);
    	setSearchArea(new Node[map.length][map[0].length]);
        setOpenList(new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        }));
        setClosedSet(new HashSet<>());
    }
    
    /**
     * 
     * @return
     */
    public Optional<List<Node>> findPath(Node initialNode, Node finalNode) {
    	// initialize
    	getOpenList().clear();
    	getClosedSet().clear();
    	initNodes(finalNode);
    	setBlocks(getMap());
        
    	// add initial node to the open list
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode, finalNode)) {
                return Optional.of(getPath(currentNode));
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 
     * @param finalNode
     */
    private void initNodes(Node finalNode) {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                Node node = new Node(i, j);
                node.calculateHeuristic(finalNode);
                this.searchArea[i][j] = node;
            }
        }
    }

    /**
     * Takes an input array describing which cells are blocked ex [5, 10] is blocked
     * @param blocksArray
     */
    public void setBlocks(int[][] blocksArray) {
        for (int i = 0; i < blocksArray.length; i++) {
            int row = blocksArray[i][0];
            int col = blocksArray[i][1];
            setBlock(row, col);
        }
    }

    /**
     * Takes an input array describing the entire map that is to be traversed where:
     * 	true = blocked
     * 	false = open
     * @param blocksMatrix
     */
    public void setBlocks(boolean[][] blocksMatrix) {
        for (int row = 0; row < blocksMatrix.length; row++) {
            for (int col = 0; col < blocksMatrix[0].length; col++) {
                if (blocksMatrix[row][col]) {
                    setBlock(row, col);
                }
            }
        }
    }

    /**
     * 
     * @param currentNode
     * @return
     */
    private List<Node> getPath(Node currentNode) {
        List<Node> path = new ArrayList<Node>();
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            currentNode = parent;
        }
        return path;
    }

    /**
     * 
     * @param currentNode
     */
    private void addAdjacentNodes(Node currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }

    private void addAdjacentLowerRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
            Node adjacentNode = getSearchArea()[lowerRow][col];
            adjacentNode.setDirection(Orthogonal.VERTICAL);
            checkNode(currentNode, adjacentNode);
        }
    }

    private void addAdjacentMiddleRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= 0) {
            Node adjacentNode = getSearchArea()[middleRow][col - 1];
            adjacentNode.setDirection(Orthogonal.HORIZONTAL);
            checkNode(currentNode, adjacentNode);
        }
        if (col + 1 < getSearchArea()[0].length) {
            Node adjacentNode = getSearchArea()[middleRow][col + 1];
            adjacentNode.setDirection(Orthogonal.HORIZONTAL);
            checkNode(currentNode, adjacentNode);
        }
    }

    /**
     * 
     * @param currentNode
     */
    private void addAdjacentUpperRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= 0) {
            Node adjacentNode = getSearchArea()[upperRow][col];
            adjacentNode.setDirection(Orthogonal.VERTICAL);
            checkNode(currentNode, adjacentNode);
        }
    }

    private void checkNode(Node currentNode, Node adjacentNode) {
        if (!adjacentNode.isBlock() && !getClosedSet().contains(adjacentNode)) {
            int cost = ORTHOGONAL_COST;
        	if (!getOpenList().contains(adjacentNode)) {
                if (currentNode.getDirection() != Orthogonal.NONE
                    && adjacentNode.getDirection() != currentNode.getDirection()) {
                    cost += DIRECTION_CHANGE_PENALTY;
                }
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                if (currentNode.getDirection() != Orthogonal.NONE
                        && adjacentNode.getDirection() != currentNode.getDirection()) {
                	cost += DIRECTION_CHANGE_PENALTY;
                }
                boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }
    
    private boolean isFinalNode(Node currentNode, Node finalNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.size() == 0;
    }

    private void setBlock(int row, int col) {
        this.searchArea[row][col].setBlock(true);
    }

    private Node[][] getSearchArea() {
        return searchArea;
    }

    private void setSearchArea(Node[][] searchArea) {
        this.searchArea = searchArea;
    }

    private PriorityQueue<Node> getOpenList() {
        return openList;
    }

    private void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    private Set<Node> getClosedSet() {
        return closedSet;
    }

    private void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }

	public boolean[][] getMap() {
		return map;
	}

	public void setMap(boolean[][] map) {
		this.map = map;
	}
}

