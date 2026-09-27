package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GraphTasks {

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

        // 1. Вывод матрицы смежности
        printMatrix(adjacencyMatrix);

        System.out.println("-------------------------------------------------");

        // 2. Вызов функции подсчета степеней и ребер
        calculateDegreesAndEdges(adjacencyMatrix);

        System.out.println("-------------------------------------------------");

        // 3. Вызов обхода в ширину (BFS) начиная с вершины 0
        int startNode = 0;
        int[] bfsResult = bfs(adjacencyMatrix, startNode);

        System.out.println("Массив, содержащий номера вершин BFS-пути от вершины " + startNode + ":");
        System.out.println(Arrays.toString(bfsResult));
    }

    /**
     * Функция для наглядного вывода матрицы смежности
     */
    public static void printMatrix(int[][] matrix) {
        System.out.println("Матрица смежности:");

        // Вывод номеров столбцов (вершин)
        System.out.print("   ");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n  --------------------");

        // Вывод строк матрицы с номерами вершин
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(i + "| ");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Функция, подсчитывающая число степеней и число рёбер графа
     */
    public static void calculateDegreesAndEdges(int[][] matrix) {
        int n = matrix.length;
        int sumOfDegrees = 0;

        System.out.println("Степени вершин:");
        for (int i = 0; i < n; i++) {
            int degree = 0;
            // Считаем количество единиц в строке для получения степени вершины
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    degree++;
                }
            }
            System.out.println("Вершина " + i + ": " + degree);
            sumOfDegrees += degree;
        }

        // В неориентированном графе количество ребер равно половине суммы степеней всех вершин (Лемма о рукопожатиях)
        System.out.println("Общая сумма степеней вершин: " + sumOfDegrees);
        int edgesCount = sumOfDegrees / 2;
        System.out.println("Общее число рёбер в графе: " + edgesCount);
    }

    /**
     * Программа обхода в ширину (BFS)
     * В результате формируется массив, содержащий номера вершин BFS-пути.
     */
    public static int[] bfs(int[][] matrix, int startNode) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> path = new ArrayList<>();

        // Начинаем с заданной стартовой вершины
        visited[startNode] = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            path.add(currentNode); // Добавляем вершину в путь обхода

            // Проверяем всех соседей текущей вершины
            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                    visited[neighbor] = true; // Отмечаем как посещенную
                    queue.add(neighbor);      // Добавляем в очередь
                }
            }
        }

        // Преобразуем полученный список (List) в требуемый массив (int[])
        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }
}