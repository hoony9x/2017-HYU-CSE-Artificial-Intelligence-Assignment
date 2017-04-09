/*
 * Assignment2
 *
 * Version 1.0.0
 *
 * Copyright 컴퓨터전공 2013012289 한기훈
 */

import java.io.FileNotFoundException;
import java.util.Random;
import java.lang.Math;
import java.io.PrintWriter;

/* 두 번째 과제에 대한 구현입니다. */
public class Assignment2 {

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: java Assignment2 [N 값] [Output 경로]");
            return;
        }

        Integer N = Integer.parseInt(args[0]); /* N */
        String output_path = args[1]; /* output파일이 위치할 경로 */
        PrintWriter output_printwriter;

        /* Exception Handling이 필수인 코드라 다음과 같이 작성함. */
        try {
            /* 파일 열기, Windows 와 Linux 의 path 표현방식 차이로 인해 System.getProperty("file.separator") 사용. */
            output_printwriter = new PrintWriter(output_path + System.getProperty("file.separator") + "result" + N.toString() + ".txt");
        } catch(FileNotFoundException error) {
            System.err.println("FileNotFoundException");
            return;
        }

        /* 체스판의 상태를 나타낸다. */
        int[] Grid = Assignment2.gridGenerator(N);

        /* Global optimal solotion 을 찾지 못했을 경우 현재까지 구한 것 중 제일 global optima 에 근접한 답을 출력하기 위해 지정한 변수. */
        int[] min_state_grid = Grid.clone();
        int min_heuristic_value = -1;

        output_printwriter.println(">Hill Climbing");
        long start_time = System.nanoTime(); /* 시작 시간 기록 */

        /* Local optima 에 걸렸을 때 몇 번이나 다시 시도할지 지정. (N * 10 으로 지정함) */
        int maximum_attempt_count = N * 10;
        while(maximum_attempt_count > 0) {
            /* 현재 판의 상태에 대한 heuristic_value를 구한다. */
            int heuristic_value = Assignment2.heuristicCalculator(Grid, N);
            if(heuristic_value == 0) {
                /* Global optima인 경우이다. */
                min_heuristic_value = heuristic_value;
                min_state_grid = Grid.clone();
                break;
            }

            /* 더 좋은 상태를 찾기 위한 준비 */
            int[] next_grid = Grid.clone();
            int next_heuristic_value = heuristic_value;

            /* 현재 상태에서 가능한 모든 다음 상태에 대해서 heuristic value를 구한 후 테스트. */
            for(int i = 0; i < N; i++) {
                int[] temp_grid = Grid.clone();
                for(int j = 0; j < N; j++) {
                    temp_grid[i] = j;
                    /* 다음 가능한 상태에 대해서 heuristic_value를 구한다. */
                    int temp_heuristic_value = Assignment2.heuristicCalculator(temp_grid, N);

                    /* 다음 가능한 상태에 대해 구한 heuristic_value가 현재 값보다 좋을 경우 이 상태를 next state로 지정한다. */
                    if(temp_heuristic_value < next_heuristic_value) {
                        next_heuristic_value = temp_heuristic_value;
                        next_grid = temp_grid.clone();
                    }
                }
            }

            if(next_heuristic_value == heuristic_value) {
                /* Local optima에 빠진 경우이다. (platue 나 shoulder 일 수도 있지만 고려하지 않았다) */
                maximum_attempt_count--; /* 재시도 횟수 카운트 1 감소 */
                Grid = Assignment2.gridGenerator(N); /* 새로운 판의 상태를 생성 */

                /* Global optima를 구하지 못할 경우, 최대한 근접한 local optima 결과를 대신 출력하기 위한 것 */
                if(min_heuristic_value == -1 || min_heuristic_value > next_heuristic_value) {
                    min_heuristic_value = next_heuristic_value;
                    min_state_grid = next_grid.clone();
                }
            }
            else {
                /* Steepest next state 선택 후 계속 진행 */
                Grid = next_grid.clone();
            }
        }

        long end_time = System.nanoTime(); /* 끝 시간 기록 */
        for(int i = 0; i < N; i++) {
            output_printwriter.printf("%d ", min_state_grid[i]);
        }
        output_printwriter.println();
        if(min_heuristic_value != 0) {
            /* Global optima 를 구하지 못했을 경우. */
            output_printwriter.println("This is not a global optimal solution!");
            output_printwriter.printf("Heuristic value of this state is %d.\n", min_heuristic_value);
        }
        output_printwriter.printf("Total Elapsed Time: %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
        output_printwriter.close();
    }

    /* 체스판을 비우고 무작위 배치한다. (초기화 작업) */
    private static int[] gridGenerator(int N) {
        /* 난수 생성기 객체 할당 */
        Random rand_num_generator = new Random();

        /* 새로 생성하는 초기 상태의 체스판. (각 Column 에는 하나의 Queen 만 존재하므로 1D Array 를 사용 */
        int[] Grid = new int[N];
        for(int i = 0; i < N; i++) {
            Grid[i] = rand_num_generator.nextInt(N);
        }

        return Grid;
    }

    /* Heuristic Function -> 서로 다른 Queen 끼리 공격 가능한 경우를 Count 한다. 단, 순서는 고려하지 않음. */
    /* (A -> B 와 B -> A 는 같은 경우로 본다) */
    private static int heuristicCalculator(int[] Grid, int N) {
        int result = 0;

        for(int i = 0; i < N; i++) {
            for(int j = i + 1; j < N; j++) {
                if(Grid[i] == Grid[j]) {
                    /* Queen이 같은 row에 존재 */
                    result += 1;
                }
                else if((j - i) == Math.abs(Grid[i] - Grid[j])) {
                    /* Queen이 같은 대각선 상에 존재 */
                    result += 1;
                }
            }
        }

        return result;
    }

}

