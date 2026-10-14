package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GraphPathsChapter9 {

    public static void main(String[] args) {
        // Матрица смежности из предыдущих лабораторных работ (GraphTasks и GraphDFS)
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

        // 1. Проверка наличия простого пути между вершинами u и v
        int u = 0;
        int v = 4;
        boolean hasPath = hasSimplePath(adjacencyMatrix, u, v);
        System.out.println("1. Наличие простого пути между " + u + " и " + v + ": " + hasPath);

        System.out.println("-------------------------------------------------");

        // 2. Нахождение вершин простого пути в массиве
        int[] simplePath = findSimplePath(adjacencyMatrix, u, v);
        System.out.println("2. Вершины простого пути между " + u + " и " + v + ":");
        System.out.println(Arrays.toString(simplePath));

        System.out.println("-------------------------------------------------");

        // 3. Проверка наличия эйлерова цикла между двумя вершинами
        // Для демонстрации создадим граф с эйлеровым циклом (степени всех вершин четные)
        int[][] eulerMatrix = {
                {0, 1, 1, 1, 1}, // 0: степень 4
                {1, 0, 1, 0, 0}, // 1: степень 2
                {1, 1, 0, 0, 0}, // 2: степень 2
                {1, 0, 0, 0, 1}, // 3: степень 2
                {1, 0, 0, 1, 0}  // 4: степень 2
        };

        boolean hasEuler = hasEulerCycle(eulerMatrix, 1, 3);
        System.out.println("3. Наличие эйлерова цикла между вершинами 1 и 3: " + hasEuler);

        System.out.println("-------------------------------------------------");

        // 4. Запоминание вершин эйлерова цикла в массиве
        int[] eulerCycle = findEulerCycle(eulerMatrix, 0);
        System.out.println("4. Массив вершин эйлерова цикла (старт с вершины 0):");
        System.out.println(Arrays.toString(eulerCycle));

        System.out.println("-------------------------------------------------");

        // 5. Проверка наличия гамильтонова цикла между двумя вершинами
        // Граф-цикл C5 с хордой (0-2)
        int[][] hamiltonianMatrix = {
                {0, 1, 1, 0, 1}, // 0
                {1, 0, 1, 0, 0}, // 1
                {1, 1, 0, 1, 0}, // 2
                {0, 0, 1, 0, 1}, // 3
                {1, 0, 0, 1, 0}  // 4
        };

        boolean hasHam = hasHamiltonianCycle(hamiltonianMatrix, 0, 4);
        System.out.println("5. Наличие гамильтонова цикла между вершинами 0 и 4: " + hasHam);

        int[] hamCycle = findHamiltonianCycle(hamiltonianMatrix, 0);
        System.out.println("   Массив вершин гамильтонова цикла: " + Arrays.toString(hamCycle));

        System.out.println("-------------------------------------------------");

        // 6. Реализация вышеуказанных функций с помощью списков смежности (ArrayList<Integer>[])
        System.out.println("6. Тестирование функций на СПИСКАХ СМЕЖНОСТИ (ArrayList<Integer>[]):");

        ArrayList<Integer>[] adjList = matrixToAdjList(adjacencyMatrix);
        ArrayList<Integer>[] eulerAdjList = matrixToAdjList(eulerMatrix);
        ArrayList<Integer>[] hamAdjList = matrixToAdjList(hamiltonianMatrix);

        System.out.println("   6.1. Наличие простого пути (0 -> 4): " + hasSimplePath(adjList, 0, 4));
        System.out.println("   6.2. Вершины простого пути: " + Arrays.toString(findSimplePath(adjList, 0, 4)));
        System.out.println("   6.3. Наличие эйлерова цикла (между 1 и 3): " + hasEulerCycle(eulerAdjList, 1, 3));
        System.out.println("   6.4. Вершины эйлерова цикла: " + Arrays.toString(findEulerCycle(eulerAdjList, 0)));
        System.out.println("   6.5. Наличие гамильтонова цикла (0 и 4): " + hasHamiltonianCycle(hamAdjList, 0, 4));
    }

    // =========================================================================
    // ЧАСТЬ 1: МАТРИЦА СМЕЖНОСТИ (int[][])
    // =========================================================================

    /**
     * Задание 1. Функция, определяющая наличие простого пути между двумя вершинами
     * графа, представленного в матрице смежности.
     * Используется обход в глубину (DFS) со Stack, аналогично GraphDFS.
     */
    public static boolean hasSimplePath(int[][] matrix, int u, int v) {
        int n = matrix.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return false;
        }
        if (u == v) {
            return true;
        }

        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        // Стартовую вершину помещаем в стек
        stack.push(u);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();

            if (currentNode == v) {
                return true; // Целевая вершина достигнута
            }

            if (!visited[currentNode]) {
                visited[currentNode] = true;

                // Добавляем непосещенных соседей в стек
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Задание 2. Функция (модификация из первого задания), запоминающая вершины
     * простого пути в массиве для графа, представленного в матрице смежности.
     * Возвращает int[] с номерами вершин пути.
     */
    public static int[] findSimplePath(int[][] matrix, int u, int v) {
        int n = matrix.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return new int[0];
        }

        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        // Рекурсивный поиск с возвратом (backtracking) для построения простого пути
        boolean found = dfsPath(matrix, u, v, visited, path);

        if (!found) {
            return new int[0];
        }

        // Перевод списка в массив (в стиле GraphTasks и GraphDFS)
        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }

    private static boolean dfsPath(int[][] matrix, int current, int target, boolean[] visited, List<Integer> path) {
        visited[current] = true;
        path.add(current);

        if (current == target) {
            return true;
        }

        for (int neighbor = 0; neighbor < matrix.length; neighbor++) {
            if (matrix[current][neighbor] == 1 && !visited[neighbor]) {
                if (dfsPath(matrix, neighbor, target, visited, path)) {
                    return true;
                }
            }
        }

        // Если путь через соседа не привел к цели — делаем откат
        path.remove(path.size() - 1);
        return false;
    }

    /**
     * Задание 3. Функция, определяющая наличие эйлерова цикла между двумя вершинами
     * графа, представленного в матрице смежности.
     * Критерий Эйлера:
     * 1) Степень каждой вершины графа четна.
     * 2) Все вершины с ненулевой степенью принадлежат одной компоненте связности.
     * 3) Вершины u и v имеют ребра и входят в эту компоненту.
     */
    public static boolean hasEulerCycle(int[][] matrix, int u, int v) {
        int n = matrix.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return false;
        }

        int[] degrees = new int[n];
        int startNode = -1;

        // 1. Подсчет степеней вершин и проверка четности
        for (int i = 0; i < n; i++) {
            int degree = 0;
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    degree++;
                }
            }
            degrees[i] = degree;
            if (degree > 0) {
                startNode = i;
            }
            // Если хотя бы одна степень нечетная, эйлерова цикла нет
            if (degree % 2 != 0) {
                return false;
            }
        }

        // Если в графе вообще нет ребер
        if (startNode == -1) {
            return u == v;
        }

        // Вершины u и v должны иметь инцидентные ребра для участия в цикле
        if (degrees[u] == 0 || degrees[v] == 0) {
            return false;
        }

        // 2. Проверка связности вершин ненулевой степени через DFS со стеком
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();
            if (!visited[currentNode]) {
                visited[currentNode] = true;
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (matrix[currentNode][neighbor] == 1 && !visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        // Все вершины с ребрами должны быть в одной компоненте
        for (int i = 0; i < n; i++) {
            if (degrees[i] > 0 && !visited[i]) {
                return false;
            }
        }

        return visited[u] && visited[v];
    }

    /**
     * Задание 4. Функция, запоминающая вершины эйлерова цикла в массиве для графа,
     * представленного в матрице смежности (алгоритм Хирхольцера).
     */
    public static int[] findEulerCycle(int[][] matrix, int startNode) {
        int n = matrix.length;
        if (n == 0 || startNode < 0 || startNode >= n) {
            return new int[0];
        }

        // Проверяем, существует ли эйлеров цикл
        if (!hasEulerCycle(matrix, startNode, startNode)) {
            return new int[0];
        }

        // Копия матрицы, чтобы безопасно удалять пройденные ребра
        int[][] adj = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adj[i][j] = matrix[i][j];
            }
        }

        Stack<Integer> stack = new Stack<>();
        List<Integer> circuit = new ArrayList<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            int current = stack.peek();
            int nextNeighbor = -1;

            // Ищем любого соседа с непройденным ребром
            for (int neighbor = 0; neighbor < n; neighbor++) {
                if (adj[current][neighbor] == 1) {
                    nextNeighbor = neighbor;
                    break;
                }
            }

            if (nextNeighbor != -1) {
                // Удаляем пройденное ребро в обоих направлениях
                adj[current][nextNeighbor] = 0;
                adj[nextNeighbor][current] = 0;
                stack.push(nextNeighbor);
            } else {
                circuit.add(stack.pop());
            }
        }

        // Разворачиваем список, так как вершины выталкивались с конца
        Collections.reverse(circuit);

        // Перевод списка в массив
        int[] resultArray = new int[circuit.size()];
        for (int i = 0; i < circuit.size(); i++) {
            resultArray[i] = circuit.get(i);
        }

        return resultArray;
    }

    /**
     * Задание 5. Функция, определяющая наличие гамильтонова цикла между двумя вершинами u и v.
     * Проверяет существование простого пути, проходящего через ВСЕ n вершин графа,
     * начинающегося в u, заканчивающегося в v и замыкающегося ребром (v, u).
     */
    public static boolean hasHamiltonianCycle(int[][] matrix, int u, int v) {
        int n = matrix.length;
        if (u < 0 || u >= n || v < 0 || v >= n || n < 3) {
            return false;
        }

        // Если между u и v нет ребра, цикл замкнуть невозможно
        if (matrix[u][v] != 1) {
            return false;
        }

        boolean[] visited = new boolean[n];
        visited[u] = true;

        return dfsHamiltonian(matrix, u, v, 1, n, visited);
    }

    private static boolean dfsHamiltonian(int[][] matrix, int current, int targetEnd,
                                          int count, int totalVertices, boolean[] visited) {
        if (count == totalVertices) {
            return current == targetEnd;
        }

        for (int neighbor = 0; neighbor < totalVertices; neighbor++) {
            if (matrix[current][neighbor] == 1 && !visited[neighbor]) {
                visited[neighbor] = true;
                if (dfsHamiltonian(matrix, neighbor, targetEnd, count + 1, totalVertices, visited)) {
                    return true;
                }
                visited[neighbor] = false; // Откат (backtracking)
            }
        }

        return false;
    }

    /**
     * Дополнительный метод для Задания 5: поиск и сохранение вершин гамильтонова цикла в массив.
     */
    public static int[] findHamiltonianCycle(int[][] matrix, int startNode) {
        int n = matrix.length;
        if (n < 3 || startNode < 0 || startNode >= n) {
            return new int[0];
        }

        int[] path = new int[n + 1];
        Arrays.fill(path, -1);
        path[0] = startNode;

        boolean[] visited = new boolean[n];
        visited[startNode] = true;

        if (findHamUtil(matrix, path, 1, n, visited)) {
            path[n] = startNode;
            return path;
        }

        return new int[0];
    }

    private static boolean findHamUtil(int[][] matrix, int[] path, int pos, int n, boolean[] visited) {
        if (pos == n) {
            return matrix[path[pos - 1]][path[0]] == 1;
        }

        for (int v = 0; v < n; v++) {
            if (matrix[path[pos - 1]][v] == 1 && !visited[v]) {
                visited[v] = true;
                path[pos] = v;

                if (findHamUtil(matrix, path, pos + 1, n, visited)) {
                    return true;
                }

                visited[v] = false;
                path[pos] = -1;
            }
        }

        return false;
    }

    // =========================================================================
    // ЧАСТЬ 2: ЗАДАНИЕ 6 — СПИСКИ СМЕЖНОСТИ (ArrayList<Integer>[])
    // =========================================================================

    /**
     * 6.1. Определение наличия простого пути между двумя вершинами (списки смежности).
     */
    public static boolean hasSimplePath(ArrayList<Integer>[] adjList, int u, int v) {
        int n = adjList.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return false;
        }
        if (u == v) {
            return true;
        }

        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        stack.push(u);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();

            if (currentNode == v) {
                return true;
            }

            if (!visited[currentNode]) {
                visited[currentNode] = true;
                for (int neighbor : adjList[currentNode]) {
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return false;
    }

    /**
     * 6.2. Запоминание вершин простого пути в массиве (списки смежности).
     */
    public static int[] findSimplePath(ArrayList<Integer>[] adjList, int u, int v) {
        int n = adjList.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return new int[0];
        }

        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        boolean found = dfsPathAdj(adjList, u, v, visited, path);
        if (!found) {
            return new int[0];
        }

        // Перевод списка в массив
        int[] resultArray = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            resultArray[i] = path.get(i);
        }

        return resultArray;
    }

    private static boolean dfsPathAdj(ArrayList<Integer>[] adjList, int current, int target,
                                      boolean[] visited, List<Integer> path) {
        visited[current] = true;
        path.add(current);

        if (current == target) {
            return true;
        }

        for (int neighbor : adjList[current]) {
            if (!visited[neighbor]) {
                if (dfsPathAdj(adjList, neighbor, target, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    /**
     * 6.3. Определение наличия эйлерова цикла между двумя вершинами (списки смежности).
     */
    public static boolean hasEulerCycle(ArrayList<Integer>[] adjList, int u, int v) {
        int n = adjList.length;
        if (u < 0 || u >= n || v < 0 || v >= n) {
            return false;
        }

        int startNode = -1;

        for (int i = 0; i < n; i++) {
            int degree = adjList[i].size();
            if (degree > 0) {
                startNode = i;
            }
            if (degree % 2 != 0) {
                return false;
            }
        }

        if (startNode == -1) {
            return u == v;
        }

        if (adjList[u].isEmpty() || adjList[v].isEmpty()) {
            return false;
        }

        // Проверка связности вершин с ребрами через стек
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int currentNode = stack.pop();
            if (!visited[currentNode]) {
                visited[currentNode] = true;
                for (int neighbor : adjList[currentNode]) {
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (!adjList[i].isEmpty() && !visited[i]) {
                return false;
            }
        }

        return visited[u] && visited[v];
    }

    /**
     * 6.4. Запоминание вершин эйлерова цикла в массиве (списки смежности).
     */
    @SuppressWarnings("unchecked")
    public static int[] findEulerCycle(ArrayList<Integer>[] adjList, int startNode) {
        int n = adjList.length;
        if (n == 0 || startNode < 0 || startNode >= n) {
            return new int[0];
        }

        if (!hasEulerCycle(adjList, startNode, startNode)) {
            return new int[0];
        }

        // Создаем изменяемую копию массива списков
        ArrayList<Integer>[] copy = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            copy[i] = new ArrayList<>(adjList[i]);
        }

        Stack<Integer> stack = new Stack<>();
        List<Integer> circuit = new ArrayList<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            int current = stack.peek();

            if (!copy[current].isEmpty()) {
                // Берем соседа и удаляем ребро (current, next) и (next, current)
                int next = copy[current].remove(copy[current].size() - 1);
                copy[next].remove(Integer.valueOf(current));
                stack.push(next);
            } else {
                circuit.add(stack.pop());
            }
        }

        Collections.reverse(circuit);

        // Перевод списка в массив
        int[] resultArray = new int[circuit.size()];
        for (int i = 0; i < circuit.size(); i++) {
            resultArray[i] = circuit.get(i);
        }

        return resultArray;
    }

    /**
     * 6.5. Определение наличия гамильтонова цикла между двумя вершинами (списки смежности).
     */
    public static boolean hasHamiltonianCycle(ArrayList<Integer>[] adjList, int u, int v) {
        int n = adjList.length;
        if (u < 0 || u >= n || v < 0 || v >= n || n < 3) {
            return false;
        }

        // Проверяем наличие ребра между u и v
        if (!adjList[u].contains(v)) {
            return false;
        }

        boolean[] visited = new boolean[n];
        visited[u] = true;

        return dfsHamAdj(adjList, u, v, 1, n, visited);
    }

    private static boolean dfsHamAdj(ArrayList<Integer>[] adjList, int current, int targetEnd,
                                     int count, int totalVertices, boolean[] visited) {
        if (count == totalVertices) {
            return current == targetEnd;
        }

        for (int neighbor : adjList[current]) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                if (dfsHamAdj(adjList, neighbor, targetEnd, count + 1, totalVertices, visited)) {
                    return true;
                }
                visited[neighbor] = false;
            }
        }

        return false;
    }

    // =========================================================================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ (КОНВЕРТАЦИЯ)
    // =========================================================================

    /**
     * Преобразование матрицы смежности в массив списков смежности ArrayList<Integer>[].
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Integer>[] matrixToAdjList(int[][] matrix) {
        int n = matrix.length;
        ArrayList<Integer>[] adjList = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            adjList[i] = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    adjList[i].add(j);
                }
            }
        }

        return adjList;
    }
}
