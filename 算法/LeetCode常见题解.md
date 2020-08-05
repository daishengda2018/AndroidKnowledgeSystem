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

示例：**

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

