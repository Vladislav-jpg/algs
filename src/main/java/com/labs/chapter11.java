package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class chapter11 {

    static final int INF = 1_000_000_000;

    // Задание 1: Реализовать метод ближнего соседа для решения задачи TSP
    static String nearestNeighbor(int[][] distanceMatrix, int startCity) {
        int cityCount = distanceMatrix.length;
        boolean[] visitedCities = new boolean[cityCount];
        List<Integer> route = new ArrayList<>();

        int currentCity = startCity;
        route.add(currentCity);
        visitedCities[currentCity] = true;
        int totalCost = 0;

        for (int step = 1; step < cityCount; step++) {
            int nextCity = -1;
            int minDistance = INF;

            for (int candidateCity = 0; candidateCity < cityCount; candidateCity++) {
                if (!visitedCities[candidateCity] && distanceMatrix[currentCity][candidateCity] < minDistance) {
                    minDistance = distanceMatrix[currentCity][candidateCity];
                    nextCity = candidateCity;
                }
            }

            if (nextCity == -1 || minDistance >= INF) {
                return "Маршрут не найден";
            }

            route.add(nextCity);
            visitedCities[nextCity] = true;
            totalCost += minDistance;
            currentCity = nextCity;
        }

        if (distanceMatrix[currentCity][startCity] >= INF) {
            return "Нет возврата в стартовую вершину";
        }

        totalCost += distanceMatrix[currentCity][startCity];
        route.add(startCity);
        return "Маршрут: " + route + " | Длина пути: " + totalCost;
    }

    // Задание 2: Реализовать метод последовательного включения для решения задачи TSP
    static String sequentialInsertion(int[][] distanceMatrix, int startCity) {
        int cityCount = distanceMatrix.length;
        boolean[] includedCities = new boolean[cityCount];
        List<Integer> cycle = new ArrayList<>();

        cycle.add(startCity);
        includedCities[startCity] = true;

        int secondCity = -1;
        int minSecondDistance = INF;
        for (int candidateCity = 0; candidateCity < cityCount; candidateCity++) {
            if (candidateCity != startCity && distanceMatrix[startCity][candidateCity] < minSecondDistance) {
                minSecondDistance = distanceMatrix[startCity][candidateCity];
                secondCity = candidateCity;
            }
        }

        if (secondCity == -1 || minSecondDistance >= INF) {
            return "Маршрут не найден";
        }

        cycle.add(secondCity);
        includedCities[secondCity] = true;

        while (cycle.size() < cityCount) {
            int bestCity = -1;
            int bestPosition = -1;
            int bestIncrease = INF;

            for (int candidateCity = 0; candidateCity < cityCount; candidateCity++) {
                if (includedCities[candidateCity]) {
                    continue;
                }

                for (int position = 0; position < cycle.size(); position++) {
                    int fromCity = cycle.get(position);
                    int toCity = cycle.get((position + 1) % cycle.size());

                    int oldEdge = distanceMatrix[fromCity][toCity];
                    int firstPart = distanceMatrix[fromCity][candidateCity];
                    int secondPart = distanceMatrix[candidateCity][toCity];

                    if (oldEdge >= INF || firstPart >= INF || secondPart >= INF) {
                        continue;
                    }

                    int costIncrease = firstPart + secondPart - oldEdge;
                    if (costIncrease < bestIncrease) {
                        bestIncrease = costIncrease;
                        bestCity = candidateCity;
                        bestPosition = position + 1;
                    }
                }
            }

            if (bestCity == -1) {
                return "Маршрут не найден";
            }

            cycle.add(bestPosition, bestCity);
            includedCities[bestCity] = true;
        }

        int totalCost = 0;
        for (int index = 0; index < cycle.size(); index++) {
            int fromCity = cycle.get(index);
            int toCity = cycle.get((index + 1) % cycle.size());
            totalCost += distanceMatrix[fromCity][toCity];
        }

        List<Integer> closedRoute = new ArrayList<>(cycle);
        closedRoute.add(startCity);
        return "Маршрут: " + closedRoute + " | Длина пути: " + totalCost;
    }

    // Задание 3: Реализовать метод Литтла для решения задачи TSP
    static String littleMethod(int[][] distanceMatrix) {
        int cityCount = distanceMatrix.length;
        int[][] startMatrix = new int[cityCount][cityCount];
        for (int row = 0; row < cityCount; row++) {
            for (int col = 0; col < cityCount; col++) {
                startMatrix[row][col] = (row == col) ? INF : distanceMatrix[row][col];
            }
        }

        int startLowerBound = 0;
        for (int row = 0; row < cityCount; row++) {
            int rowMin = INF;
            for (int col = 0; col < cityCount; col++) {
                if (startMatrix[row][col] < rowMin) {
                    rowMin = startMatrix[row][col];
                }
            }
            if (rowMin > 0 && rowMin < INF) {
                startLowerBound += rowMin;
                for (int col = 0; col < cityCount; col++) {
                    if (startMatrix[row][col] < INF) {
                        startMatrix[row][col] -= rowMin;
                    }
                }
            }
        }
        for (int col = 0; col < cityCount; col++) {
            int colMin = INF;
            for (int row = 0; row < cityCount; row++) {
                if (startMatrix[row][col] < colMin) {
                    colMin = startMatrix[row][col];
                }
            }
            if (colMin > 0 && colMin < INF) {
                startLowerBound += colMin;
                for (int row = 0; row < cityCount; row++) {
                    if (startMatrix[row][col] < INF) {
                        startMatrix[row][col] -= colMin;
                    }
                }
            }
        }

        List<int[][]> matrixStack = new ArrayList<>();
        List<Integer> boundStack = new ArrayList<>();
        List<List<int[]>> includedStack = new ArrayList<>();

        matrixStack.add(startMatrix);
        boundStack.add(startLowerBound);
        includedStack.add(new ArrayList<>());

        int bestCost = INF;
        List<int[]> bestEdges = new ArrayList<>();

        while (!matrixStack.isEmpty()) {
            int lastIndex = matrixStack.size() - 1;
            int[][] currentMatrix = matrixStack.remove(lastIndex);
            int currentBound = boundStack.remove(lastIndex);
            List<int[]> includedEdges = includedStack.remove(lastIndex);

            if (currentBound >= bestCost) {
                continue;
            }

            if (includedEdges.size() == cityCount - 2) {
                List<int[]> completedEdges = new ArrayList<>(includedEdges);
                int[][] tempMatrix = new int[cityCount][cityCount];
                for (int r = 0; r < cityCount; r++) {
                    tempMatrix[r] = Arrays.copyOf(currentMatrix[r], cityCount);
                }

                boolean validCompletion = true;
                for (int step = 0; step < 2; step++) {
                    int chosenRow = -1;
                    int chosenCol = -1;
                    int minVal = INF;
                    for (int r = 0; r < cityCount; r++) {
                        for (int c = 0; c < cityCount; c++) {
                            if (tempMatrix[r][c] < minVal) {
                                minVal = tempMatrix[r][c];
                                chosenRow = r;
                                chosenCol = c;
                            }
                        }
                    }
                    if (chosenRow == -1 || minVal >= INF) {
                        validCompletion = false;
                        break;
                    }
                    completedEdges.add(new int[]{chosenRow, chosenCol});
                    for (int k = 0; k < cityCount; k++) {
                        tempMatrix[chosenRow][k] = INF;
                        tempMatrix[k][chosenCol] = INF;
                    }
                }

                if (validCompletion) {
                    int[] nextCity = new int[cityCount];
                    Arrays.fill(nextCity, -1);
                    for (int[] edge : completedEdges) {
                        nextCity[edge[0]] = edge[1];
                    }

                    int city = 0;
                    int steps = 0;
                    int realCost = 0;
                    boolean formsFullCycle = true;
                    while (steps < cityCount) {
                        int target = nextCity[city];
                        if (target == -1 || distanceMatrix[city][target] >= INF) {
                            formsFullCycle = false;
                            break;
                        }
                        realCost += distanceMatrix[city][target];
                        city = target;
                        steps++;
                    }
                    if (formsFullCycle && city == 0 && realCost < bestCost) {
                        bestCost = realCost;
                        bestEdges = completedEdges;
                    }
                }
                continue;
            }

            int bestZeroRow = -1;
            int bestZeroCol = -1;
            int maxPenalty = -1;

            for (int row = 0; row < cityCount; row++) {
                for (int col = 0; col < cityCount; col++) {
                    if (currentMatrix[row][col] == 0) {
                        int minInRow = INF;
                        for (int c = 0; c < cityCount; c++) {
                            if (c != col && currentMatrix[row][c] < minInRow) {
                                minInRow = currentMatrix[row][c];
                            }
                        }
                        int minInCol = INF;
                        for (int r = 0; r < cityCount; r++) {
                            if (r != row && currentMatrix[r][col] < minInCol) {
                                minInCol = currentMatrix[r][col];
                            }
                        }
                        int penalty = (minInRow >= INF || minInCol >= INF) ? INF : (minInRow + minInCol);
                        if (penalty > maxPenalty || bestZeroRow == -1) {
                            maxPenalty = penalty;
                            bestZeroRow = row;
                            bestZeroCol = col;
                        }
                    }
                }
            }

            if (bestZeroRow == -1) {
                continue;
            }

            int[][] excludedMatrix = new int[cityCount][cityCount];
            for (int r = 0; r < cityCount; r++) {
                excludedMatrix[r] = Arrays.copyOf(currentMatrix[r], cityCount);
            }
            excludedMatrix[bestZeroRow][bestZeroCol] = INF;

            int excludedReduction = 0;
            for (int row = 0; row < cityCount; row++) {
                int rowMin = INF;
                for (int col = 0; col < cityCount; col++) {
                    if (excludedMatrix[row][col] < rowMin) {
                        rowMin = excludedMatrix[row][col];
                    }
                }
                if (rowMin > 0 && rowMin < INF) {
                    excludedReduction += rowMin;
                    for (int col = 0; col < cityCount; col++) {
                        if (excludedMatrix[row][col] < INF) {
                            excludedMatrix[row][col] -= rowMin;
                        }
                    }
                }
            }
            for (int col = 0; col < cityCount; col++) {
                int colMin = INF;
                for (int row = 0; row < cityCount; row++) {
                    if (excludedMatrix[row][col] < colMin) {
                        colMin = excludedMatrix[row][col];
                    }
                }
                if (colMin > 0 && colMin < INF) {
                    excludedReduction += colMin;
                    for (int row = 0; row < cityCount; row++) {
                        if (excludedMatrix[row][col] < INF) {
                            excludedMatrix[row][col] -= colMin;
                        }
                    }
                }
            }

            int excludedBound = (excludedReduction >= INF) ? INF : currentBound + excludedReduction;
            if (excludedBound < bestCost) {
                matrixStack.add(excludedMatrix);
                boundStack.add(excludedBound);
                includedStack.add(new ArrayList<>(includedEdges));
            }

            int[][] includedMatrix = new int[cityCount][cityCount];
            for (int r = 0; r < cityCount; r++) {
                includedMatrix[r] = Arrays.copyOf(currentMatrix[r], cityCount);
            }
            for (int k = 0; k < cityCount; k++) {
                includedMatrix[bestZeroRow][k] = INF;
                includedMatrix[k][bestZeroCol] = INF;
            }
            includedMatrix[bestZeroCol][bestZeroRow] = INF;

            List<int[]> nextIncludedEdges = new ArrayList<>(includedEdges);
            nextIncludedEdges.add(new int[]{bestZeroRow, bestZeroCol});

            int[] nextCity = new int[cityCount];
            int[] prevCity = new int[cityCount];
            Arrays.fill(nextCity, -1);
            Arrays.fill(prevCity, -1);
            for (int[] edge : nextIncludedEdges) {
                nextCity[edge[0]] = edge[1];
                prevCity[edge[1]] = edge[0];
            }
            for (int[] edge : nextIncludedEdges) {
                int chainStart = edge[0];
                while (prevCity[chainStart] != -1 && prevCity[chainStart] != edge[1]) {
                    chainStart = prevCity[chainStart];
                }
                int chainEnd = edge[1];
                while (nextCity[chainEnd] != -1 && nextCity[chainEnd] != chainStart) {
                    chainEnd = nextCity[chainEnd];
                }
                int chainLength = 1;
                int walker = chainStart;
                while (nextCity[walker] != -1 && walker != chainEnd) {
                    walker = nextCity[walker];
                    chainLength++;
                }
                if (chainLength < cityCount && nextCity[chainEnd] == -1) {
                    includedMatrix[chainEnd][chainStart] = INF;
                }
            }

            int includedReduction = 0;
            for (int row = 0; row < cityCount; row++) {
                int rowMin = INF;
                for (int col = 0; col < cityCount; col++) {
                    if (includedMatrix[row][col] < rowMin) {
                        rowMin = includedMatrix[row][col];
                    }
                }
                if (rowMin > 0 && rowMin < INF) {
                    includedReduction += rowMin;
                    for (int col = 0; col < cityCount; col++) {
                        if (includedMatrix[row][col] < INF) {
                            includedMatrix[row][col] -= rowMin;
                        }
                    }
                }
            }
            for (int col = 0; col < cityCount; col++) {
                int colMin = INF;
                for (int row = 0; row < cityCount; row++) {
                    if (includedMatrix[row][col] < colMin) {
                        colMin = includedMatrix[row][col];
                    }
                }
                if (colMin > 0 && colMin < INF) {
                    includedReduction += colMin;
                    for (int row = 0; row < cityCount; row++) {
                        if (includedMatrix[row][col] < INF) {
                            includedMatrix[row][col] -= colMin;
                        }
                    }
                }
            }

            int includedBound = (includedReduction >= INF) ? INF : currentBound + includedReduction;
            if (includedBound < bestCost) {
                matrixStack.add(includedMatrix);
                boundStack.add(includedBound);
                includedStack.add(nextIncludedEdges);
            }
        }

        if (bestCost >= INF || bestEdges.size() != cityCount) {
            return "Маршрут не найден";
        }

        int[] nextCity = new int[cityCount];
        Arrays.fill(nextCity, -1);
        for (int[] edge : bestEdges) {
            nextCity[edge[0]] = edge[1];
        }

        List<Integer> route = new ArrayList<>();
        int city = 0;
        for (int step = 0; step < cityCount; step++) {
            route.add(city);
            city = nextCity[city];
        }
        route.add(0);

        return "Маршрут: " + route + " | Длина пути: " + bestCost;
    }

    // Задание 4: Реализовать решение задачи TSP методом ограниченного перебора при обходе графа в глубину
    static String boundedDepthFirstSearch(int[][] distanceMatrix, int startCity) {
        int cityCount = distanceMatrix.length;
        boolean[] visitedCities = new boolean[cityCount];
        int[] path = new int[cityCount];
        int[] pathCosts = new int[cityCount];
        int[] nextCandidateToTry = new int[cityCount];

        int depth = 0;
        path[depth] = startCity;
        pathCosts[depth] = 0;
        nextCandidateToTry[depth] = 0;
        visitedCities[startCity] = true;

        int bestCost = INF;
        List<Integer> bestRoute = new ArrayList<>();

        while (depth >= 0) {
            int currentCity = path[depth];
            int currentCost = pathCosts[depth];

            if (currentCost >= bestCost) {
                visitedCities[currentCity] = false;
                depth--;
                continue;
            }

            if (depth == cityCount - 1) {
                int returnCost = distanceMatrix[currentCity][startCity];
                if (returnCost < INF && currentCost + returnCost < bestCost) {
                    bestCost = currentCost + returnCost;
                    bestRoute.clear();
                    for (int i = 0; i <= depth; i++) {
                        bestRoute.add(path[i]);
                    }
                    bestRoute.add(startCity);
                }
                visitedCities[currentCity] = false;
                depth--;
                continue;
            }

            int nextCity = -1;
            for (int candidate = nextCandidateToTry[depth]; candidate < cityCount; candidate++) {
                nextCandidateToTry[depth] = candidate + 1;
                if (!visitedCities[candidate] && distanceMatrix[currentCity][candidate] < INF
                        && currentCost + distanceMatrix[currentCity][candidate] < bestCost) {
                    nextCity = candidate;
                    break;
                }
            }

            if (nextCity != -1) {
                depth++;
                path[depth] = nextCity;
                pathCosts[depth] = currentCost + distanceMatrix[currentCity][nextCity];
                nextCandidateToTry[depth] = 0;
                visitedCities[nextCity] = true;
            } else {
                visitedCities[currentCity] = false;
                depth--;
            }
        }

        if (bestCost >= INF || bestRoute.isEmpty()) {
            return "Маршрут не найден";
        }
        return "Маршрут: " + bestRoute + " | Длина пути: " + bestCost;
    }

    // Задание 5: Реализовать метод отжига для решения задачи TSP
    static String simulatedAnnealing(int[][] distanceMatrix) {
        int cityCount = distanceMatrix.length;
        Random random = new Random(42L);

        List<Integer> currentOrder = new ArrayList<>();
        for (int city = 0; city < cityCount; city++) {
            currentOrder.add(city);
        }
        Collections.shuffle(currentOrder, random);

        int currentCost = 0;
        for (int i = 0; i < cityCount; i++) {
            int fromCity = currentOrder.get(i);
            int toCity = currentOrder.get((i + 1) % cityCount);
            currentCost += distanceMatrix[fromCity][toCity];
        }

        List<Integer> bestOrder = new ArrayList<>(currentOrder);
        int bestCost = currentCost;

        double temperature = 100.0;
        double finalTemperature = 0.01;
        double coolingRate = 0.95;

        while (temperature > finalTemperature) {
            for (int iter = 0; iter < 200; iter++) {
                int firstIndex = random.nextInt(cityCount);
                int secondIndex = random.nextInt(cityCount);
                if (firstIndex == secondIndex) {
                    continue;
                }

                List<Integer> nextOrder = new ArrayList<>(currentOrder);
                Collections.swap(nextOrder, firstIndex, secondIndex);

                int nextCost = 0;
                for (int i = 0; i < cityCount; i++) {
                    int fromCity = nextOrder.get(i);
                    int toCity = nextOrder.get((i + 1) % cityCount);
                    nextCost += distanceMatrix[fromCity][toCity];
                }

                int delta = nextCost - currentCost;
                if (delta <= 0 || random.nextDouble() < Math.exp(-delta / temperature)) {
                    currentOrder = nextOrder;
                    currentCost = nextCost;
                    if (currentCost < bestCost) {
                        bestCost = currentCost;
                        bestOrder = new ArrayList<>(currentOrder);
                    }
                }
            }
            temperature *= coolingRate;
        }

        List<Integer> bestRoute = new ArrayList<>(bestOrder);
        bestRoute.add(bestOrder.get(0));
        return "Маршрут: " + bestRoute + " | Длина пути: " + bestCost;
    }

    // Задание 6: Реализовать метод генетической эволюции для решения задачи TSP
    static String geneticAlgorithm(int[][] distanceMatrix) {
        int cityCount = distanceMatrix.length;
        Random random = new Random(42L);

        int populationSize = 60;
        int generations = 150;
        double mutationRate = 0.25;

        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Integer> individual = new ArrayList<>();
            for (int city = 0; city < cityCount; city++) {
                individual.add(city);
            }
            Collections.shuffle(individual, random);
            population.add(individual);
        }

        List<Integer> bestChromosome = new ArrayList<>(population.get(0));
        int bestCost = INF;

        for (int gen = 0; gen < generations; gen++) {
            int[] costs = new int[populationSize];
            for (int i = 0; i < populationSize; i++) {
                List<Integer> individual = population.get(i);
                int cost = 0;
                for (int j = 0; j < cityCount; j++) {
                    cost += distanceMatrix[individual.get(j)][individual.get((j + 1) % cityCount)];
                }
                costs[i] = cost;
                if (cost < bestCost) {
                    bestCost = cost;
                    bestChromosome = new ArrayList<>(individual);
                }
            }

            List<List<Integer>> nextPopulation = new ArrayList<>();
            nextPopulation.add(new ArrayList<>(bestChromosome));

            while (nextPopulation.size() < populationSize) {
                int p1 = random.nextInt(populationSize);
                int p1Challenger = random.nextInt(populationSize);
                if (costs[p1Challenger] < costs[p1]) {
                    p1 = p1Challenger;
                }

                int p2 = random.nextInt(populationSize);
                int p2Challenger = random.nextInt(populationSize);
                if (costs[p2Challenger] < costs[p2]) {
                    p2 = p2Challenger;
                }

                List<Integer> parentOne = population.get(p1);
                List<Integer> parentTwo = population.get(p2);

                int left = random.nextInt(cityCount);
                int right = random.nextInt(cityCount);
                if (left > right) {
                    int temp = left;
                    left = right;
                    right = temp;
                }

                List<Integer> child = new ArrayList<>(Collections.nCopies(cityCount, -1));
                boolean[] used = new boolean[cityCount];
                for (int idx = left; idx <= right; idx++) {
                    int city = parentOne.get(idx);
                    child.set(idx, city);
                    used[city] = true;
                }

                int writePos = (right + 1) % cityCount;
                for (int offset = 0; offset < cityCount; offset++) {
                    int candidate = parentTwo.get((right + 1 + offset) % cityCount);
                    if (!used[candidate]) {
                        child.set(writePos, candidate);
                        used[candidate] = true;
                        writePos = (writePos + 1) % cityCount;
                    }
                }

                if (random.nextDouble() < mutationRate) {
                    int firstIdx = random.nextInt(cityCount);
                    int secondIdx = random.nextInt(cityCount);
                    Collections.swap(child, firstIdx, secondIdx);
                }

                nextPopulation.add(child);
            }

            population = nextPopulation;
        }

        for (List<Integer> individual : population) {
            int cost = 0;
            for (int j = 0; j < cityCount; j++) {
                cost += distanceMatrix[individual.get(j)][individual.get((j + 1) % cityCount)];
            }
            if (cost < bestCost) {
                bestCost = cost;
                bestChromosome = new ArrayList<>(individual);
            }
        }

        List<Integer> bestRoute = new ArrayList<>(bestChromosome);
        bestRoute.add(bestChromosome.get(0));
        return "Маршрут: " + bestRoute + " | Длина пути: " + bestCost;
    }

    public static void main(String[] args) {
        int[][] distanceMatrix = {
                {0,  20, 42, 35, 28},
                {20, 0,  30, 34, 15},
                {42, 30, 0,  12, 25},
                {35, 34, 12, 0,  18},
                {28, 15, 25, 18, 0}
        };

        int startCity = 0;

        // Задание 1: Метод ближнего соседа
        System.out.println("1. Метод ближнего соседа -> " + nearestNeighbor(distanceMatrix, startCity));

        // Задание 2: Метод последовательного включения
        System.out.println("2. Метод последовательного включения -> " + sequentialInsertion(distanceMatrix, startCity));

        // Задание 3: Метод Литтла
        System.out.println("3. Метод Литтла -> " + littleMethod(distanceMatrix));

        // Задание 4: Ограниченный перебор при обходе графа в глубину
        System.out.println("4. Ограниченный перебор (DFS) -> " + boundedDepthFirstSearch(distanceMatrix, startCity));

        // Задание 5: Метод отжига
        System.out.println("5. Метод отжига -> " + simulatedAnnealing(distanceMatrix));

        // Задание 6: Метод генетической эволюции
        System.out.println("6. Метод генетической эволюции -> " + geneticAlgorithm(distanceMatrix));
    }
}