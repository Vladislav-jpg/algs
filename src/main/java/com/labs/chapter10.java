package com.labs;

import java.util.*;

public class chapter10 {

    public static void main(String[] args) {
        // --- Задача 1: Простой Дейкстра ---
        System.out.println("=== 1. Алгоритм Дейкстры (только расстояния) ===");
        int[][] graph1 = {
                {0, 4, 2, 0},
                {0, 0, 1, 5},
                {0, 0, 0, 8},
                {0, 0, 0, 0}
        };
        int[] d1 = dijkstraDistances(graph1, 0);
        System.out.println("Кратчайшие расстояния от вершины 0: " + Arrays.toString(d1));

        // --- Задача 2: Дейкстра с валидацией и восстановлением путей ---
        System.out.println("\n=== 2. Дейкстра с проверкой весов и путями ===");
        int[][] graph2 = {
                {0, 7, 9, 0, 0, 14},
                {7, 0, 10, 15, 0, 0},
                {9, 10, 0, 11, 0, 2},
                {0, 15, 11, 0, 6, 0},
                {0, 0, 0, 6, 0, 9},
                {14, 0, 2, 0, 9, 0}
        };
        dijkstraWithPaths(graph2, 0);

        // --- Задача 4: Проверка ацикличности (DAG) ---
        System.out.println("\n=== 4. Проверка ацикличности графа ===");
        int[][] dag = {
                {0, 3, 2, 0},
                {0, 0, 0, 4},
                {0, 0, 0, 1},
                {0, 0, 0, 0}
        };
        int[][] cyclicGraph = {
                {0, 1, 0},
                {0, 0, 1},
                {1, 0, 0}
        };
        System.out.println("Граф 1 ацикличен? " + isAcyclic(dag));
        System.out.println("Граф 2 ацикличен? " + isAcyclic(cyclicGraph));

        // --- Задача 3: Кратчайший путь в DAG (топологическая сортировка) ---
        System.out.println("\n=== 3. Кратчайший путь в DAG (от 0 до 3) ===");
        shortestPathInDag(dag, 0, 3);

        // --- Задача 5: Алгоритм Флойда-Уоршелла ---
        System.out.println("\n=== 5. Алгоритм Флойда-Уоршелла ===");
        int[][] graphFloyd = {
                {0, 5, 0, 10},
                {0, 0, 3, 0},
                {0, 0, 0, 1},
                {0, 0, 0, 0}
        };
        floydWarshall(graphFloyd);
    }

    /**
     * 1. Расчёт расстояний кратчайших путей по алгоритму Дейкстры.
     * graph[u][v] > 0 означает наличие ребра с весом; 0 — отсутствие ребра (при u != v).
     */
    public static int[] dijkstraDistances(int[][] graph, int start) {
        int n = graph.length;
        int[] dist = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            int minVal = Integer.MAX_VALUE;

            // Поиск не посещенной вершины с минимальным текущим расстоянием
            for (int v = 0; v < n; v++) {
                if (!visited[v] && dist[v] < minVal) {
                    minVal = dist[v];
                    u = v;
                }
            }

            if (u == -1 || dist[u] == Integer.MAX_VALUE) {
                break;
            }

            visited[u] = true;

            // Релаксация смежных ребер
            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[u][v] > 0 && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }

        return dist;
    }

    /**
     * 2. Дейкстра с валидацией (неотрицательность весов) и восстановлением путей.
     */
    public static void dijkstraWithPaths(int[][] graph, int start) {
        int n = graph.length;

        // Проверка применимости алгоритма (отсутствие отрицательных ребер)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] < 0) {
                    System.out.println("Ошибка: граф содержит ребро с отрицательным весом (" + i + " -> " + j + ": " + graph[i][j] + "). Алгоритм Дейкстры неприменим.");
                    return;
                }
            }
        }

        int[] dist = new int[n];
        boolean[] visited = new boolean[n];
        int[] prev = new int[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            int minVal = Integer.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && dist[v] < minVal) {
                    minVal = dist[v];
                    u = v;
                }
            }

            if (u == -1 || dist[u] == Integer.MAX_VALUE) {
                break;
            }

            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[u][v] > 0) {
                    if (dist[u] + graph[u][v] < dist[v]) {
                        dist[v] = dist[u] + graph[u][v];
                        prev[v] = u;
                    }
                }
            }
        }

        // Вывод результатов и восстановление путей
        for (int target = 0; target < n; target++) {
            if (dist[target] == Integer.MAX_VALUE) {
                System.out.println("До вершины " + target + ": пути нет");
            } else {
                List<Integer> path = new ArrayList<>();
                for (int at = target; at != -1; at = prev[at]) {
                    path.add(at);
                }
                Collections.reverse(path);
                System.out.println("До вершины " + target + " (длина " + dist[target] + "): " + path);
            }
        }
    }

    /**
     * 3. Кратчайший путь между двумя вершинами ациклического ориентированного графа (DAG).
     * Работает за O(V + E) с помощью топологической сортировки (алгоритм Кана).
     */
    public static void shortestPathInDag(int[][] graph, int start, int finish) {
        int n = graph.length;
        int[] inDegree = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] != 0 && i != j) {
                    inDegree[j]++;
                }
            }
        }

        // Топологическая сортировка через очередь
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && u != v) {
                    inDegree[v]--;
                    if (inDegree[v] == 0) {
                        queue.add(v);
                    }
                }
            }
        }

        if (topoOrder.size() < n) {
            System.out.println("Ошибка: граф содержит цикл, метод применим только для DAG.");
            return;
        }

        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[start] = 0;

        // Релаксация ребер в топологическом порядке
        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (int v = 0; v < n; v++) {
                    if (graph[u][v] != 0 && u != v) {
                        if (dist[u] + graph[u][v] < dist[v]) {
                            dist[v] = dist[u] + graph[u][v];
                            prev[v] = u;
                        }
                    }
                }
            }
        }

        if (dist[finish] == Integer.MAX_VALUE) {
            System.out.println("Пути между " + start + " и " + finish + " не существует.");
        } else {
            List<Integer> path = new ArrayList<>();
            for (int at = finish; at != -1; at = prev[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            System.out.println("Кратчайший путь от " + start + " до " + finish + ": " + path + " (длина = " + dist[finish] + ")");
        }
    }

    /**
     * 4. Проверка ацикличности ориентированного графа (поиск циклов через степени входа).
     * Возвращает true, если граф ацикличен.
     */
    public static boolean isAcyclic(int[][] graph) {
        int n = graph.length;
        int[] inDegree = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] != 0 && i != j) {
                    inDegree[j]++;
                }
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        int visitedCount = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            visitedCount++;
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && u != v) {
                    inDegree[v]--;
                    if (inDegree[v] == 0) {
                        queue.add(v);
                    }
                }
            }
        }

        return visitedCount == n;
    }

    /**
     * 5. Алгоритм Флойда-Уоршелла для поиска кратчайших путей между всеми парами вершин.
     */
    public static void floydWarshall(int[][] graph) {
        int n = graph.length;
        int INF = 1_000_000_000;
        int[][] dist = new int[n][n];

        // Инициализация матрицы расстояний
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else if (graph[i][j] != 0) {
                    dist[i][j] = graph[i][j];
                } else {
                    dist[i][j] = INF;
                }
            }
        }

        // Основной трехкратный цикл алгоритма Флойда-Уоршелла
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }

        // Вывод матрицы кратчайших расстояний
        System.out.println("Матрица кратчайших расстояний:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][j] >= INF) {
                    System.out.printf("%6s", "INF");
                } else {
                    System.out.printf("%6d", dist[i][j]);
                }
            }
            System.out.println();
        }
    }
}