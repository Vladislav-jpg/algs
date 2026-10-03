package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GraphDFS {

    public static void main(String[] args) {
        // Матрица смежности
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

        // DFS с вершины 0
        int startNode = 0;
        int[] dfsResult = dfs(adjacencyMatrix, startNode);

        System.out.println("Массив, содержащий номера вершин DFS-пути от вершины " + startNode + ":");
        System.out.println(Arrays.toString(dfsResult));
    }

    /**
     * Итеративный метод для запуска DFS с использованием Stack.
     * Возвращает массив с номерами вершин в порядке обхода.
     */
    public static int[] dfs(int[][] matrix, int startNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        // стартовую вершину в стек
        stack.push(startNode);

        while (!stack.isEmpty()) {
            // берем верхнюю вершину из стека
            int currentNode = stack.pop();

            // Проверка были ли в вершине
            if (!visited[currentNode]) {
                visited[currentNode] = true;
                path.add(currentNode);

                // Добавляем соседей в стек в ОБРАТНОМ порядке.
                // Стек работает по принципу LIFO (последним пришел - первым ушел).
                // Чтобы первым обработался сосед с меньшим индексом, он должен попасть в стек последним.
                for (int neighbor = n - 1; neighbor >= 0; neighbor--) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        // Перевод списка в массив
        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }
}