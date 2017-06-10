/*
 * CompareDescending
 *
 * Version 1.0.0
 *
 * Copyright 컴퓨터전공 2013012289 한기훈
 */

import java.util.Comparator;

/* Queen 의 정보를 담고 있는 List 의 정렬을 위한 Class 이다. */
public class CompareDescending implements Comparator<CustomPair> {

    @Override
    public int compare(CustomPair parent1, CustomPair parent2) {
        /* 살아 있는 Queen 의 수를 기준으로 내림차순 정렬을 한다. */
        return (parent2.alive_count - parent1.alive_count);
    }
}
