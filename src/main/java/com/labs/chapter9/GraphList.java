package com.labs.chapter9;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphList {
    public static void main(String[] args) {
        int n = 4;
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        graph.get(0).add(1);
        graph.get(0).add(3);

        graph.get(1).add(0);
        graph.get(1).add(2);

        graph.get(2).add(1);
        graph.get(2).add(3);

        graph.get(3).add(0);
        graph.get(3).add(2);

        System.out.println("1. Наличие пути (0-2): " + hasSimplePath(graph, 0, 2));
        System.out.println("2. Вершины пути (0-2): " + findSimplePath(graph, 0, 2));
        System.out.println("3. Наличие эйлерова цикла: " + hasEulerianCycle(graph));
        System.out.println("4. Эйлеров цикл: " + findEulerianCycle(graph, 0));
        System.out.println("5. Наличие Гамильтонова пути: " + hasHamiltonianPath(graph, 0, 3, new boolean[n], 0));
    }

    public static boolean hasSimplePath(ArrayList<ArrayList<Integer>> graph, int startNode, int targetNode) {
        int n = graph.size();
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

                ArrayList<Integer> neighbors = graph.get(currentNode);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    int neighbor = neighbors.get(i);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }
        return false;
    }

    // 2. Поиск маршрута простого пути
    public static List<Integer> findSimplePath(ArrayList<ArrayList<Integer>> graph, int startNode, int targetNode) {
        int n = graph.size();
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

                ArrayList<Integer> neighbors = graph.get(currentNode);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    int neighbor = neighbors.get(i);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                        if (parent[neighbor] == -1) {
                            parent[neighbor] = currentNode;
                        }
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public static boolean hasEulerianCycle(ArrayList<ArrayList<Integer>> graph) {
        for (int i = 0; i < graph.size(); i++) {
            if (graph.get(i).size() % 2 != 0) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Integer> findEulerianCycle(ArrayList<ArrayList<Integer>> graph, int startNode) {
        ArrayList<Integer> cycle = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        ArrayList<ArrayList<Integer>> graphCopy = new ArrayList<>();
        for (int i = 0; i < graph.size(); i++) {
            graphCopy.add(new ArrayList<>(graph.get(i)));
        }

        stack.push(startNode);

        while (!stack.isEmpty()) {
            int currentVertex = stack.peek();
            if (!graphCopy.get(currentVertex).isEmpty()) {
                int nextVertex = graphCopy.get(currentVertex).get(0);

                stack.push(nextVertex);

                graphCopy.get(currentVertex).remove((Integer) nextVertex);
                graphCopy.get(nextVertex).remove((Integer) currentVertex);
            }
            else {
                cycle.add(stack.pop());
            }
        }
        return cycle;
    }

    public static boolean hasHamiltonianPath(ArrayList<ArrayList<Integer>> graph, int currentVertex, int targetVertex, boolean[] visitedVertices, int visitedCount) {
        visitedVertices[currentVertex] = true;
        visitedCount++;

        if (visitedCount == graph.size() && currentVertex == targetVertex) {
            return true;
        }

        for (int nextVertex : graph.get(currentVertex)) {
            if (!visitedVertices[nextVertex]) {
                if (hasHamiltonianPath(graph, nextVertex, targetVertex, visitedVertices, visitedCount)) {
                    return true;
                }
            }
        }

        visitedVertices[currentVertex] = false;
        return false;
    }
}