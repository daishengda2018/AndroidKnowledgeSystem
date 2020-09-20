[toc]

# 15 三数之和

三数之和一道经典老题，是两数之和的升级版。此解法的主要思路就是排序 + 双指针的方式。时间复杂度为 O(n^2) 空间复杂度为 O(1)

```java
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        final List<List<Integer>> result = new ArrayList<>();
        final int length = nums.length;
        if (nums == null || length <= 0) {
            return result;
        }
        Arrays.sort(nums);
        for (int i = 0; i < length; i++) {
            if (nums[i] > 0) {
                // 排序后如果元素大于0，后面就不能再出现三数之和为0的情况
                break;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                // 去重
                continue;
            }      
            int left = i + 1;
            int right = length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) {
                        // 去重
                        left ++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        // 去重
                        right --;
                    }
                    left ++;
                    right --;
                } else if (sum > 0) {
                    right --;
                } else if (sum < 0) {
                    left ++;
                }
            }
        }
        return result;
    }
}
```



# 19 删除链表的倒数第N个节点

```
给定一个链表: 1->2->3->4->5, 和 n = 2.

当删除了倒数第二个节点后，链表变为 1->2->3->5.
```

**说明：**

给定的 *n* 保证是有效的。

**进阶：**

你能尝试使用一趟扫描实现吗？



**解题思路：**

使用快慢指针解决问题，二者的移动速度是相同的，快指针首先开始移动当快指针领先慢指针 n 步的时候，慢指针开始移动，当快指针到达链表末尾的时候，慢指针正好到达要删除节点的上一节点，改变指向关系，删除成功。

**复杂度分析**

一次遍历所以时间复杂度为 O(n)、没有引入其他数据结构所有时间复杂度为 O(1)

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        int step = 0;
        ListNode emptyNode = new ListNode(-1);
        emptyNode.next = head;
        ListNode fast = emptyNode.next;
        ListNode slow = emptyNode;
        while(fast != null) {
            fast = fast.next;
            step ++;
            if(step > n) {
                slow = slow.next;
            }
        }
        slow.next = slow.next.next; 
        return emptyNode.next;
    }
}
```

# [25. K 个一组翻转链表](https://leetcode-cn.com/problems/reverse-nodes-in-k-group/)

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode nextGroupNode = head;
        int count = 0;
        while(nextGroupNode != null && count != k) { // 寻找第 k+1 个节点
            nextGroupNode = nextGroupNode.next;
            count++;
        }
        if (count == k) { // 如果第 k + 1 个节点找到了
           ListNode reverseNode = reverseKGroup(nextGroupNode, k); // 以k + 1个节点为首的反向列表
            // head - 待翻转的头指针
            // reverseNode 已经翻转过部分的头指针
            while(count-- > 0) { // 翻转 k 组
                ListNode next = head.next; 
                head.next = reverseNode; // 将待翻转头元素连接到已翻转部分的头指针上
                reverseNode = head; // 移动已翻转指针到新的头上
                head = next; // 待翻转数据头指针下移一位
            }
            head = reverseNode;
        }
        return head;
    }
}
```

# [88. 合并两个有序数组](https://leetcode-cn.com/problems/merge-sorted-array/)

给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。

 

说明:

初始化 nums1 和 nums2 的元素数量分别为 m 和 n 。
你可以假设 nums1 有足够的空间（空间大小大于或等于 m + n）来保存 nums2 中的元素。


示例:

> 输入:
> nums1 = [1,2,3,0,0,0], m = 3
> nums2 = [2,5,6],       n = 3

> 输出: [1,2,2,3,5,6]



```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (nums1.length < m + n) {
            return;
        }
        int i = m - 1;
        int j = n - 1;
        int k = m + n - 1;
        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[k--] = nums1[i--];
            } else {
                nums1[k--] = nums2[j--];
            }
        }
        while(j >= 0) {
            nums1[k--] = nums2[j--];
        }
    }
}
```

时间复杂度：O(m+n)

空间复杂度：O(1)



# [84. 柱状图中最大的矩形](https://leetcode-cn.com/problems/largest-rectangle-in-histogram/)

使用单调栈的方式寻找左右边界

```java
class Solution {
    public int largestRectangleArea(int[] heights) {
        int length = heights.length;
        if (heights == null || length == 0) {
            return 0;
        }
        if (length == 1) {
            return heights[0];
        }

        int[] left = new int[length];
        int[] right = new int[length];
        Arrays.fill(right, length);
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < length; i++) {
            while (!stack.empty() && heights[i] < heights[stack.peek()]) {
                right[stack.pop()] = i;
            }
            left[i] = stack.empty() ? -1 : stack.peek();
            stack.push(i);
        }

        int result = 0;
        for (int i = 0; i < length; i++) {
            result = Math.max(result, (right[i] - left[i] -1) * heights[i]);
        }
        return result;
    }
}
```

时间复杂度：O(N)

空间复杂度：O(N)

# [239. 滑动窗口最大值](https://leetcode-cn.com/problems/sliding-window-maximum/)

```java
class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length * k <= 0) {
            return new int[0];
        }
        int length = nums.length;
        int[] result = new int[length - k + 1];
        int resultIndex = 0;
        Deque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < length; i ++) {
            // 窗口开始向前移动, 
            while (!deque.isEmpty() && deque.peek() < i - k + 1) {
                deque.poll();
            }
            // 循环比较大小，永远保留窗口内最大的元素
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }
            // 装载新数据
            deque.offer(i);
            // 手机结果
            if (i >= k - 1) {
                result[resultIndex++] = nums[deque.peek()];
            }
        }
        return result;
    }
}
```

时间复杂度：O(N)

空间复杂度：O(N)



# [反转链表](https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/)

```java
class Solution {
    public ListNode reverseList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode dumpy = new ListNode(Integer.MIN_VALUE);
        while (head != null) {
            ListNode next = head.next;
            head.next = dumpy.next;
            dumpy.next = head;
            head = next;
        }
        return dumpy.next;
    }
}
```

