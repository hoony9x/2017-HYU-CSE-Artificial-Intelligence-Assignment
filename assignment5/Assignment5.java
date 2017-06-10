/*
 * Assignment5
 *
 * Version 1.0.0
 *
 * Copyright 컴퓨터전공 2013012289 한기훈
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Assignment5 {

    private static PrintWriter output_printwriter; /* 파일 입출력 Object */
    private static Random generator; /* Random number generator */

    public static void main(String args[]) {
        if(args.length != 2) {
            System.err.println("Usage: java Assignment5 [N 값] [Output 경로]");
            return;
        }

        int N = Integer.parseInt(args[0]); /* N */
        String output_path = args[1]; /* output파일이 위치할 경로 */

        /* Exception Handling이 필수인 코드라 다음과 같이 작성함. */
        try {
            /* 파일 열기, Windows 와 Linux 의 path 표현방식 차이로 인해 System.getProperty("file.separator") 사용. */
            Assignment5.output_printwriter = new PrintWriter(output_path + System.getProperty("file.separator") + "result" + ((Integer)N).toString() + ".txt");
        } catch(FileNotFoundException error) {
            System.err.println("FileNotFoundException");
            return;
        }

        Assignment5.output_printwriter.println(">Genetic Algorithm");

        if(N == 1) {
            /* 어차피 가능한 경우가 하나밖에 없음. */
            Assignment5.output_printwriter.println("0");
        }
        else if(N == 2 || N == 3) {
            /* N 이 2나 3인 경우는 답을 낼 수 없으므로 직접 예외처리. */
            Assignment5.output_printwriter.println("No solution");
        }
        else {
            long start_time = System.nanoTime(); /* 시작 시간 기록 */

            Assignment5.generator = new Random(); /* Random number 생성을 위해 객체 할당. */
            Assignment5.GeneticAlgorithm(N);

            long end_time = System.nanoTime(); /* 끝 시간 기록 */

            Assignment5.output_printwriter.printf("Total Elapsed Time: %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
        }

        Assignment5.output_printwriter.close();
    }

    /* Genetic Algorithm 을 이용하여 적절한 Queen의 배열이 나올 때까지 반복, 답을 찾으면 출력 후 종료. */
    private static void GeneticAlgorithm(int N) {
        List<CustomPair> parents = new ArrayList<>(); /* Queen 배치 상태를 담을 List 이다. */

        for(int i = 0; i < N * (N + 2); i++) {
            /* 처음에는 N * (N + 2) 개의 Queen List 를 완전 Random 으로 생성한다. */
            parents.add(Assignment5.RandomGenerate(N));
        }

        while(true) {
            /* 살아있는 Queen 의 갯수를 기준으로 내림차순 정렬. */
            parents.sort(new CompareDescending());

            if(parents.get(0).alive_count == N) {
                /* 답을 찾은 경우이므로 해당 답을 출력 후 while loop 를 탈출한다. */
                for(int i = 0; i < N; i++) {
                    Assignment5.output_printwriter.printf("%d ", parents.get(0).queen_list[i]);
                }
                Assignment5.output_printwriter.println();

                break;
            }
            else {
                while(parents.size() > N) {
                    /* 결과가 좋은 N 개의 부모를 남기고 나머지를 전부 없앤다. */
                    parents.remove(parents.size() - 1);
                }

                for(int i = 0; i < N; i++) {
                    for(int j = 0; j < N; j++) {
                        /* N * N 개의 자식을 CrossOver 를 이용하여 생성. */
                        CustomPair child = new CustomPair();
                        child.queen_list = Assignment5.CrossOver(N, parents.get(i).queen_list, parents.get(j).queen_list);
                        child.alive_count = Assignment5.GetAliveCountOfQueens(N, child.queen_list);
                        parents.add(child);
                    }
                }

                for(int i = 0; i < N; i++) {
                    /* N 개의 자식을 Mutation 을 이용하여 생성. */
                    CustomPair child = new CustomPair();
                    child.queen_list = Assignment5.Mutation(N, parents.get(i).queen_list);
                    child.alive_count = Assignment5.GetAliveCountOfQueens(N, child.queen_list);
                    parents.add(child);
                }
            }
        }
    }

    /* 살아있는 Queen 의 갯수를 구한다. */
    private static int GetAliveCountOfQueens(int N, int[] queen_list) {
        int count_total = N;

        for(int i = 0; i < N; i++) {
            for(int j = i + 1; j < N; j++) {
                if(queen_list[i] == queen_list[j] || (j - i) == Math.abs(queen_list[i] - queen_list[j])) {
                    /* Queen이 같은 row에 존재 or Queen이 같은 대각선 상에 존재 */
                    count_total--;
                    break;
                }
            }
        }

        return count_total;
    }

    /* Queen 의 배치 상태를 무작위로 생성한다. */
    private static CustomPair RandomGenerate(int N) {
        CustomPair parent = new CustomPair();
        parent.queen_list = new int[N];

        for(int j = 0; j < N; j++) {
            int position = Assignment5.generator.nextInt(N);
            parent.queen_list[j] = position;
        }

        parent.alive_count = Assignment5.GetAliveCountOfQueens(N, parent.queen_list);

        return parent;
    }

    /* CrossOver 를 이용하여 Queen 의 배치를 새로 생성한다. */
    private static int[] CrossOver(int N, int[] parent1, int[] parent2) {
        int[] child = new int[N];

        for(int i = 0; i < N; i++) {
            /* 2개의 부모 중 어떤 부모에게서 data를 가져올 지 선택. */
            boolean select_parent1 = Assignment5.generator.nextBoolean();

            if(select_parent1) {
                child[i] = parent1[i];
            }
            else {
                child[i] = parent2[i];
            }
        }

        return child;
    }

    /* Mutation 을 이용하여 Queen 의 배치를 새로 생성한다. */
    private static int[] Mutation(int N, int[] parent) {
        int[] child = new int[N];

        for(int i = 0; i < N; i++) {
            /* 각각의 배치에 대해서 Mutation 여부를 결정. */
            boolean is_mutated = Assignment5.generator.nextBoolean();

            if(is_mutated) {
                /* Mutation 결정 시 새로운 번호를 생성. */
                child[i] = Assignment5.generator.nextInt(N);
            }
            else {
                /* Mutation 아닐 경우 부모의 정보를 그대로 전달. */
                child[i] = parent[i];
            }
        }

        return child;
    }
}

