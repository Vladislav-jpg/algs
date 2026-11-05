package com.labs;

import java.util.*;

public class GraphTasks {

    public static void main(String[] args) {
        int[][] adjacencyMatrix = {
                {0, 0, 0, 0, 0, 1, 0, 0, 1, 0}, // 0
                {0, 0, 1, 1, 0, 1, 1, 1, 0, 0}, // 1
                {0, 1, 0, 1, 0, 0, 0, 0, 1, 0}, // 2
                {0, 1, 1, 0, 0, 1, 0, 0, 1, 0}, // 3
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 1}, // 4
                {1, 1, 0, 1, 0, 0, 1, 0, 0, 1}, // 5
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0}, // 6
                {0, 1, 0, 0, 1, 0, 0, 0, 0, 0}, // 7
                {1, 0, 1, 1, 0, 0, 0, 0, 0, 0}, // 8
                {0, 0, 0, 0, 1, 1, 0, 0, 0, 0}  // 9
        };

        System.out.println("-------------------------------------------------");

        calculateDegreesAndEdges(adjacencyMatrix);

        System.out.println("-------------------------------------------------");

        int startNode = 0;
        int[] bfsResult = bfs(adjacencyMatrix, startNode);

        System.out.println("Массив, содержащий номера вершин BFS-пути от вершины " + startNode + ":");
        System.out.println(Arrays.toString(bfsResult));

        int[] dfsResult = dfs(adjacencyMatrix, startNode);

        System.out.println("Массив, содержащий номера вершин DFS-пути от вершины " + startNode + ":");
        System.out.println(Arrays.toString(dfsResult));
    }


    public static void calculateDegreesAndEdges(int[][] matrix) {
        int n = matrix.length;
        int sumOfDegrees = 0;

        System.out.println("Степени вершин:");
        for (int i = 0; i < n; i++) {
            int degree = 0;
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    degree++;
                }
            }
            System.out.println("Вершина " + i + ": " + degree);
            sumOfDegrees += degree;
        }

        System.out.println("Общая сумма степеней вершин: " + sumOfDegrees);
        int edgesCount = sumOfDegrees / 2;
        System.out.println("Общее число рёбер в графе: " + edgesCount);
    }

    public static int[] bfs(int[][] matrix, int startNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> path = new ArrayList<>();

        visited[startNode] = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            path.add(currentNode);

            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }

        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }

    public static int[] dfs(int[][] matrix, int startNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        List<Integer> path = new ArrayList<>();

        //visited[startNode] = true;
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();

            if (!visited[currentNode]) {
                visited[currentNode] = true;
                path.add(currentNode);
                for (int neighbor = n - 1 ; neighbor >= 0; neighbor --) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }


        }

        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }

    
    
    
    
}
