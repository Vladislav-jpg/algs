package com.labs;
import java.util.*; // Импортируем утилиты для списков и массивов

public class P10{

    static final int INF = 1_000_000_000; // Бесконечность (10^9, чтобы не было переполнения при INF + INF)

    // ================= ЗАДАЧА 1: Расчет расстояний по алгоритму Дейкстры =================
    public static int[] dijkstraSimple(int[][] matrix, int start) {
        int n = matrix.length; // Количество вершин в графе
        int[] dist = new int[n]; // Массив кратчайших расстояний от start до остальных вершин
        boolean[] visited = new boolean[n]; // Массив уже обработанных (зафиксированных) вершин

        Arrays.fill(dist, INF); // Заполняем все расстояния бесконечностью
        dist[start] = 0; // Расстояние от стартовой вершины до самой себя равно 0

        for (int step = 0; step < n; step++) { // Цикл на N итераций (по одной на каждую вершину)
            int u = -1; // Индекс вершины с минимальным расстоянием
            for (int i = 0; i < n; i++) { // Ищем непосещенную вершину с наименьшим dist[i]
                if (!visited[i] && (u == -1 || dist[i] < dist[u])) {
                    u = i; // Запоминаем индекс лучшей вершины
                }
            }

            if (u == -1 || dist[u] == INF) break; // Если оставшиеся вершины недостижимы — выходим
            visited[u] = true; // Помечаем вершину u как обработанную

            // Релаксация (ослабление) всех ребер, исходящих из вершины u
            for (int v = 0; v < n; v++) {
                if (matrix[u][v] > 0 && !visited[v]) { // Если есть ребро u -> v и вершина v не посещена
                    if (dist[u] + matrix[u][v] < dist[v]) { // Если путь через u короче текущего пути до v
                        dist[v] = dist[u] + matrix[u][v]; // Обновляем кратчайшее расстояние до v
                    }
                }
            }
        }
        return dist; // Возвращаем массив кратчайших расстояний
    }

    // ================= ЗАДАЧА 2: Дейкстра с проверкой применимости и сохранением путей =================
    public static int[] dijkstraWithPaths(int[][] matrix, int start, int[] parent) {
        int n = matrix.length; // Число вершин

        // 1. Проверка применимости алгоритма Дейкстры (absence of negative weight edges)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] < 0) { // Если обнаружено ребро с отрицательным весом
                    throw new IllegalArgumentException("Алгоритм Дейкстры неприменим: в графе есть отрицательные веса!");
                }
            }
        }

        int[] dist = new int[n]; // Массив расстояний
        boolean[] visited = new boolean[n]; // Массив посещенных вершин
        Arrays.fill(dist, INF); // Инициализируем расстояния бесконечностью
        Arrays.fill(parent, -1); // Инициализируем массив предков значением -1 (нет предка)
        dist[start] = 0; // До старта расстояние 0

        for (int step = 0; step < n; step++) { // Основной цикл алгоритма
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && (u == -1 || dist[i] < dist[u])) u = i; // Выбор минимума
            }
            if (u == -1 || dist[u] == INF) break; // Если до остальных вершин не добраться
            visited[u] = true; // Фиксируем вершину u

            for (int v = 0; v < n; v++) { // Просматриваем соседей
                if (matrix[u][v] > 0 && !visited[v]) {
                    if (dist[u] + matrix[u][v] < dist[v]) { // Если нашли более выгодный путь
                        dist[v] = dist[u] + matrix[u][v]; // Обновляем расстояние
                        parent[v] = u; // СОХРАНЕНИЕ ПУТИ: записываем, что в вершину v мы пришли из u
                    }
                }
            }
        }
        return dist; // Возвращаем расстояния (при этом массив parent заполнен для восстановления маршрутов)
    }

    // Вспомогательный метод восстановления маршрута из массива предков parent (к Задаче 2)
    public static List<Integer> reconstructPath(int target, int[] parent) {
        List<Integer> path = new ArrayList<>(); // Список для вершин кратчайшего пути
        for (int v = target; v != -1; v = parent[v]) { // Шагаем от целевой вершины назад по предкам до -1
            path.add(v); // Добавляем вершину в список
        }
        Collections.reverse(path); // Разворачиваем список, чтобы маршрут шел от старта к цели
        return path; // Возвращаем готовый маршрут
    }

    // ================= ЗАДАЧА 4: Проверка ацикличности графа =================
    public static boolean isAcyclic(int[][] matrix) {
        int n = matrix.length; // Количество вершин
        int[] color = new int[n]; // Цвета вершин: 0 - белая (не были), 1 - серая (в стеке), 2 - черная (обработана)

        for (int i = 0; i < n; i++) { // Проверяем все компоненты графа
            if (color[i] == 0) { // Если вершина еще белая
                if (hasCycleDFS(matrix, i, color)) { // Запускаем DFS поиска цикла
                    return false; // Если цикл найден — граф НЕ является ациклическим
                }
            }
        }
        return true; // Циклов нет — граф ациклический (DAG)
    }

    // Вспомогательный DFS трёх цветов для поиска цикла (к Задаче 4)
    private static boolean hasCycleDFS(int[][] matrix, int u, int[] color) {
        color[u] = 1; // При входе в вершину красим её в СЕРЫЙ цвет (1)
        for (int v = 0; v < matrix.length; v++) { // Перебираем исходящие ребра
            if (matrix[u][v] != 0) { // Если ребро u -> v существует
                if (color[v] == 1) return true; // Если пришли в СЕРУЮ вершину — цикл замкнулся!
                if (color[v] == 0 && hasCycleDFS(matrix, v, color)) return true; // Если белая — идем глубже
            }
        }
        color[u] = 2; // При выходе из вершины красим её в ЧЕРНЫЙ цвет (2)
        return false; // Циклов не обнаружено
    }

    // ================= ЗАДАЧА 3: Кратчайший путь между двумя вершинами ациклического графа =================
    public static int shortestPathDAG(int[][] matrix, int start, int target) {
        if (!isAcyclic(matrix)) { // Сначала убеждаемся, что граф действительно без циклов (Задача 4)
            throw new IllegalArgumentException("Граф содержит циклы! Метод применим только к DAG.");
        }

        int n = matrix.length; // Число вершин
        boolean[] visited = new boolean[n]; // Массив посещенных вершин для топологической сортировки
        List<Integer> topoOrder = new ArrayList<>(); // Список вершин в порядке топологической сортировки

        for (int i = 0; i < n; i++) { // Обходим все вершины
            if (!visited[i]) topoDFS(matrix, i, visited, topoOrder); // Собираем время выхода
        }
        Collections.reverse(topoOrder); // Разворачиваем список — получаем топологический порядок

        int[] dist = new int[n]; // Массив кратчайших расстояний
        Arrays.fill(dist, INF); // Заполняем бесконечностью
        dist[start] = 0; // До старта расстояние 0

        // Релаксируем ребра строго в порядке топологической сортировки
        for (int u : topoOrder) {
            if (dist[u] != INF) { // Если до вершины u можно добраться из старта
                for (int v = 0; v < n; v++) { // Смотрим все ребра из u
                    if (matrix[u][v] != 0) { // Если ребро есть
                        if (dist[u] + matrix[u][v] < dist[v]) {
                            dist[v] = dist[u] + matrix[u][v]; // Обновляем расстояние до v
                        }
                    }
                }
            }
        }
        return dist[target]; // Возвращаем кратчайшее расстояние до целевой вершины target
    }

    // Вспомогательный DFS для топологической сортировки (к Задаче 3)
    private static void topoDFS(int[][] matrix, int u, boolean[] visited, List<Integer> topoOrder) {
        visited[u] = true; // Помечаем вершину посещенной
        for (int v = 0; v < matrix.length; v++) {
            if (matrix[u][v] != 0 && !visited[v]) { // Переходим в непосещенных соседей
                topoDFS(matrix, v, visited, topoOrder);
            }
        }
        topoOrder.add(u); // На выходе из рекурсии добавляем вершину в список
    }

    // ================= ЗАДАЧА 5: Алгоритм Флойда–Уоршелла =================
    public static int[][] floydWarshall(int[][] matrix) {
        int n = matrix.length; // Количество вершин
        int[][] dist = new int[n][n]; // Матрица кратчайших расстояний между всеми парами вершин

        // 1. Начальная инициализация матрицы расстояний
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) dist[i][j] = 0; // На главной диагонали — нули (расстояние до себя = 0)
                else if (matrix[i][j] != 0) dist[i][j] = matrix[i][j]; // Если есть ребро — берем его вес
                else dist[i][j] = INF; // Если ребра нет — ставим бесконечность
            }
        }

        // 2. Основной тройной цикл динамического программирования
        for (int k = 0; k < n; k++) { // k — промежуточная (транзитная) вершина
            for (int i = 0; i < n; i++) { // i — начальная вершина
                for (int j = 0; j < n; j++) { // j — конечная вершина
                    // Если существуют пути i -> k и k -> j, проверяем, выгоднее ли идти через k
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j]; // Обновляем кратчайший путь i -> j
                        }
                    }
                }
            }
        }
        return dist; // Возвращаем матрицу кратчайших расстояний между всеми парами
    }

    // ================= МЕТОД MAIN ДЛЯ ЗАПУСКА И ПРОВЕРКИ ГЛАВЫ Р10 =================
    public static void main(String[] args) {
        // Взвешенный граф без отрицательных весов (для Дейкстры и Флойда-Уоршелла)
        int[][] weightedGraph = {
                {0, 10,  0, 30, 100},
                {0,  0, 50,  0,   0},
                {0,  0,  0,  0,  10},
                {0,  0, 20,  0,  60},
                {0,  0,  0,  0,   0}
        };
        int n = weightedGraph.length;

        System.out.println("=== ПРАКТИЧЕСКАЯ РАБОТА Р10: КРАТЧАЙШИЕ ПУТИ ===");

        // Тест Задачи 1
        int[] distSimple = dijkstraSimple(weightedGraph, 0); // Расстояния от вершины 0
        System.out.println("Задача 1. Кратчайшие расстояния Дейкстры от вершины 0: " + Arrays.toString(distSimple));

        // Тест Задачи 2
        int[] parent = new int[n]; // Массив для сохранения предков
        int[] distPaths = dijkstraWithPaths(weightedGraph, 0, parent); // Дейкстра с путями
        System.out.println("Задача 2. Восстановленный кратчайший путь (0 -> 4): " + reconstructPath(4, parent)
                + " (Длина = " + distPaths[4] + ")");

        // Ориентированный ациклический граф (DAG) для Задач 3 и 4
        int[][] dagMatrix = {
                {0, 5, 3, 0, 0},
                {0, 0, 0, 6, 2},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0}
        };

        // Тест Задачи 4
        boolean acyclic = isAcyclic(dagMatrix); // Проверяем, нет ли циклов
        System.out.println("Задача 4. Граф dagMatrix является ациклическим? " + acyclic);

        // Тест Задачи 3
        int dagShortest = shortestPathDAG(dagMatrix, 0, 3); // Путь 0 -> 2 -> 1 -> 4 -> 3 (вес 3 + 1 + 2 + 1 = 7)
        System.out.println("Задача 3. Кратчайшее расстояние в ациклическом графе (0 -> 3): " + dagShortest);

        // Тест Задачи 5
        System.out.println("Задача 5. Матрица кратчайших путей Флойда-Уоршелла:");
        int[][] floydResult = floydWarshall(weightedGraph); // Запускаем Флойда-Уоршелла
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Выводим "INF", если пути нет, иначе само расстояние
                System.out.printf("%5s ", (floydResult[i][j] == INF ? "INF" : floydResult[i][j]));
            }
            System.out.println(); // Переход на новую строку матрицы
        }
    }
}