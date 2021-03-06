package com.leetcode.sort;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Created by Michael on 2017/3/3.
 *
 * Given an array of meeting time intervals consisting of start and end times [[s1,e1],[s2,e2],...] (si < ei),
 * find the minimum number of conference rooms required.
 *
 * For example,
 * Given [[0, 30],[5, 10],[15, 20]], return 2.
 *
 * Function Signature:
 * public int roomsRequired(Interval[] a) {...}
 *
 * <区间集合 系列问题>
 * E252 Meeting Rooms  : 给定一个时间区间集合，判断是否存在区间交叉的情况。
 * M56  Merge Intervals: 给定一个时间区间集合，合并所有存在交叉的区间并返回。
 * M253 Meeting Rooms 2: 给定一个时间区间集合，判断最多需要多少个并行的线程可以无冲突执行所有时间区间。
 *
 * <Tags>
 * - Min Heap
 * - 使用Arrays.sort(T array, Comparator<T> comparator)进行自定义规则的对象数组排序。
 * - 使用匿名类或Lambda表达式
 * - 对对象的两个属性分别排序
 *
 */
public class M253_Meeting_Rooms_2 {
    public static void main(String[] args) {
        System.out.println(roomsRequired(new Interval[] {new Interval(0, 30), new Interval(5, 10), new Interval(15, 20)}));
    }


    /** 解法1：优先队列。 */
    // 关键在于如何记录所需的最大会议室数量。
    // |-------------------|                    |-------------------|                       |-------------------|
    //    |----|  (end early)          ->           |------------|               ->             |------------| (end early)
    //       |-----|                                   |-----| (end early)                         |------------|
    //           |-----| (extend with 2nd)                      |----| (extend with 3rd)                         |----| (extend with 2nd)
    // 每扩张一次，就说明有两个区间可以并行。当前的最早会议室区间就会被扩张。
    // 每一行都是一个独立的会议室（可以理解为线程），我们需要做的就是把会议合并到现有最早的会议室中，或者另开一个会议室。
    // 这么做的理论依据
    // 条件1：我们已经事先将会议区间的开始时间从小到大排序，所以待插入队列的会议的开始时间一定晚于队列里的所有会议区间。
    // 条件2：根据加入优先队列的原则，我们可以确保队列里的所有区间一定都是具有重叠的，因此只要待加入会议和第一个会议重叠，他就一定会与队列里的其他所有队列重叠。
    // 根据以上两条因此有必要将他添加到优先队列里。
    // Java的PriorityQueue使用最小堆实现。最先出队的是最小的那个元素，至于大和小的相对概念可以通过Lambda表达式指定Comparator对象。
    static int roomsRequired(Interval[] a) {
        if (a == null || a.length == 0) return 0;
        Arrays.sort(a, (x, y) -> (x.start - y.start));                                              // 以<开始时间>对interval排序，开始时间越早越靠前
        PriorityQueue<Interval> heap = new PriorityQueue<>(a.length, (x, y) -> (x.end - y.end));    // 以<结束时间>作为优先级准则构造空优先队列
        heap.offer(a[0]);                                               // 插入第一个会议做种子
        for (int i = 1; i < a.length; i++) {
            Interval early = heap.poll();                               // 取出队列中结束最早的会议
            if (a[i].start >= early.end) early.end = a[i].end;          // 如果当前会议与结束最早的会议不冲突，就扩张结束最早的会议
            else heap.offer(a[i]);                                      // 如果冲突，就把当前会议加入队列
            heap.offer(early);                                          // 恢复结束最早的会议
        }
        return heap.size();
    }


    /** 解法2：对Interval对象的两个属性分别排序。 */
    // 与解法1利用最小堆的思路一致，都是判断新的会议区间是否能够与最早结束的会议进行合并，如果冲突，就要扩展。
    static int roomsRequired2(Interval[] a) {
        int len = a.length;
        int firstEnd = 0;
        int[] start = new int[len];
        int[] end = new int[len];
        for (int i = 0; i < len; i++) {
            start[i] = a[i].start;
            end[i] = a[i].end;
        }
        Arrays.sort(start);
        Arrays.sort(end);
        for (int i = 0; i < len; i++)
            if (start[i] >= end[firstEnd]) firstEnd++;          // 只要不冲突，就说明无需扩张会议室个数
        return len - firstEnd;
    }
}
