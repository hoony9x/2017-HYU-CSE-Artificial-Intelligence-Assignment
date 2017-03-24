/*
 * Assignment1
 *
 * Version 1.0.0
 *
 * Copyright 컴퓨터전공 2013012289 한기훈
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Math;
import java.io.PrintWriter;

/* 첫 번째 과제에 대한 구현입니다. */
public class Assignment1 {

    private static boolean is_finish; /* 각 Search에서 끝났을 때의 경우를 체크하기 위한 것. */
    private static PrintWriter output_printwriter; /* 파일 출력을 위한 PrintWriter Object */

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: java Assignment1 [N 값] [Output 경로]");
            return;
        }

        Integer N = Integer.parseInt(args[0]); /* N */
        String output_path = args[1]; /* output파일이 위치할 경로 */

        /* Exception Handling이 필수인 코드라 다음과 같이 작성함. */
        try {
            /* 파일 열기 */
            output_printwriter = new PrintWriter(output_path + "/result" + N.toString() + ".txt");
        } catch(FileNotFoundException error) {
            System.err.println("FileNotFoundException");
            return;
        }

        /* DFS 탐색 수행 (Backtracking, using recursive function) */
        {
            Assignment1.is_finish = false;
            output_printwriter.println(">DFS");
            long start_time = System.nanoTime(); /* 시작 시간 기록 */

            /* 현재까지 지나온 점들을 담는다. */
            ArrayList<Integer> checklist = new ArrayList<Integer>();

            /* 첫 번째 Column에서의 Row 번호를 0 부터 N - 1 까지 순서대로 지정하여 탐색 */
            for(int i = 0; i < N; i++) {
                Integer init_Integer = i;

                checklist.add(init_Integer); /* 첫 번째 Column 에서의 row번호를 넣는다. */
                Assignment1.searchByDFS(N, checklist); /* 다음 Column으로 이동하여 탐색 */
                checklist.remove(init_Integer); /* 첫 번째 Column에 넣었던 row번호를 제거한다. */
            }

            /* 탐색에서 Solution을 찾지 못한 경우. */
            if(!Assignment1.is_finish) {
                output_printwriter.println("No solution");
            }

            long end_time = System.nanoTime(); /* 끝 시간 기록 */
            output_printwriter.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
            output_printwriter.println(""); /* 빈 줄 삽입 */
        }

        /* BFS 탐색 수행 */
        {
            Assignment1.is_finish = false;
            output_printwriter.println(">BFS");
            long start_time = System.nanoTime(); /* 시작 시간 기록 */

            /* BFS 탐색을 시작한다. */
            Assignment1.searchByBFS(N);

            /* 탐색에서 Solution을 찾지 못한 경우. */
            if(!Assignment1.is_finish) {
                output_printwriter.println("No solution");
            }

            long end_time = System.nanoTime(); /* 끝 시간 기록 */
            output_printwriter.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); /* 시간 출력 */
            output_printwriter.println(""); /* 빈 줄 삽입 */
        }

        /* DFID 탐색 수행 (Backtracking, using recursive function) */
        {
            /*
             * DFS탐색의 search depth limit 를 1 씩 늘려나가는 것이라 사실상 DFS랑 구조와 유사.
             * 그래서 DFS의 코드를 거의 그대로 가져옴.
             */
            Assignment1.is_finish = false;
            output_printwriter.println(">DFID");
            long start_time = System.nanoTime(); // 시작 시간 기록

            /* search depth limit 을 N 까지 늘려나가면서 탐색한다. */
            for(int depth_limit = 0; depth_limit <= N; depth_limit++) {
                ArrayList<Integer> checklist = new ArrayList<Integer>(); // 현재까지 지나온 점들을 담는다.
                for(int i = 0; i < N; i++) {
                    Integer init_Integer = i;

                    checklist.add(init_Integer);
                    Assignment1.searchByDFID(N, depth_limit, checklist);
                    checklist.remove(init_Integer);
                }
            }

            /* 탐색에서 Solution을 찾지 못한 경우. */
            if(!Assignment1.is_finish) {
                output_printwriter.println("No solution");
            }

            long end_time = System.nanoTime(); //끝 시간 기록
            output_printwriter.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); // 시간 출력
            output_printwriter.println(""); // 빈 줄 삽입
        }

        output_printwriter.close();
    }

    private static void searchByDFS(int N, ArrayList<Integer> checklist) {
        if(Assignment1.is_finish) {
            /*
             * 원하는 답을 찾았을 경우 DFS탐색을 중지하기 위해 삽입한 코드.
             * 원하는 답을 찾으면 Math.is_finish 가 true 로 set 되기 때문에 이를 체크하는 것.
             */
            return;
        }

        /* 리스트에 Queen 이 N 개가 모이면 이들이 조건을 만족하는지 확인한다. (서로 공격 불가능한 위치인지 확인) */
        if(checklist.size() == N) {
            /* Queen 들이 서로 공격 불가능한 위치인지 테스트 */
            if(Assignment1.isQueensAreAlive(checklist)) {
                /* 조건 테스트를 통과하면 각 Column 에 있는 Queen의 Row 번호를 출력한다. */
                output_printwriter.printf("Location : ");
                for (Integer P: checklist) {
                    output_printwriter.printf("%d ", P);
                }
                output_printwriter.printf("\n");

                /* true로 세팅하여 더 이상의 DFS탐색이 수행되지 않도록 한다. */
                Assignment1.is_finish = true;
            }

            /* 정답이 아니면 다음 대상으로 진행될 것이고, 정답인 경우라면 여기서 끝날 것이다. */
            return;
        }

        for(int i = 0; i < N; i++) {
            /*
             * checklist에는 현재 checklist.size() - 1 개의 row 번호가 들어있다.
             * 따라서 새로운 row 번호를 넣을 position 값은 checklist.size() 가 된다.
             */
            checklist.add(checklist.size(), i); /* 다음 Column 에서의 row번호를 넣는다. */
            Assignment1.searchByDFS(N, checklist); /* 다음 Column으로 이동하여 탐색 */
            checklist.remove(checklist.size() - 1); /* Column에 넣었던 row번호를 제거한다. */
        }
    }

    private static void searchByBFS(int N) {

        /* "각 Column 에서 뽑은 row 번호를 저장한 리스트" 를 저장할 Queue 를 선언한다. */
        Queue< ArrayList<Integer> > Q = new LinkedList< ArrayList<Integer> >();

        /* 아래 코드는 첫 번째(0 번째) Column 에 있는 row 번호를 하나씩 list에 넣은 후 이들을 Queue에 넣는 과정을 나타낸 것이다. */
        for(int i = 0; i < N; i++) {
            /*
             * 새로운 list를 생성하고 첫 번째(0 번째) Column 에 있는 row 번호를 하나 넣어준다.
             */
            ArrayList<Integer> init_list = new ArrayList<Integer>();
            init_list.add(0, i);

            /* 위에서 생성한 리스트를 Queue에 넣는다. */
            Q.offer(init_list);
        }

        /* Queue 가 비거나 정답을 찾을 때까지 반복 */
        while(!Q.isEmpty()) {
            /* 각 Column 별 row 번호가 담긴 list 를 뽑는다. */
            ArrayList<Integer> recent_list = Q.poll();

            /* 뽑은 list 에 이미 N 개 만큼의 row 번호가 있다면 정답인지를 확인한다. */
            if(recent_list.size() == N) {
                /* Queen 들이 서로 공격 불가능한 위치인지 테스트 */
                if(Assignment1.isQueensAreAlive(recent_list)) {
                    /* 조건 테스트를 통과하면 각 Column 에 있는 Queen의 Row 번호를 출력한다. */
                    output_printwriter.printf("Location : ");
                    for (Integer P: recent_list) {
                        output_printwriter.printf("%d ", P);
                    }
                    output_printwriter.printf("\n");

                    /* true로 세팅하여 'No solution' 이라는 메세지가 출력되지 않도록 한다. */
                    Assignment1.is_finish = true;

                    /* BFS 탐색을 종료한다. */
                    return;
                }

                /* 정답이 아니면 다음 대상으로 진행 */
                continue;
            }

            for(int i = 0; i < N; i++) {
                /* 기존 리스트에 다음 Column 에서의 row 번호를 뽑아서 넣는다. */
                ArrayList<Integer> next_list = (ArrayList<Integer>)recent_list.clone();
                next_list.add(next_list.size(), i);

                /* Q 에 위의 리스트를 넣는다. */
                Q.offer(next_list);
            }
        }
    }

    private static void searchByDFID(int N, int depth_limit, ArrayList<Integer> checklist) {
        if(Assignment1.is_finish) {
            /*
             * 원하는 답을 찾았을 경우 DFID 탐색을 중지하기 위해 삽입한 코드.
             * 원하는 답을 찾으면 Math.is_finish 가 true 로 set 되기 때문에 이를 체크하는 것.
             */
            return;
        }

        /* 리스트에 Queen 이 N 개가 모이면 이들이 조건을 만족하는지 확인한다. (서로 공격 불가능한 위치인지 확인) */
        if(checklist.size() == N) {
            /* Queen 들이 서로 공격 불가능한 위치인지 테스트 */
            if(Assignment1.isQueensAreAlive(checklist)) {
                /* 조건 테스트를 통과하면 각 Column 에 있는 Queen의 Row 번호를 출력한다. */
                output_printwriter.printf("Location : ");
                for (Integer P: checklist) {
                    output_printwriter.printf("%d ", P);
                }
                output_printwriter.printf("\n");

                /* true로 세팅하여 더 이상의 DFID 탐색이 수행되지 않도록 한다. */
                Assignment1.is_finish = true;
            }

            /* 정답이 아니면 다음 대상으로 진행될 것이고, 정답인 경우라면 여기서 끝날 것이다. */
            return;
        }

        /* 현재 DFID 탐색에서 지정한 search depth limit 까지 도달했는지를 확인한다. */
        if(checklist.size() == depth_limit) {
            return;
        }

        for(int i = 0; i < N; i++) {
            /*
             * checklist에는 현재 checklist.size() - 1 개의 row 번호가 들어있다.
             * 따라서 새로운 row 번호를 넣을 position 값은 checklist.size() 가 된다.
             */
            checklist.add(checklist.size(), i); /* 다음 Column 에서의 row번호를 넣는다. */
            Assignment1.searchByDFID(N, depth_limit, checklist); /* 다음 Column으로 이동하여 탐색 */
            checklist.remove(checklist.size() - 1); /* Column에 넣었던 row번호를 제거한다. */
        }
    }

    /* Queen 들이 전부 살아있는지 테스트하는 Method */
    private static boolean isQueensAreAlive(ArrayList<Integer> checklist) {
        for(int i = 0; i < checklist.size(); i++) {
            for(int j = 0; j < checklist.size(); j++) {
                if(i == j) {
                    /*
                     * 애시당초 각 Column 에서 row 번호는 하나만 뽑고 다음 Column으로 넘어간다.
                     * 따라서 이 경우는 확인할 필요가 없다.
                     */
                    continue;
                }

                /* 서로 다른 Column 에서 뽑은 row 번호가 같은 경우임. */
                if(checklist.get(i).equals(checklist.get(j))) {
                    return false;
                }

                /* 서로 다른 Queen 2개가 Y = X + k 또는 Y = -X + k 축 상에 존재하는 경우임. */
                if(Math.abs(i - j) == Math.abs(checklist.get(i) - checklist.get(j))) {
                    return false;
                }
            }
        }

        /* 여기까지 왔다면 각 Queen 들은 서로 공격할 수 없는 위치인 것이다. */
        return true;
    }
}
