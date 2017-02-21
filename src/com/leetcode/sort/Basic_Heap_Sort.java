package com.leetcode.sort;

import java.util.Arrays;

/**
 * Created by LYuan on 2016/9/27.
 *
 * Basic Algorithm: Heap Sort
 * Time - o(n * log n)
 * Space - o(1)
 */
public class Basic_Heap_Sort {
    public static void main(String[] args) {
        int[] b = {9, 8, 7, 6, 56, 4, 3, 2, 1};
        HeapSort(b);
        System.out.println(Arrays.toString(b));
    }

    static void HeapSort(int[] a) {
        if (a == null || a.length < 2) return;
        maxHeapify(a, a.length);
        for (int range = a.length - 1; range > 0; range--) {
            swap(a, 0, range);
            maxHeapify(a, range);
        }
    }

    static void maxHeapify(int[] a, int range) {
        for (int i = range / 2 - 1; i >= 0; i--) {
            int left = a[i * 2 + 1];
            int right = i * 2 + 2 < range ? a[i * 2 + 2] : Integer.MIN_VALUE;
            int max = Math.max(left, right);
            if (a[i] < max) swap(a, i, max == left ? i * 2 + 1 : i * 2 + 2);
        }
    }

    static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }


    // 看到Heap Sort我完全想不起来到底跟Heap有什么关系
    // 在复习算法的时候我发现一个特别有意思的现象，就是之前学会了这个算法之后，往往已经忘记了最初接触时候的那些最根本的疑问，
    // 比如在Heap Sort代码的注释里可以看到我当时事无巨细的分析了很多细枝末节的问题，却并没有强调这个算法最独特的地方就是他用了Heap，heap到底是什么等等
    // 结果就是，我现在再看之前写的算法注释的时候，发现完全get不到上面写的点，因为我压根就不记得到底Heap是个什么东西了，
    // 以为就是一个类似于Queue的东西，和Java内存的Heap和Stack搞混了。这也说明其实我之前学的时候就没有真正的领悟到最核心的东西。
    // 好，啰嗦完毕，到底Heap是什么？Heap Sort又是什么呢？
    // 如果我们说一个数据结构（比如一个数组）是Heap，那么是因为这个数据结构符合“最大堆或最小堆性质”。
    // 这个性质是这样的：如果将该数据结构转化为二叉树，那么一定有父节点全大于或全小于他们的子节点。
    // 所以可以判定{1, 2, 3, 4, 5, 6, 7}是Heap，而且是最小堆。
    // 所以可以判定{2, 1, 3, 4, 5, 6, 7}不是Heap，因为父节点2的两个子节点1和3并不同时大于或小于父节点。
    // 同时可以看到，如果一个数据结构是Heap，那么他基本上就已经差不多有序了，不过Heap的性质并不能确保层与层之间一定是有序的，
    // 例如{6, 5, 4, 3, 2, 1, 0}是最大堆，可以看到第一层{6}、第二层{5, 4}、第三层{3, 2, 1, 0}的层与层之间已经有序了，不需要再交换任何元素了。
    // 但是例如{14, 10, 13, 8, 9, 12, 6, 7, 3, 1, 4, 11, 5, 2, 0}(由0~14最大堆化而来)也是最大堆，但是第三层的12比第二层的10还要大，所以层与层之间并不有序。
    // 也就是说，已排序数组一定是最大堆，但是最大堆并不一定是已排序数组，最大堆的层与层之间也不一定是已排序的。
    // 所以如何在最大堆的基础上将对应的数组完整升序排列就需要技巧了，也就是下面介绍的第二步内容。
    // 上面介绍的就是到底什么是Heap，下面我们做的其实就是利用Heap的这些性质来为排序服务。
    // 第一步：首先我们要把任何一个数组转变成为一个最大堆，
    // 第二步：然后我们不断提取最大堆的根，与最后一个元素交换，并缩小二叉树，再让二叉树最大堆化，再提取。
    //
    // 关于第一步：
    // 但是把任何一个数组转化成为一个最大堆其实还是比较麻烦的，因为这要求你把这个数组对应的二叉树上的元素都重新交换顺序排列，看上去头绪比较多
    // 但是好在二叉树的自相似特性使得递归特别适合使用，因为只要从叶子节点开始扫描，确保每个父节点和两个子节点的大小都满足最大堆即可。
    // 当然，需要考虑到，越向二叉树的顶端移动，如果遇到了在错误位置的元素需要更换，那么必须更新整个已排序的二叉树，
    // 这是因为只要更新了一个节点，由于二叉树父子节点间的依赖性，就会导致牵一发动全身，所以必须更新整个已排序部分。
    //
    // 关于第二步：
    // 上面已经举例说明了最大堆并不能保证层与层之间是绝对已排序的，所以试图每层内部排序的思路是不可行的，
    // 但同时可以看到，最大堆是可以保证第一层（即根节点）一定是二叉树的最大节点，利用这个性质，我们可以每次提取该二叉树的最大节点
    // 也就是把数组第一个元素与数组最后一个元素交换（即二叉树最低端的叶子节点），然后缩小二叉树的范围，
    // 由于最大堆的二叉树的第一层一定是当前二叉树最大的元素，这个过程也就相当于是逆序的把最大堆的最大值提取出来放在了最后。
    // 不过坦白的讲，堆排序里面不管是第一步还是第二步中都要反复最大堆化的运算让我很难相信这是一个(n * log n)复杂度的算法。挺有违直觉的。
    // 由于第二步操作中maxHeapify的时候，构造二叉树的数组长度需要不断缩小，
    // 因此有两种方案，一个方案是给maxHeapify这个方法传进去一个长度，一个方案是将长度作为类的成员变量，游离于方法之外。
    //
    // 关于时间复杂度计算和最坏情况性能：
    // 堆排序比较蛋疼的的一点在于如果数组是已排序的，那么他还是会傻傻的把已经升序的数组先修改成最大堆，然后再修改回来，简直是南辕北辙。
    // 时间复杂度之堆排序的第一步：将数组最大堆化，时间复杂度为o(n)。
    // 例如四层15个数进行最大堆化：最坏情况一共7个数需要下降，这7个数中，4个下降1层，2个下降2层，1个下降3层，总下降数11.
    // 例如五层31个数进行最大堆化：最坏情况一共15个数需要下降，这15个数中，8个下降1层，4个下降2层，2个下降3层，1个下降4层，总下降数26.
    // 时间复杂度之堆排序的第二步：不断取根节点，时间复杂度为o(nlogn)
    // 因为要提取n次根节点，每次从根节点最大堆化是logn（这个似乎是从主定理证明的）
    static void HeapSortx(int[] a) {
        for (int i = a.length / 2 - 1; i >= 0; i--)
            maxHeapify_Iterative(a, i, a.length);


        for (int i = a.length - 1; i > 0; i--) {
            int temp = a[0];
            a[0] = a[i];
            a[i] = temp;
            //maxHeapify_Recursive(a, 0, i);    // i既做索引，又做未排序数组的长度，且自减隐式包含在for循环中。比较简洁 ^_^
            maxHeapify_Iterative(a, 0, i);
        }
    }

    /** 递归解法，正序递归（一边操作一遍递归，递归到头后什么都不做就结束了）*/
    // 很神奇的性质1：拥有叶子节点的数组最尾端元素的索引值一定是 array.length / 2 - 1.
    // 很神奇的性质2：左儿子的节点索引一定等于 父节点索引 * 2 + 1，右儿子的节点索引一定等于 父节点索引 * 2 + 2
    // 由于是逆序自底向上maxHeapify的，因此可以假设当前root节点之下的所有节点都已经maxHeapify了
    // 可以简化的点：在更新一个父子节点之后，由于父节点只会与左右儿子之一进行交换，因此递归调用maxHeapify只需要作用在交换了的那个分支上，没改的那个分支不用动。
    // 关于父节点/左儿子/右儿子：首先需要考虑到左儿子可能不存在（数组长度为1时），而右儿子有可能不存在（不完全二叉树）
    // 至于如何做到优雅的比较和交换：记住一点，用元素的索引而不是元素的值。
    private static void maxHeapify_Recursive(int[] a, int root, int len) {
        int left = root * 2 + 1;
        int right = root * 2 + 2;
        int max = root;
        if (left < len && a[left] > a[max]) max = left;
        if (right < len && a[right] > a[max]) max = right;
        if (max != root) {
            int temp = a[max];
            a[max] = a[root];
            a[root] = temp;
            maxHeapify_Recursive(a, max, len);
        }
    }

    /** 迭代解法，和正序递归很像，只不过是用while循环代替了递归方法而已 */
    private static void maxHeapify_Iterative(int[] a, int root, int len) {
        while (root <= len / 2 - 1) {   // 至多扫描至最大非叶子节点索引
            int left = root * 2 + 1;
            int right = root * 2 + 2;
            int max = root;
            if (left < len && a[left] > a[max]) max = left;
            if (right < len && a[right] > a[max]) max = right;
            if (max != root) {
                int temp = a[max];
                a[max] = a[root];
                a[root] = temp;
                root = max;
            }
            else break;     // 如果无需交换父节点和子节点，也就不需要下降了
        }
    }
}
