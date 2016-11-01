package com.leetcode.array;

import java.util.*;

/**
 * Created by Michael on 2016/10/24.
 * Given an array S of n integers, are there elements a, b, c in S such that a + b + c = 0?
 * Find all unique triplets in the array which gives the sum of zero.
 * The solution set must not contain duplicate triplets.
 *
 * For example, given array S = [-1, 0, 1, 2, -1, -4],
 * A solution set is:
 * [
 *  [-1, 0, 1],
 *  [-1, -1, 2]
 * ]
 *
 * Funtion Signature:
 * public List<List<Integer>> threeSum(int[] a) {...}
 */
public class M15_Three_Sum {
    public static void main(String[] args) {
        List<List<Integer>> result = threeSum2(new int[] {-1, 0, 1, 2, -1, -4});    // Should return 2 results.
        List<List<Integer>> result2 = threeSum2(new int[] {0, 0, 0, 0});   // Should return 1 result.
        System.out.println(result.size());
        System.out.println(result2.size());
    }

    /** 思路：先排序后解决问题。因为常规解法需要o(n^4)，已经远远高过排序本身的复杂度o(nlogn)
     *  因此有必要先排序，再使用排序所带了的很好的性质，使得最后整体复杂度降为了o(n^2)。爽爽的。
     *  同时需要将2Sum的解法扩展到这里。
     *  外循环遍历第三个值，从头扫描至倒数第三个元素
     *  将第三个值取反，作为2Sum的Target。
     *  内循环则用双指针扫描，从而实现在o(n)复杂度下达到既不丢解，也不重复记录解。
     */
    // 已排序数组的特性1：任意两个元素之和依旧已排序。可以根据这个左右移动首尾指针，记录所有独特解。
    // 已排序数组的特性2：任何相等的元素都一定会在一起，因此可以直接跳过。根据这个性质避免记录重复解。
    // 简化代码：使用Arrays.asList()直接生成一个填满元素的List对象。而不用使用add()语句好几次。
    static List<List<Integer>> threeSum2(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        if (a.length < 3) return result;
        Arrays.sort(a);
        for (int i = 0; i < a.length - 2; i++) {
            if (i > 0 && a[i] == a[i - 1]) continue;        // Avoid Duplicate
            int target = - a[i];
            int left = i + 1;
            int right = a.length - 1;
            while (left < right) {
                int sum = a[left] + a[right];
                if      (sum > target) right--;
                else if (sum < target) left++;
                else {
                    result.add(Arrays.asList(- target, a[left], a[right]));
                    left++;
                    right--;
                    while (left < right && a[left] == a[left - 1]) left++;      // Avoid Duplicate
                    while (left < right && a[right] == a[right + 1]) right--;   // Avoid Duplicate
                }
            }
        }
        return result;
    }

    // 下面是错误解法：试图将2Sum的HashMap解法用在这里，
    // 但是HashMap解法只能在仅有唯一解的前提下才能正常工作，因此无法避免重复。
    static List<List<Integer>> threeSumWrong(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        if (a.length < 3) return result;
        Arrays.sort(a);
        for (int i = 0; i < a.length - 2; i++) {
            if (i > 0 && a[i] == a[i - 1]) continue;        // Avoid Duplicate
            int target = -a[i];
            Map<Integer, Integer> map = new HashMap<>();
            for (int j = i + 1; j < a.length; j++) {
                for (int k = j + 1; k < a.length; k++) {
                    if (map.containsKey(a[j])) {
                        List<Integer> set = new ArrayList<>();
                        set.add(a[i]);
                        set.add(target - a[j]);
                        set.add(a[j]);
                        result.add(set);
                        break;              // 错误：无法做到既确保每个不同解都记录下来，又确保不重复记录相同解。
                    } else map.put(target - a[j], a[j]);
                }
            }
        }
        return result;
    }

    // 2Sum解法供参考。主要区别：
    // 1. 2Sum是直接给你提供target，而3Sum是你在遍历的时候主动选择的值。
    // 2. 2Sum需要返回的是索引值，而3Sum需要返回的是元素值本身。
    // 3. 2Sum告诉你只会有一个解，不需要处理多解情况，而3Sum则需要处理多解但并不重复的情况。
    static int[] twoSum(int[] a, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            if (map.containsKey(a[i])) return new int[]{map.get(a[i]), i};
            else map.put(target - a[i], i);
        }
        return new int[] {};
    }


    // 穷举法, time - o(n^4)
    // 遍历三个元素就用了三个for循环，
    // 同时还需要检查待插入结果是否已经重复，这又是一个for循环，所以整体时间复杂度是o(n^4)
    static List<List<Integer>> threeSum(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        Set<Integer> pattern = new HashSet<>();
        if (a.length < 3) return result;
        for (int x = 0; x < a.length - 2; x++) {
            for (int y = x + 1; y < a.length - 1; y++) {
                for (int z = y + 1; z < a.length; z++) {
                    if (a[x] + a[y] + a[z] == 0) {
                        List<Integer> set = new ArrayList<>();
                        set.add(a[x]);
                        set.add(a[y]);
                        set.add(a[z]);
                        Collections.sort(set);
                        if (result.isEmpty()) result.add(set);
                        else {
                            boolean duplicate = false;
                            for (List<Integer> res : result) {
                                if (set.equals(res)) {
                                    duplicate = true;
                                    break;
                                }
                            }
                            if (!duplicate) result.add(set);
                        }
                    }
                }
            }
        }
        return result;
    }
}