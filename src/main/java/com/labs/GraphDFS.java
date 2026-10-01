package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphDFS {

    public static void main(String[] args) {
        // Матрица смежности для графа из задания
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

        // Вызов обхода в глубину (DFS) начиная с вершины 0
        int startNode = 0;
        int[] dfsResult = dfs(adjacencyMatrix, startNode);

        System.out.println("Массив, содержащий номера вершин DFS-пути от вершины " + startNode + ":");
        System.out.println(Arrays.toString(dfsResult));
    }

    /**
     * Основной метод для запуска DFS.
     * Возвращает массив с номерами вершин в порядке обхода.
     */
    public static int[] dfs(int[][] matrix, int startNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        // Запускаем рекурсивный обход
        dfsRecursive(matrix, startNode, visited, path);

        // Преобразуем список в массив
        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }

    /**
     * Вспомогательный рекурсивный метод для обхода в глубину
     */
    private static void dfsRecursive(int[][] matrix, int currentNode, boolean[] visited, List<Integer> path) {
        // Отмечаем текущую вершину как посещенную и добавляем в путь
        visited[currentNode] = true;
        path.add(currentNode);

        // Проверяем всех соседей текущей вершины
        for (int neighbor = 0; neighbor < matrix.length; neighbor++) {
            // Если есть ребро и сосед еще не посещен, идем в него
            if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                dfsRecursive(matrix, neighbor, visited, path);
            }
        }
    }


}