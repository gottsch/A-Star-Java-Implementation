package com.ai.astar;

import java.util.List;

public class AStarTest {

    public static void main(String[] args) {
        Node initialNode = new Node(2, 1);
        Node finalNode = new Node(2, 5);
        int rows = 6;
        int cols = 7;

        // original test array
        int[][] blocksArray = new int[][]{{1, 3}, {2, 3}, {3, 3}};
        
        boolean[][] map = new boolean[rows][cols];
        map[1][3] = true;
        map[2][3] = true;
        map[3][3] = true;

        System.out.println("=== A Star Orthongonal ===========");
        System.out.println("=== (2,1) -> (2, 5)  ===========");
        AStarOrthogonal aStarO = new AStarOrthogonal(map);
        List<Node> pathO = aStarO.findPath(initialNode, finalNode);
        for (Node node : pathO) {
            System.out.println(node);
        }
        System.out.println("=== (1,1) -> (2, 5)  ===========");
        List<Node> pathO2 = aStarO.findPath(new Node(2, 0), finalNode);
        for (Node node : pathO2) {
            System.out.println(node);
        }
        // search Area
        //      0   1   2   3   4   5   6
        // 0    -   -   -   -   -   -   -
        // 1    -   -   -   B   -   -   -
        // 2    -   I   -   B   -   F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -

        // expected output with diagonals
        //Node [row=2, col=1]
        //Node [row=1, col=2]
        //Node [row=0, col=3]
        //Node [row=1, col=4]
        //Node [row=2, col=5]

        //Search Path with diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   -   *   -   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  -   B   -  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -

        //Expected output without diagonals
        //Node [row=2, col=1]
        //Node [row=2, col=2]
        //Node [row=1, col=2]
        //Node [row=0, col=2]
        //Node [row=0, col=3]
        //Node [row=0, col=4]
        //Node [row=1, col=4]
        //Node [row=2, col=4]
        //Node [row=2, col=5]

        //Search Path without diagonals
        //      0   1   2   3   4   5   6
        // 0    -   -   *   *   *   -   -
        // 1    -   -   *   B   *   -   -
        // 2    -   I*  *   B   *  *F   -
        // 3    -   -   -   B   -   -   -
        // 4    -   -   -   -   -   -   -
        // 5    -   -   -   -   -   -   -
    }
}
