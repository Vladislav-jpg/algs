package com.labs.chapter9;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphMatrix {
    public static void main(String[] args) {
        int[][] graph = {
                {0, 1, 0, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 0, 1, 0}
        };

        System.out.println("1. Наличие пути (0 -> 2): " + hasSimplePath(graph, 0, 2));

        System.out.println("2. Вершины пути (0 -> 2): " + findSimplePath(graph, 0, 2));

        System.out.println("3. Наличие эйлерова цикла: " + hasEulerianCycle(graph));

        System.out.println("4. Эйлеров цикл: " + findEulerianCycle(graph, 0));

        System.out.println("5. Наличие Гамильтонова пути: " + hasHamiltonianPath(graph, 0, 3, new boolean[graph.length], 0));
    }



    public static boolean hasSimplePath(int[][] matrix, int startNode, int targetNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);
        while (!stack.isEmpty()) {
            int currentNode = stack.pop();
            if (currentNode == targetNode) {
                return true;
            }
            if (!visited[currentNode]) {
                visited[currentNode] = true;
                for (int neighbor = n - 1; neighbor >= 0; neighbor--) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return false;
    }

    public static List<Integer> findSimplePath(int[][] matrix, int startNode, int targetNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }

        stack.push(startNode);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();
            if (currentNode == targetNode) {
                List<Integer> path = new ArrayList<>();
                int curr = targetNode;
                while (curr != -1) {
                    path.add(0, curr);
                    curr = parent[curr];
                }
                return path;
            }

            if (!visited[currentNode]) {
                visited[currentNode] = true;
                for (int neighbor = n - 1; neighbor >= 0; neighbor--) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);

                        // Записываем, что в соседа мы пришли из currentNode
                        // Делаем это только если у соседа еще нет родителя
                        if (parent[neighbor] == -1) {
                            parent[neighbor] = currentNode;
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }
    //каждая степень вершины четная(по каждому ребру один раз должен проходить Эйлеров Цикл)
    public static boolean hasEulerianCycle(int[][] graph) {
        int n = graph.length;
        for (int i = 0; i < n; i++) {
            int degree = 0;
            for (int j = 0; j < n; j++) {
                if (graph[i][j] > 0) degree++;
            }
            if (degree % 2 != 0) return false; 
        }
        return true; 
    }
    
    public static ArrayList<Integer> findEulerianCycle(int[][] graph, int startNode) {
        ArrayList<Integer> cycle = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        //копия графа
        int n = graph.length;
        int[][] graphCopy = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                graphCopy[i][j] = graph[i][j];
        
        stack.push(startNode);
        while (!stack.isEmpty()) {
            int currentVertex = stack.peek();
            int nextVertex = -1;
            
            for (int i = 0; i < n; i++) {
                if (graphCopy[currentVertex][i] > 0) {
                    nextVertex = i;
                    break;
                }
            }
            if (nextVertex != -1) {
                stack.push(nextVertex);
                graphCopy[currentVertex][nextVertex]--;
                graphCopy[nextVertex][currentVertex]--;
            } else {
                cycle.add(stack.pop());
            }
        }
        return cycle; 
    }
    //каждая вершина один раз
    public static boolean hasHamiltonianPath(int[][] adjacencyMatrix, int currentVertex, int targetVertex, boolean[] visitedVertices, int visitedCount) {
        visitedVertices[currentVertex] = true;
        visitedCount++;

        if (visitedCount == adjacencyMatrix.length && currentVertex == targetVertex) {
            return true;
        }

        for (int nextVertex = 0; nextVertex < adjacencyMatrix.length; nextVertex++) {
            if (adjacencyMatrix[currentVertex][nextVertex] > 0 && !visitedVertices[nextVertex]) {
                if (hasHamiltonianPath(adjacencyMatrix, nextVertex, targetVertex, visitedVertices, visitedCount)) {
                    return true;
                }
            }
        }
        visitedVertices[currentVertex] = false;
        return false;
    }
}
