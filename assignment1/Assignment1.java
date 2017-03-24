/*
 * Assignment1
 *
 * Version info
 *
 * Copyright notice
 */


import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Math;

public class Assignment1 {

    private static boolean is_finish; // 각 Search에서 끝났을 때의 경우를 체크하기 위한 것.

    public static void main(String[] args) {
        int N = 8;

        // DFS 탐색 수행 (Backtracking, using recursive function)
        {
            Assignment1.is_finish = false;
            System.out.println(">DFS");
            long start_time = System.nanoTime(); // 시작 시간 기록

            ArrayList<Integer> checklist = new ArrayList<Integer>(); // 현재까지 지나온 점들을 담는다.
            for(int i = 0; i < N; i++) {
                Integer init_Integer = i;

                checklist.add(init_Integer);
                Assignment1.searchByDFS(N, checklist);
                checklist.remove(init_Integer);
            }

            // 탐색에서 Solution을 찾지 못한 경우.
            if(!Assignment1.is_finish) {
                System.out.println("No solution");
            }

            long end_time = System.nanoTime(); //끝 시간 기록
            System.out.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); // 시간 출력
            System.out.println(""); // 빈 줄 삽입
        }

        // BFS 탐색 수행
        {
            Assignment1.is_finish = false;
            System.out.println(">BFS");
            long start_time = System.nanoTime(); // 시작 시간 기록

            //Assignment1.searchByBFS(N);

            // 탐색에서 Solution을 찾지 못한 경우.
            if(!Assignment1.is_finish) {
                System.out.println("No solution");
            }

            long end_time = System.nanoTime(); //끝 시간 기록
            System.out.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); // 시간 출력
            System.out.println(""); // 빈 줄 삽입
        }

        // DFID 탐색 수행
        {
            Assignment1.is_finish = false;
            System.out.println(">DFID");
            long start_time = System.nanoTime(); // 시작 시간 기록

            for(int depth_limit = 0; depth_limit <= N; depth_limit++) {
                ArrayList<Integer> checklist = new ArrayList<Integer>(); // 현재까지 지나온 점들을 담는다.
                for(int i = 0; i < N; i++) {
                    Integer init_Integer = i;

                    checklist.add(init_Integer);
                    Assignment1.searchByDFID(N, depth_limit, checklist);
                    checklist.remove(init_Integer);
                }
            }

            // 탐색에서 Solution을 찾지 못한 경우.
            if(!Assignment1.is_finish) {
                System.out.println("No solution");
            }

            long end_time = System.nanoTime(); //끝 시간 기록
            System.out.printf("Time : %f\n", (double)(end_time - start_time) / 1000000000.0); // 시간 출력
            System.out.println(""); // 빈 줄 삽입
        }
    }

    private static void searchByDFS(int N, ArrayList<Integer> checklist) {
        if(Assignment1.is_finish) {
            // 원하는 답을 찾았을 경우 DFS탐색을 중지하기 위해 삽입한 코드.
            // 원하는 답을 찾으면 Math.is_finish 가 true 로 set 된다.
            return;
        }

        // 리스트에 Queen 이 8개가 모이면 이들이 조건을 만족하는지 확인한다. (서로 catch 불가능한 위치인지 확인)
        if(checklist.size() == N) {
            // Queen 들이 살아있는 위치인지 테스트
            if(Assignment1.isQueensAreAlive(checklist)) {
                // 조건 테스트를 통과하면 각 Column 에 있는 Queen의 Row 번호를 출력한다.
                System.out.printf("Location : ");
                for (Integer P: checklist) {
                    System.out.printf("%d ", P);
                }
                System.out.printf("\n");

                // true로 세팅하여 더 이상의 DFS탐색이 수행되지 않도록 한다.
                Assignment1.is_finish = true;
            }

            return;
        }

        for(int i = 0; i < N; i++) {
            checklist.add(checklist.size(), i);
            Assignment1.searchByDFS(N, checklist);
            checklist.remove(checklist.size() - 1);
        }
    }

    private static void searchByBFS(int N) {
        Queue< ArrayList<Integer> > Q = new LinkedList< ArrayList<Integer> >();
        for(int i = 0; i < N; i++) {
            ArrayList<Integer> init_list = new ArrayList<Integer>();
            init_list.add(0, i);

            Q.offer(init_list);
        }

        while(!Q.isEmpty()) {
            ArrayList<Integer> recent_list = Q.poll();

            if(recent_list.size() == N) {
                if(Assignment1.isQueensAreAlive(recent_list)) {
                    System.out.printf("Location : ");
                    for (Integer P: recent_list) {
                        System.out.printf("%d ", P);
                    }
                    System.out.printf("\n");

                    Assignment1.is_finish = true;

                    return;
                }

                continue;
            }

            for(int i = 0; i < N; i++) {
                ArrayList<Integer> next_list = (ArrayList<Integer>)recent_list.clone();
                next_list.add(next_list.size(), i);
                Q.offer(next_list);
            }
        }
    }

    private static void searchByDFID(int N, int depth_limit, ArrayList<Integer> checklist) {
        if(Assignment1.is_finish) {
            // 원하는 답을 찾았을 경우 DFS탐색을 중지하기 위해 삽입한 코드.
            // 원하는 답을 찾으면 Math.is_finish 가 true 로 set 된다.
            return;
        }

        // 리스트에 Queen 이 8개가 모이면 이들이 조건을 만족하는지 확인한다. (서로 catch 불가능한 위치인지 확인)
        if(checklist.size() == N) {
            // Queen 들이 살아있는 위치인지 테스트
            if(Assignment1.isQueensAreAlive(checklist)) {
                // 조건 테스트를 통과하면 각 Column 에 있는 Queen의 Row 번호를 출력한다.
                System.out.printf("Location : ");
                for (Integer P: checklist) {
                    System.out.printf("%d ", P);
                }
                System.out.printf("\n");

                // true로 세팅하여 더 이상의 DFS탐색이 수행되지 않도록 한다.
                Assignment1.is_finish = true;
            }

            return;
        }

        if(checklist.size() == depth_limit) {
            return;
        }

        for(int i = 0; i < N; i++) {
            checklist.add(checklist.size(), i);
            Assignment1.searchByDFS(N, checklist);
            checklist.remove(checklist.size() - 1);
        }
    }

    // Queen 들이 전부 살아있는지 테스트하는 Method
    private static boolean isQueensAreAlive(ArrayList<Integer> checklist) {
        for(int i = 0; i < checklist.size(); i++) {
            for(int j = 0; j < checklist.size(); j++) {
                if(i == j) {
                    continue;
                }
                
                if(checklist.get(i).equals(checklist.get(j))) {
                    return false;
                }
                
                if(Math.abs(i - j) == Math.abs(checklist.get(i) - checklist.get(j))) {
                    return false;
                }
            }
        }

        return true;
    }
}
