package com.leetcode.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.leetcode.linkedlist.ListNode;

/**
 * Created by LYuan on 2016/9/27.
 *
 * Basic Algorithm: Merge Sort
 *
 * <Core Mechanism>
 * Divide + Merge
 *
 * <For Array>
 * - Time: O(n * log n) 原本复杂度是 <n分解 * n合并>。其中分解部分本质上是二分法，因此整体复杂度降至 log n * n。
 * - Space: O(n)        对于数组，归并排序的合并部分操作如果想要在O(n)时间内完成，必须用额外空间。
 *
 * <For Linked List>
 * - Time: O(n * log n) 同数组
 * - Space: O(1)        对于链表，归并排序的合并部分操作可以在O(1)n内完成。
 *
 * <Tags>
 * - 二分法：将问题规模分解，使得时间复杂度从 n * n 降至 n * log n.
 * - 递归：利用逆序递归对数组进行分解
 * - Sentinel卫兵：同步扫描两个序列时经常用到
 * - Two Pointers: 快慢指针（倍速双指针）同向扫描定位链表中点。[ slow → ... fast → → ... ]
 * - Dummy节点：链表迁移法
 * - 链表拆分（切断）：在修改链表结构的同时切断原有链表，可以有助于简化操作。
 *
 */
public class Basic_Merge_Sort extends SortMethod {
    public static void main(String[] args) {
        // For Arrays
        int[] arr = {9, 8, 7, 5, 2, 4, 1, 6};
        MergeSort_Iterative(arr);
        System.out.println(Arrays.toString(arr));

        int[] arr2 = {9, 8, 7, 5, 2, 4, 1, 6};
        MergeSort_Recursive(arr2);
        System.out.println(Arrays.toString(arr2));

        // For LinkedList
        mergeSort(ListNode.Generator(new int[] {4, 5, 3, 2, 1, 0})).print();

        // Bulk Test
        SortUtility.VerifySortAlgorithm("merge");
        SortUtility.TestPerformance("merge", 100000);       // 40ms per 10,0000 elements
    }


    /** 数组迭代解法：<分解过程>用迭代实现，<合并过程>与递归解法完全一样。Time - O(nlogn), Space - O(n). */
    // 与递归解法共享合并过程，区别仅在于分解过程，省略了递归方法不断除二分解将数组分段的过程。
    // 该解法又称为Bottom-Up，这是因为相比与Top-Down的不断分解方法，Bottom-Up直接就从最小分段开始入手。
    //             Top-Down                       Bottom-Up
    //          4  5  3  2  1  0               4  5  3  2  1  0
    // Split ↓  -------  -------      Merge ↓  -  -  -  -  -  -   (0, 0, 1) (2, 2, 3) (4, 4, 5)
    // Split ↓  ----  -  ----  -      Merge ↓  ----  ----  ----   (0, 1, 3) (4, 4, 5)
    // Split ↓  -  -  -  -  -  -      Merge ↓  ----------  ----   (0, 3, 5)
    // Merge ↓  ----  -  ----  -      Merge ↓  ----------------
    // Merge ↓  -------  -------
    // Merge ↓  ----------------
    // 从上面的对比示意可以看到，两种方式对数组的分割方式是不同的，Bottom-Up是按照以2为底的幂进行的扩张的。
    /** 循环指针含义 */
    // 记住内外循环的目的是为了构造正确的left, mid, right值给merge方法使用，而不是直接当场扫描并合并
    // 外循环i表示每次划分的<左右分区各自宽度>，应为1, 2, 4, 8, 16...，因此有：i *= 2
    // 内循环j表示每次划分的<左分区起始位置>，j自增间隔等于两个i（即左右区间长度之和），因此有：j += 2 * i
    // 在i=1时应为0, 2, 4, 6, 8...
    // 在i=2时应为0, 4, 8, 12, 16...
    // 在i=3时应为0, 8, 16, 24, 32...
    // 在i=4时应为0, 16, 32, 48, 64...
    /** merge调用方式 */
    // left = j; 因为j就表示做分区起始位置
    // mid = j + i - 1; 因为起始位置j加上分区宽度i就是中点，减一就是左分区的终止位置
    // right = j + 2 * i - 1; 因为起始位置加上左右分区的宽度2*i就是总长度，减一就是右分区的终止位置
    // 还要考虑到对于长度不是2的幂的数组，需要避免mid和right越界，因此在使用前需要和a.length-1进行比较，取小的那个。
    /** 举例 */
    // {9, 8, 7, 4, 5}
    // Merge(0, 0, 1): 9 8 -> 8 9
    // Merge(2, 2, 3): 7 4 -> 4 7
    // Merge(4, 4, 4): 5, do nothing. 如果没有Math.min保护，原本应是(4, 4, 5), right会越界。
    // Merge(0, 1, 3): 8 9 4 7 -> 4 7 8 9
    // Merge(4, 4, 4): 5, do nothing. 如果没有Math.min保护，原本应是(4, 5, 7), mid和right都会越界。
    // Merge(0, 3, 4): 4 7 8 9 5 -> 4 5 7 8 9
    // {4, 5, 7, 8, 9}
    static void MergeSort_Iterative(int[] a) {
        for (int i = 1; i < a.length; i *= 2)
            for (int j = 0; j < a.length; j += 2 * i)
                mergeX(a, j, Math.min(j + i - 1, a.length - 1), Math.min(j + 2 * i - 1, a.length - 1));
    }
    // 完全相同的合并过程
    static void mergeX(int[] a, int left, int mid, int right) {
        List<Integer> list = new ArrayList<>(right - left + 1);
        int i = left;
        int j = mid + 1;
        while (i <= mid || j <= right) {
            int x = i <= mid ? a[i] : Integer.MAX_VALUE;
            int y = j <= right ? a[j] : Integer.MAX_VALUE;
            list.add(Math.min(x, y));
            if (x < y) i++;
            else j++;
        }
        int idx = left;
        for (int element : list) {
            a[idx++] = element;
        }
    }

    /** 数组递归解法：<分解过程>用递归实现，<合并过程>需要使用额外空间。Time - O(nlogn), Space - O(n). */
    // Top-down Recursive: 逆序递归，即先递归至终点，再在返回的过程中进行Conquer
    // 用三层结构实现：
    // 1. 最外层(Wrapper)
    // 2. 中间层(Split)：将数组按二分法进行划分至最小单位（逆序递归中的“先递归”）
    // 3. 最内层(Merge)：将划分完的数组区间按顺序进行合并（逆序递归中的“后干活”）
    // 从整体可以看到实际上这里递归的唯一目的就是对数组区间进行了划分，划分完了之后就可以依次合并排序了。
    // 使用ArrayList实现merge功能，双指针同时扫描两段，并存入ArrayList，最后由ArrayList覆盖当前区间
    // 使用卫兵：用极大值保护先扫完的部分，直至左右两部分都扫完。
    // 左中右left / mid / right三个指针索引都是inclusive的，因此在调用的时候应该调用MergeSort(a, 0, a.length - 1)
    // 算法导论里介绍的方法是先把左右两段元素备份至一个临时数组，然后用双指针扫描更新原数组，本质上是一样的，不过感觉我的方法写起来更简单些
    //     4  5  3  2  1  0              4  5  3  2  1
    //     -------  -------              -------  ----          断开的位置就是分段的位置
    //     ----  -  ----  -              ----  -  -  -          可以看到最后会分解为长度全为1的状态
    //     -  -  -  -  -  -              -  -  -  -  -
    static void MergeSort_Recursive(int[] a) {                              // 最外层：Wrapper
        split(a, 0, a.length - 1);
    }

    static void split(int[] a, int left, int right) {                       // 中间层：二分法划分数组
        if (left == right) return;
        int mid = left + (right - left) / 2;                                // 对于奇数长度数组，mid会指向中间元素
        split(a, left, mid);                                                // 对于偶数长度数组，mid会指向中间的左侧元素
        split(a, mid + 1, right);
        merge(a, left, mid, right);
    }

    static void merge(int[] a, int left, int mid, int right) {              // 最内层：对已划分区间进行合并
        List<Integer> list = new ArrayList<>(right - left + 1);
        int i = left;
        int j = mid + 1;
        while (i <= mid || j <= right) {
            int x = i <= mid ? a[i] : Integer.MAX_VALUE;                    // 使用卫兵
            int y = j <= right ? a[j] : Integer.MAX_VALUE;                  // 使用卫兵
            list.add(Math.min(x, y));
            if (x < y) i++;
            else j++;
        }
        int idx = left;
        for (int element : list) {                                          // 拷贝回原数组
            a[idx++] = element;
        }
    }

    @Override
    public void sort(int[] a) {
        MergeSort_Recursive(a);
    }

    /** 链表递归解法：双指针（快慢指针）进行链表分解切断 + 链表合并。Time - O(nlogn), Space - O(1). */
    // 详见M148的分析示例。
    // 链表的归并排序和数组一样分为<分解>和<合并>两个过程。但链表和数组在这两个过程的具体操作上是完全不同的。
    // <链表分解>核心思路：利用快慢指针（倍速）寻找链表中点，当fast抵达右侧边界时，slow所在位置就是中点。
    // 关键点1：奇偶长度下，中点应该停留在中间节点（奇数情况）或中间的左侧节点（偶数情况）。
    // 关键点2：每次分解都需要从中点将链表直接切断。
    // <链表合并>核心思路：利用Dummy节点的链表迁移法，将节点按顺序组装至Dummy节点所引领的新链表。
    static ListNode mergeSort(ListNode head) {
        if (head == null || head.next == null) return head;   // 递归终止条件：如果当前链表为空或只有一个节点，则无需再分解。
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) { // 倍速双指针：确保slow会提前停在中点（奇数）或中点左侧（偶数）
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode right = slow.next;                           // 缓存右半部起点
        slow.next = null;                                     // 将链表从中点位置拆分。
        head = mergeSort(head);                               // 递归左半部，并记录返回的新表头
        right = mergeSort(right);                             // 递归右半部，并记录返回的新表头
        return merge(head, right);                            // 根据新表头进行合并
    }

    // 将两个链表进行原位合并
    static ListNode merge(ListNode left, ListNode right) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        while (left != null && right != null) {         // 只需将其中一个链表扫描完成即可，无需两个链表全扫完
            if (left.val < right.val) { curr.next = left; left = left.next; }
            else                      { curr.next = right; right = right.next; }
            curr = curr.next;
        }
        if (left == null) curr.next = right;            // 收尾工作：如果其中一个链表尚未扫完，就直接把curr.next接在这个链表上即可
        else              curr.next = left;
        return dummy.next;
    }

    /** 扩展问题：对于数组如何做到O(1)空间复杂度的合并过程？ */
    // 对于数组的两种归并排序解法又是递归又是迭代，看上去很不同，但是实际上用的是相同的合并算法(merge方法)，而该合并算法的空间复杂度是O(n)。
    // 那么问题就来了，对于数组，是否可以做到O(1)空间复杂度的合并呢？
    // 可以看到其时间复杂度已经最优，即左右部分的所有元素扫描完就合并完成了，不可能比这个复杂度更低了，因为你必须扫描每个元素，不可能不扫描人家就自动排好。
    // 但是其空间复杂度是O(n + m)，依然需要额外的空间才能完成，是否可以优化至O(1)即Constant Space呢。这是个非常有意思的问题。
    // 下面就提供一个In-place的数组合并解法，不过需要注意的是，为了做到In-place Merge，我们不得不提高了时间复杂度至O(n * m)
    // 这就是典型的<Time / Space Trade-off>，要不然用时间复杂度换空间复杂度，要不然用空间换时间，你终归需要付出才有回报。
    // 那么新问题是：对于数组，是否可以在保持Time - O(n)的前提下做到Space - O(1)的合并呢？
    // 答案是肯定存在，只不过达到这个标准的解法通常都太过复杂，以至于一般人不可能在面试时临时创造出来。

    /** 对<两个独立数组>进行原位排序：Time - O(n^2), Space - O(1) */
    // 其本质是插入排序。
    // 将问题抽象为对两个独立的数组进行merge，使得merge完成后a连接b也是已排序的
    // a = {1, 5, 9, 10, 15, 20}
    // b = {2, 3, 8, 13}
    // 核心思路在于需要在比对两个数组的同时，确保两个数组剩余未扫描部分的元素依然是有序的。
    // 为了确保这一点，需要将两者比对的落选者插入到对方数组的合适部分。所以实际上就是插入排序的过程。
    static void merge_inplace(int[] a, int[] b) {
        int i = a.length - 1;
        int j = b.length - 1;
        while (j >= 0) {
            if (a[i] > b[j]) {
                int x, temp = b[j];
                b[j] = a[i];
                for (x = i - 1; x >= 0 && a[x] > temp; x--)
                    a[x + 1] = a[x];
                a[x + 1] = temp;
            }
            j--;
        }
    }

    /** 对<一个数组指定区间内的左右两部分>进行原位排序：Time - O(n^2), Space - O(1) */
    // 在merge2()方法的基础上修改，得到了可以用与Merge Sort的原位merge方法
    // 需要注意j和x的下限应该相应的改为mid + 1和left。
    static void merge_inplace2(int[] a, int left, int mid, int right) {
        int i = mid;
        int j = right;
        while (j >= mid + 1) {
            if (a[i] > a[j]) {
                int x, temp = a[j];
                a[j] = a[i];
                for (x = i - 1; x >= left && a[x] > temp; x--)
                    a[x + 1] = a[x];
                a[x + 1] = temp;
            }
            j--;
        }
    }
}
