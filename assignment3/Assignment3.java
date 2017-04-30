/*
 * Assignment3
 *
 * Version 1.0.0
 *
 * Copyright 컴퓨터전공 2013012289 한기훈
 */

import java.io.FileNotFoundException;
import java.lang.Math;
import java.io.PrintWriter;

/* 세 번째 과제에 대한 구현입니다. */
public class Assignment3 {

    private static PrintWriter output_printwriter; /* 파일 입출력 Object */
    private static boolean is_success; /* 각 단계에서 Solution 찾았는지 여부를 확인하기 위한 것. */

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: java Assignment3 [N 값] [Output 경로]");
            return;
        }

        int N = Integer.parseInt(args[0]); /* N */
        String output_path = args[1]; /* output파일이 위치할 경로 */

        /* Exception Handling이 필수인 코드라 다음과 같이 작성함. */
        try {
            /* 파일 열기, Windows 와 Linux 의 path 표현방식 차이로 인해 System.getProperty("file.separator") 사용. */
            Assignment3.output_printwriter = new PrintWriter(output_path + System.getProperty("file.separator") + "result" + ((Integer)N).toString() + ".txt");
        } catch(FileNotFoundException error) {
            System.err.println("FileNotFoundException");
            return;
        }

        {
            Assignment3.output_printwriter.println(">Standard CSP");
            long start_time = System.nanoTime(); /* 시작 시간 기록 */
            Assignment3.is_success = false;

            /* 현재까지 지나온 점들을 담는다. */
            int[] checklist = new int[N];

            /* 첫 번째 Column에서의 Row 번호를 0 부터 N - 1 까지 순서대로 지정하여 탐색 */
            for(int i = 0; i < N; i++) {
                checklist[0] = i; /* 첫 번째 Column 에서의 row번호를 넣는다. */
                Assignment3.StandardCSP(N, checklist, 1); /* 다음 Column으로 이동하여 탐색 */

                if(Assignment3.is_success) {
                    break;
                }
            }

            long end_time = System.nanoTime(); /* 끝 시간 기록 */

            if(!Assignment3.is_success) {
                Assignment3.output_printwriter.println("No solution");
            }
            Assignment3.output_printwriter.printf("Total Elapsed Time: %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
        }

        Assignment3.output_printwriter.println();

        {
            Assignment3.output_printwriter.println(">CSP with Forward Checking");
            long start_time = System.nanoTime(); /* 시작 시간 기록 */
            Assignment3.is_success = false;

            /* N * N 의 게임판을 만든다. 그리고 초기 상태로 전부 0을 채운다. */
            int[][] grid = new int[N][N];
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    grid[i][j] = 0;
                }
            }

            /* 첫 번째(0) Column에서 각 row에 Queen을 놓고 다음 Column으로 진행한다. */
            for(int i = 0; i < N; i++) {
                Assignment3.updateGridState(grid, N, i, 0, true);
                Assignment3.CSPwithForwardChecking(grid, N, 1);
                Assignment3.updateGridState(grid, N, i, 0, false);
            }

            long end_time = System.nanoTime(); /* 끝 시간 기록 */

            if(!Assignment3.is_success) {
                Assignment3.output_printwriter.println("No solution");
            }
            Assignment3.output_printwriter.printf("Total Elapsed Time: %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
        }

        Assignment3.output_printwriter.println();

        {
            Assignment3.output_printwriter.println(">CSP with Arc Consistency");
            long start_time = System.nanoTime(); /* 시작 시간 기록 */
            Assignment3.is_success = false;

            /* N * N 의 게임판을 만든다. 그리고 초기 상태로 전부 0을 채운다. */
            int[][] grid = new int[N][N];
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    grid[i][j] = 0;
                }
            }

            /* 첫 번째(0) Column에서 각 row에 Queen을 놓고 다음 Column으로 진행한다. */
            for(int i = 0; i < N; i++) {
                Assignment3.updateGridState(grid, N, i, 0, true);
                Assignment3.CSPwithArcConsistency(grid, N, 1);
                Assignment3.updateGridState(grid, N, i, 0, false);
            }

            long end_time = System.nanoTime(); /* 끝 시간 기록 */

            if(!Assignment3.is_success) {
                Assignment3.output_printwriter.println("No solution");
            }
            Assignment3.output_printwriter.printf("Total Elapsed Time: %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
        }

        Assignment3.output_printwriter.close();
    }

    private static void StandardCSP(int N, int[] checklist, int step) {
        if(Assignment3.is_success) {
            /*
             * 원하는 답을 찾았을 경우 DFS탐색을 중지하기 위해 삽입한 코드.
             */
            return;
        }

        /* N 개의 Queen을 성공적으로 놓았을 경우 각 Column에서 Queen의 row번호를 출력한다. 그리고 종료. */
        if(step == N) {
            Assignment3.output_printwriter.printf("Location : ");
            for (int P: checklist) {
                Assignment3.output_printwriter.printf("%d ", P);
            }
            output_printwriter.println();

            Assignment3.is_success = true;

            return;
        }

        for(int i = 0; i < N; i++) {
            /* 새로운 row 번호를 넣을 position(Column) 값은 step에 들어간 값이 된다. */
            checklist[step] = i; /* 다음 Column 에서의 row번호를 넣는다. */
            if(Assignment3.isQueensAreAlive(checklist, N, step)) {
                /* 새 Queen을 놓은 row번호가 constraint를 만족하는 경우 다음 단계로 진행한다. */
                Assignment3.StandardCSP(N, checklist, step + 1); /* 다음 Column으로 이동하여 탐색 */
            }
        }
    }

    /* Queen 들이 전부 살아있는지 테스트하는 Method -> StandardCSP 전용 */
    private static boolean isQueensAreAlive(int[] checklist, int N, int step) {
        for(int i = 0; i <= step; i++) {
            for(int j = i + 1; j <= step; j++) {
                /* 서로 다른 Column 에서 뽑은 row 번호가 같은 경우임. */
                if(checklist[i] == checklist[j]) {
                    return false;
                }
                /* 서로 다른 Queen 2개가 Y = X + k 또는 Y = -X + k 축 상에 존재하는 경우임. */
                if(Math.abs(i - j) == Math.abs(checklist[i] - checklist[j])) {
                    return false;
                }
            }
        }
        /* 여기까지 왔다면 각 Queen 들은 서로 공격할 수 없는 위치인 것이다. */
        return true;
    }

    /* Queen을 놓을 때마다 Forward Checking을 진행한다. */
    private static void CSPwithForwardChecking(int[][] grid, int N, int step) {
        if(Assignment3.is_success) {
            return;
        }

        /* 성공적으로 N개의 Queen을 놓은 경우. */
        if(step == N) {
            Assignment3.output_printwriter.printf("Location : ");
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(grid[j][i] == 0) {
                        Assignment3.output_printwriter.printf("%d ", j);
                        break;
                    }
                }
            }

            output_printwriter.println();

            Assignment3.is_success = true;

            return;
        }

        /* 현재의 Column에서 grid 배열에서의 값이 0인 부분에 새로운 Queen을 놓는다. */
        for(int i = 0; i < N; i++) {
            if(grid[i][step] == 0) {
                updateGridState(grid, N, i, step, true);
                Assignment3.CSPwithForwardChecking(grid, N, step + 1);
                updateGridState(grid, N, i, step, false);
            }
        }
    }

    /* Forward Checking 과정은 동일하나 Queen을 놓기 전에 Arc Consistency를 확인한다. (Preprocessing 용도로 활용했다) */
    private static void CSPwithArcConsistency(int[][] grid, int N, int step) {
        if(Assignment3.is_success) {
            return;
        }

        /* 성공적으로 N개의 Queen을 놓은 경우. */
        if(step == N) {
            Assignment3.output_printwriter.printf("Location : ");
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                    if(grid[j][i] == 0) {
                        Assignment3.output_printwriter.printf("%d ", j);
                        break;
                    }
                }
            }

            output_printwriter.println();

            Assignment3.is_success = true;

            return;
        }

        /* grid배열에서 값이 0인 row에 Queen을 놓는 것은 동일하나, 놓기 전에 해당 칸과 이후의 Column간의 Arc Consistency를 확인한다. */
        for(int i = 0; i < N; i++) {
            if(grid[i][step] == 0 && Assignment3.checkArcConsistency(grid, N, step, i)) {
                /* Arc Consistency 까지 만족할 경우 다음 단계로 진행한다. */
                updateGridState(grid, N, i, step, true);
                Assignment3.CSPwithArcConsistency(grid, N, step + 1);
                updateGridState(grid, N, i, step, false);
            }
        }
    }

    /* Queen 을 배치/제거 후 판의 상태를 업데이트하는 함수. 숫자가 0인 칸이 Queen이 놓일 수 있는 칸이다. */
    private static void updateGridState(int[][] grid, int N, int row_num, int col_num, boolean is_add) {
        /*
         * row, col 번호를 중심으로 해당 중심을 제외하고 가로, 세로, 그리고 Y = (+/-)X 대각선의 값을 업데이트한다.
         * is_add == true 일 경우 업데이트 되는 칸의 값을 1 증가, is_add == false 일 경우 1 감소.
         * grid 배열에서 값이 0인 부분이 Queen을 놓을 수 있는 부분이 된다.
         */

        if(is_add) {
            for(int i = 0; i < N; i++) {
                grid[row_num][i] += 1;
                grid[i][col_num] += 1;
            }

            for(int i = -N + 1; i < N; i++) {
                int x = i + col_num;
                int y = i + row_num;

                if(x >= 0 && x < N && y >= 0 && y < N) {
                    grid[y][x] += 1;
                }
            }

            for(int i = -N + 1; i < N; i++) {
                int x = i + col_num;
                int y = -i + row_num;

                if(x >= 0 && x < N && y >= 0 && y < N) {
                    grid[y][x] += 1;
                }
            }

            grid[row_num][col_num] -= 4;
        }
        else {
            for(int i = 0; i < N; i++) {
                grid[row_num][i] -= 1;
                grid[i][col_num] -= 1;
            }

            for(int i = -N + 1; i < N; i++) {
                int x = i + col_num;
                int y = i + row_num;

                if(x >= 0 && x < N && y >= 0 && y < N) {
                    grid[y][x] -= 1;
                }
            }

            for(int i = -N + 1; i < N; i++) {
                int x = i + col_num;
                int y = -i + row_num;

                if(x >= 0 && x < N && y >= 0 && y < N) {
                    grid[y][x] -= 1;
                }
            }

            grid[row_num][col_num] += 4;
        }
    }

    /* 현재 주어진 칸(col_num, row_num 이 주어짐)과 이후의 Column간의 Arc Consistency 를 확인한다. */
    private static boolean checkArcConsistency(int[][] grid, int N, int col_num, int row_num) {
        /*
         * 현재의 column 과 row 를 기준으로 그 다음에 있는 column과의 consistency를 확인한다.
         * 주어진 칸에 Queen을 놓았을 때 이후의 Column 에서 Queen을 놓을 수 있는 경우가 없어지는 경우가 발생할 경우
         * Arc Consistency를 만족하지 않는 경우로 본다.
         * Consistency 만족 시 true 반환, 아닐 경우 false 반환.
         */

        for(int next_col = col_num + 1; next_col < N; next_col++) {
            boolean is_ok = false;
            for(int next_row = 0; next_row < N; next_row++) {
                if(grid[next_row][next_col] == 0) {
                    if(next_row != row_num && Math.abs(next_row - row_num) != Math.abs(next_col - col_num)) {
                        is_ok = true;
                        break;
                    }
                }
            }

            if(!is_ok) {
                return false;
            }
        }

        return true;
    }
}

