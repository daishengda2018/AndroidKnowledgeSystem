[toc]

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

