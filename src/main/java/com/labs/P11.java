package com.labs;
import java.util.*; // Импортируем утилиты для работы с коллекциями и генератором случайных чисел

public class P11 {

    static final int INF = 1_000_000_000; // Бесконечность

    // Вспомогательная функция: подсчет суммарного веса маршрута (для списка List<Integer>)
    public static int getListTourCost(int[][] matrix, List<Integer> tour) {
        int cost = 0; // Начальный вес равен 0
        for (int i = 0; i < tour.size() - 1; i++) { // Идем по всем соседним парам городов в списке
            cost += matrix[tour.get(i)][tour.get(i + 1)]; // Прибавляем вес ребра
        }
        return cost; // Возвращаем сумму
    }

    // Вспомогательная функция: подсчет веса замкнутого маршрута (для массива int[] длины N)
    public static int calculateTourCost(int[][] matrix, int[] tour) {
        int cost = 0; // Сумма весов
        int n = tour.length; // Количество городов
        for (int i = 0; i < n - 1; i++) {
            cost += matrix[tour[i]][tour[i + 1]]; // Прибавляем вес перехода между соседними городами
        }
        cost += matrix[tour[n - 1]][tour[0]]; // Замыкаем маршрут: прибавляем переход из последнего города в первый
        return cost; // Возвращаем полную стоимость цикла
    }

    // ================= ЗАДАЧА 1: Метод ближайшего соседа =================
    public static List<Integer> nearestNeighborTSP(int[][] matrix, int start) {
        int n = matrix.length; // Число городов
        boolean[] visited = new boolean[n]; // Массив посещенных городов
        List<Integer> tour = new ArrayList<>(); // Список для сохранения порядка обхода

        int curr = start; // Начинаем в городе start
        tour.add(curr); // Добавляем старт в маршрут
        visited[curr] = true; // Отмечаем стартовый город посещенным

        for (int step = 1; step < n; step++) { // Нам нужно посетить еще N - 1 городов
            int nextCity = -1; // Индекс ближайшего непосещенного города
            int minDist = INF; // Минимальное расстояние до соседа

            for (int candidate = 0; candidate < n; candidate++) { // Просматриваем все города
                // Если город не посещен и расстояние до него меньше текущего минимума
                if (!visited[candidate] && matrix[curr][candidate] > 0 && matrix[curr][candidate] < minDist) {
                    minDist = matrix[curr][candidate]; // Обновляем минимальное расстояние
                    nextCity = candidate; // Запоминаем лучший город
                }
            }

            visited[nextCity] = true; // Отмечаем выбранный город посещенным
            tour.add(nextCity); // Добавляем его в маршрут
            curr = nextCity; // Перемещаемся в выбранный город
        }

        tour.add(start); // Замыкаем цикл возвратом в стартовый город
        return tour; // Возвращаем построенный маршрут
    }

    // ================= ЗАДАЧА 2: Метод последовательного включения =================
    public static List<Integer> sequentialInsertionTSP(int[][] matrix, int start) {
        int n = matrix.length; // Количество городов
        boolean[] inTour = new boolean[n]; // Отметки городов, уже добавленных в цикл
        List<Integer> tour = new ArrayList<>(); // Список вершин цикла

        // 1. Ищем ближайший к start город для формирования начального цикла из 2 городов
        int second = -1;
        int minDist = INF;
        for (int i = 0; i < n; i++) {
            if (i != start && matrix[start][i] < minDist) {
                minDist = matrix[start][i];
                second = i;
            }
        }

        // Создаем стартовый цикл: start -> second -> start
        tour.add(start);
        tour.add(second);
        tour.add(start);
        inTour[start] = true;
        inTour[second] = true;

        // 2. По очереди вставляем оставшиеся N - 2 города в самую выгодную позицию цикла
        for (int step = 2; step < n; step++) {
            int bestCity = -1; // Какой город выгоднее всего вставить
            int bestInsertPos = -1; // Между какими элементами списка его вставить
            int minIncrease = INF; // Минимальное увеличение длины маршрута

            for (int k = 0; k < n; k++) { // Перебираем все еще не включенные города k
                if (!inTour[k]) {
                    // Пробуем разорвать каждое ребро (u, v) текущего цикла и вставить туда город k
                    for (int pos = 0; pos < tour.size() - 1; pos++) {
                        int u = tour.get(pos); // Начало ребра
                        int v = tour.get(pos + 1); // Конец ребра
                        // Прирост длины: новые ребра (u, k) + (k, v) минус старое ребро (u, v)
                        int increase = matrix[u][k] + matrix[k][v] - matrix[u][v];
                        if (increase < minIncrease) { // Если прирост минимальный
                            minIncrease = increase; // Запоминаем лучший прирост
                            bestCity = k; // Запоминаем город
                            bestInsertPos = pos + 1; // Запоминаем индекс для вставки в список
                        }
                    }
                }
            }

            tour.add(bestInsertPos, bestCity); // Вставляем город в оптимальное место цикла
            inTour[bestCity] = true; // Помечаем город как включенный в маршрут
        }
        return tour; // Возвращаем готовый цикл
    }

    // ================= ЗАДАЧА 3*: Метод Литтла (редукция матриц + ветви и границы) =================
    static int bestExactCost; // Глобальная переменная для хранения стоимости лучшего точного цикла
    static int[] bestExactTour; // Глобальный массив для хранения лучшего точного маршрута

    // Функция приведения (редукции) матрицы по строкам и столбцам — основа метода Литтла
    public static int reduceMatrixLittle(int[][] m) {
        int n = m.length;
        int sumReduction = 0; // Сумма вычтенных минимумов (нижняя оценка стоимости H)

        // Шаг 1: Редукция по строкам
        for (int i = 0; i < n; i++) {
            int minRow = INF; // Ищем минимальный элемент в строке i
            for (int j = 0; j < n; j++) if (m[i][j] < minRow) minRow = m[i][j];
            if (minRow != INF && minRow > 0) { // Если минимум больше нуля
                sumReduction += minRow; // Прибавляем его к нижней границе H
                for (int j = 0; j < n; j++) if (m[i][j] != INF) m[i][j] -= minRow; // Вычитаем из всех элементов строки
            }
        }

        // Шаг 2: Редукция по столбцам
        for (int j = 0; j < n; j++) {
            int minCol = INF; // Ищем минимальный элемент в столбце j
            for (int i = 0; i < n; i++) if (m[i][j] < minCol) minCol = m[i][j];
            if (minCol != INF && minCol > 0) { // Если минимум больше нуля
                sumReduction += minCol; // Прибавляем его к нижней границе H
                for (int i = 0; i < n; i++) if (m[i][j] != INF) m[i][j] -= minCol; // Вычитаем из всех элементов столбца
            }
        }
        return sumReduction; // Возвращаем константу приведения матрицы
    }

    public static int[] littleMethodTSP(int[][] matrix) {
        int n = matrix.length;
        int[][] copy = new int[n][n]; // Создаем рабочую копию матрицы
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copy[i][j] = (i == j) ? INF : matrix[i][j]; // Запрещаем петли (диагональ = INF)
            }
        }
        int lowerBoundH = reduceMatrixLittle(copy); // Выполняем редукцию матрицы и находим начальную границу H
        System.out.println("   [Метод Литтла] Начальная нижняя граница после редукции H = " + lowerBoundH);
        return boundedDFS_TSP(matrix); // Находим точный маршрут методом ветвей и границ
    }

    // ================= ЗАДАЧА 4*: Ограниченный перебор при обходе в глубину (DFS Branch & Bound) =================
    public static int[] boundedDFS_TSP(int[][] matrix) {
        int n = matrix.length; // Количество городов
        bestExactCost = INF; // Сбрасываем рекорд стоимости до бесконечности
        bestExactTour = new int[n + 1]; // Массив под лучший замкнутый маршрут

        boolean[] visited = new boolean[n]; // Посещенные города
        int[] currentPath = new int[n + 1]; // Текущий путь в рекурсии
        visited[0] = true; // Начинаем поиск всегда из города 0
        currentPath[0] = 0; // Первый элемент пути — 0

        branchAndBoundDFS(matrix, 0, 1, 0, visited, currentPath); // Запускаем ограниченный перебор в глубину
        return bestExactTour; // Возвращаем оптимальный маршрут
    }

    // Рекурсивный обход в глубину с отсечением бесперспективных ветвей (к Задаче 4)
    private static void branchAndBoundDFS(int[][] matrix, int curr, int count, int cost, boolean[] visited, int[] currentPath) {
        int n = matrix.length;

        // ОТСЕЧЕНИЕ: если накопленный вес пути уже больше или равен лучшему рекорду — прекращаем обход этой ветки!
        if (cost >= bestExactCost) return;

        if (count == n) { // Если обошли все N городов
            if (matrix[curr][0] > 0) { // Проверяем наличие обратного ребра в стартовый город 0
                int totalCost = cost + matrix[curr][0]; // Полная стоимость цикла
                if (totalCost < bestExactCost) { // Если нашли новый рекорд
                    bestExactCost = totalCost; // Обновляем минимальную стоимость
                    System.arraycopy(currentPath, 0, bestExactTour, 0, n); // Сохраняем вершины маршрута
                    bestExactTour[n] = 0; // Замыкаем цикл вершиной 0
                }
            }
            return; // Возвращаемся назад
        }

        // Пробуем перейти во все еще не посещенные города
        for (int next = 0; next < n; next++) {
            if (!visited[next] && matrix[curr][next] > 0) {
                visited[next] = true; // Занимаем город next
                currentPath[count] = next; // Записываем его в текущий маршрут
                branchAndBoundDFS(matrix, next, count + 1, cost + matrix[curr][next], visited, currentPath); // Шаг вглубь
                visited[next] = false; // БЭКТРЕКИНГ: освобождаем город при откате назад
            }
        }
    }

    // ================= ЗАДАЧА 5*: Метод имитации отжига (Simulated Annealing) =================
    public static int[] simulatedAnnealingTSP(int[][] matrix) {
        int n = matrix.length; // Количество городов
        Random rand = new Random(42); // Фиксируем зерно рандома для повторяемости результата

        // 1. Формируем начальный маршрут: 0 -> 1 -> 2 -> ... -> n-1
        int[] currentTour = new int[n];
        for (int i = 0; i < n; i++) currentTour[i] = i;
        int currentCost = calculateTourCost(matrix, currentTour); // Считаем его стоимость

        int[] bestTour = currentTour.clone(); // Лучший найденный маршрут
        int bestCost = currentCost; // Лучшая найденная стоимость

        double temp = 10000.0; // Начальная температура
        double coolingRate = 0.995; // Коэффициент охлаждения на каждой итерации

        // 2. Цикл остывания
        while (temp > 0.001) { // Пока температура выше порога замерзания
            int[] nextTour = currentTour.clone(); // Копируем текущий маршрут
            // Выбираем два случайных индекса (от 1 до n-1, чтобы старт 0 оставался на месте)
            int i = 1 + rand.nextInt(n - 1);
            int j = 1 + rand.nextInt(n - 1);
            // Меняем местами два города (элементарная мутация состояния)
            int swap = nextTour[i]; nextTour[i] = nextTour[j]; nextTour[j] = swap;

            int nextCost = calculateTourCost(matrix, nextTour); // Стоимость нового состояния
            int delta = nextCost - currentCost; // Изменение энергии (длины маршрута)

            // Принимаем новый маршрут, если он лучше (delta < 0) ИЛИ с вероятностью e^(-delta / T)
            if (delta < 0 || Math.exp(-delta / temp) > rand.nextDouble()) {
                currentTour = nextTour; // Переходим в новое состояние
                currentCost = nextCost; // Обновляем текущую стоимость

                if (currentCost < bestCost) { // Если улучшили глобальный рекорд
                    bestCost = currentCost; // Сохраняем рекордную стоимость
                    bestTour = currentTour.clone(); // Сохраняем лучший маршрут
                }
            }
            temp *= coolingRate; // Уменьшаем температуру
        }
        return bestTour; // Возвращаем лучший маршрут
    }

    // ================= ЗАДАЧА 6*: Метод генетической эволюции (Генетический алгоритм) =================
    public static int[] geneticAlgorithmTSP(int[][] matrix) {
        int n = matrix.length; // Количество городов
        int popSize = 20; // Размер популяции (20 особей)
        int generations = 100; // Число поколений эволюции
        Random rand = new Random(42); // Генератор случайных чисел

        // 1. Инициализация начальной популяции случайными перестановками городов
        int[][] population = new int[popSize][n];
        for (int p = 0; p < popSize; p++) {
            for (int i = 0; i < n; i++) population[p][i] = i; // Заполняем 0..n-1
            for (int i = 1; i < n; i++) { // Случайно перемешиваем города с 1 по n-1
                int r = 1 + rand.nextInt(n - 1);
                int tmp = population[p][i]; population[p][i] = population[p][r]; population[p][r] = tmp;
            }
        }

        // 2. Эволюционный цикл по поколениям
        for (int gen = 0; gen < generations; gen++) {
            // Сортируем особей по возрастанию длины маршрута (самые приспособленные — в начале)
            Arrays.sort(population, Comparator.comparingInt(tour -> calculateTourCost(matrix, tour)));

            int[][] nextGen = new int[popSize][n]; // Массив для нового поколения
            // Элитизм: две самые лучшие особи переходят в следующее поколение напрямую
            nextGen[0] = population[0].clone();
            nextGen[1] = population[1].clone();

            // Скрещивание (кроссовер) и мутация для получения остальных особей
            for (int p = 2; p < popSize; p++) {
                int[] parent1 = population[rand.nextInt(popSize / 2)]; // Выбираем родителя 1 из лучшей половины
                int[] parent2 = population[rand.nextInt(popSize / 2)]; // Выбираем родителя 2 из лучшей половины
                int[] child = orderCrossover(parent1, parent2); // Получаем потомка через упорядоченный кроссовер

                // С вероятностью 20% подвергаем потомка мутации (меняем 2 города местами)
                if (rand.nextDouble() < 0.2 && n > 2) {
                    int i = 1 + rand.nextInt(n - 1);
                    int j = 1 + rand.nextInt(n - 1);
                    int tmp = child[i]; child[i] = child[j]; child[j] = tmp;
                }
                nextGen[p] = child; // Добавляем потомка в новое поколение
            }
            population = nextGen; // Сменяем поколение
        }

        // Сортируем итоговую популяцию и возвращаем лучшую особь
        Arrays.sort(population, Comparator.comparingInt(tour -> calculateTourCost(matrix, tour)));
        return population[0];
    }

    // Упорядоченный кроссовер (Order Crossover), исключающий дублирование городов в маршруте (к Задаче 6)
    private static int[] orderCrossover(int[] p1, int[] p2) {
        int n = p1.length;
        int[] child = new int[n];
        boolean[] used = new boolean[n]; // Массив уже использованных в потомке городов

        int cut = n / 2; // Точка разреза хромосомы
        for (int i = 0; i < cut; i++) { // Первую половину маршрута копируем от первого родителя p1
            child[i] = p1[i];
            used[p1[i]] = true; // Помечаем эти города как уже присутствующие в маршруте
        }

        int idx = cut; // Указатель для заполнения оставшихся генов ребенка
        for (int i = 0; i < n; i++) { // Проходим по генам второго родителя p2
            if (!used[p2[i]]) { // Если город p2[i] еще не включен в ребенка
                child[idx++] = p2[i]; // Добавляем его на свободную позицию
            }
        }
        return child; // Возвращаем валидный маршрут-потомок
    }

    // ================= МЕТОД MAIN ДЛЯ ЗАПУСКА И ПРОВЕРКИ ГЛАВЫ Р11 =================
    public static void main(String[] args) {
        // Полный взвешенный граф из 5 городов (матрица расстояний между городами)
        int[][] matrix = {
                { 0, 10, 15, 20, 25},
                {10,  0, 35, 25, 15},
                {15, 35,  0, 30, 20},
                {20, 25, 30,  0, 10},
                {25, 15, 20, 10,  0}
        };

        System.out.println("=== ПРАКТИЧЕСКАЯ РАБОТА Р11: ЗАДАЧА КОММИВОЯЖЕРА (TSP) ===");

        // Тест Задачи 1
        List<Integer> nnTour = nearestNeighborTSP(matrix, 0);
        System.out.println("Задача 1. Метод ближайшего соседа:        " + nnTour
                + " | Вес = " + getListTourCost(matrix, nnTour));

        // Тест Задачи 2
        List<Integer> siTour = sequentialInsertionTSP(matrix, 0);
        System.out.println("Задача 2. Метод последоват. включения:    " + siTour
                + " | Вес = " + getListTourCost(matrix, siTour));

        // Тест Задачи 3*
        int[] littleTour = littleMethodTSP(matrix);
        System.out.println("Задача 3*. Метод Литтла (точный):         " + Arrays.toString(littleTour)
                + " | Вес = " + bestExactCost);

        // Тест Задачи 4*
        int[] bnbTour = boundedDFS_TSP(matrix);
        System.out.println("Задача 4*. Ограниченный перебор в глубину: " + Arrays.toString(bnbTour)
                + " | Вес = " + bestExactCost);

        // Тест Задачи 5*
        int[] saTour = simulatedAnnealingTSP(matrix);
        System.out.println("Задача 5*. Метод имитации отжига:         " + Arrays.toString(saTour) + " -> 0"
                + " | Вес = " + calculateTourCost(matrix, saTour));

        // Тест Задачи 6*
        int[] gaTour = geneticAlgorithmTSP(matrix);
        System.out.println("Задача 6*. Генетический алгоритм:         " + Arrays.toString(gaTour) + " -> 0"
                + " | Вес = " + calculateTourCost(matrix, gaTour));
    }
}