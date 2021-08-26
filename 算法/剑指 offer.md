[toc]



# [3. 数组中重复的数字](https://leetcode-cn.com/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof/)

常见的解思路是使用 Set 作为辅助数据结构一次遍历集合如果 set 中已经有同样的 value 返回，否则加入。

```java
class Solution {
    public int findRepeatNumber(int[] nums) {
        final Set<Integer> cache = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (cache.contains(nums[i])) {
                return nums[i];
            } else {
                cache.add(nums[i]);
            }
        }
        return -1;
    }
}
```

此解法的时间复杂度为 O(n)，空间复杂度 O(n)



但是此题很特殊：数组 nums 里的所有数字都在 0～n-1 的范围内，那么就有了一个空间复杂度为 O(1) 解发：

一次遍历数组，依次比较当前数字和下标与之对应的数字，如果数字和下标相等 continue，如果二者不相等则交换位置，相当则返回结果：

```java
class Solution {
    public int findRepeatNumber(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            if (i == nums[i]) {
                continue;
            }
            int target = nums[i];
            if (target == nums[target]) {
                return target;
            } else {
                nums[i] = nums[target];
                nums[target] = target;
            }
        }
        return -1;
    }
}
```

此解法的时间复杂度为 O(n)，空间复杂度 O(1)

# [二维数组中的查找](https://leetcode-cn.com/problems/er-wei-shu-zu-zhong-de-cha-zhao-lcof/)

这道题的思路就是缩小搜索空间。

1. 此题必须选用左下角，即 matrix\[matrix.length - 1][0] 否者选取左上角判断的条件很多，很难一次性写对！
2. 判断条件要判断数组是否为 null ，一位数组长度是否为 0，二维数组是否为 null。

```java
class Solution {
    public boolean findNumberIn2DArray(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null) {
            return false;
        }
        int curRow = matrix.length - 1;
        int curColumns = 0;
        while (curRow >= 0 && curColumns < matrix[0].length) {
            final int flag = matrix[curRow][curColumns];
            if (target == flag) {
                return true;
            } 
            if (target > flag) {
                curColumns++;
            } else {
                curRow--;
            }
        }
        return false;
    }
}
```

# [06. 从尾到头打印链表](https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof/)

1. 使用辅助栈，完成先进后出的特点：时间复杂度为 O(n) 空间复杂度为 O(n) 

```
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {
    public int[] reversePrint(ListNode head) {
        final Stack<ListNode> stack = new Stack<>();
        int count = 0;
        while (head != null) {
            stack.push(head);
            head = head.next;
            count++;
        }
        int index = 0;
        final int[] result = new int[count];
        while (!stack.empty()) {
            result[index++] = stack.pop().val;
        }
        return result;
    }
}
```

2. 使用数组下标索引特性,不借助任何辅助数据结构：时间复杂度为 O(n) 空间复杂度为 O(1) 

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
    public int[] reversePrint(ListNode head) {
        int capacity = 0;
        ListNode node = head;
        while (node != null) {
            capacity++;
            node = node.next;
        }
        int[] result = new int[capacity];
        while (head != null) {
            result[capacity - 1] = head.val;
            head = head.next;
            capacity--;
        }
        return result;
    }
}
```

# [10- I. 斐波那契数列](https://leetcode-cn.com/problems/fei-bo-na-qi-shu-lie-lcof/)

采用递归的方式是第一直观感受，但是递归算法设计大量重复计算，需要引入 HashMap 作为缓冲才能通过测试

```java
import java.util.*;

class Solution {
    private Map<Integer, Integer> mMap = new HashMap<>();
    public int fib(int n) {
        if (n <= 1) {
            return n;
        }
        if (mMap.containsKey(n)) {
            return mMap.get(n);
        } 
        int result = fib(n - 1) + fib(n - 2);
        mMap.put(n, result);
        return result;
    }
}
```

此解法时间、空间复杂度都为：O（n）

同样可以使用数组：

```java
class Solution {
    public int fib(int n) {
        if (n == 0) {
            return 0;
        }
        int[] cache = new int[n + 1];
        return performFib(cache, n);
    }

    private int performFib(int[] cache, int n) {
        if (n == 1 || n == 2) {
            return 1;
        }
        if (cache[n] != 0) {
            return cache[n];
        }
        int result = performFib(cache, (n - 1)) + performFib(cache, (n - 2));
        cache[n] = result % 1000000007;
        return cache[n];
    }
}
```



动态规划解法：

```java
class Solution {

    public int fib(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1 || n == 2) {
            return 1;
        }

        final int[] dp = new int[n + 1];
        dp[1] = dp[2] = 1;
        for (int i = 3; i <= n; i++) {
            dp[i] = (dp[i - 1] + dp[i - 2]) % 1000000007;
        }
        return dp[n];
    }
}
```



还可以使用递推从下往上计算：首先根据 f(0) 和 f(1) 计算出 f(2) 在根据 f(1) 和 f(2) 算出 f(3)……。此解法时间复杂度：O(n)、空间复杂度都为：O（1）

```java
class Solution {
    public int fib(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1 || n == 2) {
            return 1;
        }
        int prev = 1;
        int curr = 1;
        for (int i = 3; i <=n; i++){
            int sum = (prev + curr) % 1000000007;
            prev = curr;
            curr = sum;
        }
        return curr;
    }
}
```

# [12. 矩阵中的路径](https://leetcode-cn.com/problems/ju-zhen-zhong-de-lu-jing-lcof/)

```java
class Solution {
    public boolean exist(char[][] board, String word) {
        char[] words = word.toCharArray();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[0].length; x++) {
                if(dfs(board, x, y, words, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][]board, int x, int y, char[] words, int wordIndex) {
      	// 搜索的边界
        if (x < 0 || y < 0 || x >= board[0].length || y >= board.length) {
            return false;
        }
        // 不相等
        if (board[y][x] != words[wordIndex]) {
            return false;
        }
      	// 所有单词搜索完毕
        if (wordIndex == words.length - 1) {
            return true;
        }
        char tmp = board[y][x];
        // 防止重复搜索
        board[y][x] = '/';
        // 搜索左上右下
        boolean result = dfs(board, x - 1, y, words, wordIndex + 1)
                      || dfs(board, x, y + 1, words, wordIndex + 1)
                      || dfs(board, x + 1, y, words, wordIndex + 1)
                      || dfs(board, x, y - 1, words, wordIndex + 1);
        // 还原
        board[y][x] = tmp;
        return result;
    }
}
```

时间复杂度：O(M*N) ;空间复杂度： O(k) k = words.length



# [13. 机器人的运动范围](https://leetcode-cn.com/problems/ji-qi-ren-de-yun-dong-fan-wei-lcof/) 

20-09-03

> 地上有一个m行n列的方格，从坐标 [0,0] 到坐标 [m-1,n-1] 。一个机器人从坐标 [0, 0] 的格子开始移动，它每次可以向左、右、上、下移动一格（不能移动到方格外），也不能进入行坐标和列坐标的数位之和大于k的格子。例如，当k为18时，机器人能够进入方格 [35, 37] ，因为3+5+3+7=18。但它不能进入方格 [35, 38]，因为3+5+3+8=19。请问该机器人能够到达多少个格子？

这道题还是矩阵搜索题目，适用回溯 + DFS 深度优先算法解决。

这道题暗含两个优化点：

1. 位数之和的计算方式

   正常来讲计算位数之和需要使用如下函数：

   ```java
   private int sum(int i, int j) {
       int sum = 0;
       while (i != 0) {
           sum += i % 10;
           i /= 10;
       }
       return sum;
   }
   ```

   但是此题的取值是连续的即 x、 x  + 1、x + 2、 ……、x + ∞。所以适用如下公式：

   $$ s(x + 1)= \begin{cases} x - 8 & \text {$x \% 10 = 0$} \\ x + 1 & \text{$x \% 10  \neq 0 $} \end{cases} $$

   

2. 搜索方向的优化

   根据数位和增量公式得知，数位和每逢 **进位** 突变一次。则搜索路径为等腰三角形，所以可以看到只要向左、下搜索即可。

   <img src="images/cb7d37aced1c88a8e1bb2bf5b1cd1ab469abff23552eb1b103243961871ec65d-Picture1.png" alt="img" style="zoom: 50%;" />

```java
class Solution {
    private int m;
    private int n;
    private int k;
    private boolean[][] visited;
    public int movingCount(int m, int n, int k) {
        this.m = m;
        this.n = n;
        this.k = k;
        visited = new boolean[m][n];
        return dfs(0, 0, 0, 0);
    }

    private int dfs(int m, int n, int sm, int sn) {
        // 只向左、下搜索，搜索过的地方也不再搜索
        // 注意边界条件：>=
        if (m >= this.m || n >= this.n || visited[m][n] || k < sm + sn) {
            return 0;
        }
      	// 标记已经搜索完毕
        visited[m][n] = true
        // 特殊的位数之和计算方式 S(x + 1) = (x + 1) % 10 == 0 ? S(x) - 8 : S(x) + 1;
        int newSm = dfs(m + 1, n, (m + 1) % 10 == 0 ? sm - 8 : sm + 1, sn);
        int newSn = dfs(m, n + 1, sm, (n + 1) % 10 == 0 ? sn - 8 : sn + 1);
        return 1 + newSm + newSn;
    }
}
```

时间、空间复杂度：O（MN）



# [14- I. 剪绳子](https://leetcode-cn.com/problems/jian-sheng-zi-lcof/)

20-09-09

剪绳子问题体现的是贪婪算法：尽可能多的剪绳子、最好每段都是 3 这样乘积最大。

```java
class Solution {
    public int cuttingRope(int n) {
        if (n <= 3) {
            return n -1;
        }
       // 最终结果
        int result = 1;
       // 剩下的绳子比 4 大，就接着剪
        while (n > 4) {
            result *= 3;
            n -= 3;
        }
        // 最后的 n 的取值范围是 1、2、3、4.而此时最大乘基就是自身。
        // 再剪反而变小
        return result * n;
    }
}
```

# [14- II. 剪绳子 II](https://leetcode-cn.com/problems/jian-sheng-zi-ii-lcof/)

20-09-09

这道题是上面一题的延伸，主要解决的是 int 容量溢出的问题。

```JAVA
class Solution {
    public int cuttingRope(int n) {
        if (n <= 3) {
            return n - 1;
        }
        // 最终结果
        long result = 1;
        // 剩下的绳子比 4 大就接着剪
        while (n > 4) {
            result *= 3;
            result = result % 1000000007;
            n -= 3;
        }
        // 最后的 n 的取值范围是 1、2、3、4.而此时最大乘基就是自身。
        // 再剪反而变小
        return (int)(result * n % 1000000007);
    }
}
```

# 分治算法

## [07、重建二叉树](https://leetcode-cn.com/problems/zhong-jian-er-cha-shu-lcof/)

重建二叉树的关键在于中序遍历的结果：[左子树区间]root[右子树区间]，通过中序遍历的结果可以清晰的得出根节点与左右子树之间的关系。

此题要求使用中序遍历与前序遍历的结果还原二叉树，从中我们可得到如下信息：

1. 中序遍历的特点：[左子树区间]root[右子树区间]

2. 前序遍历的特点：root [左子树区间][右子树区间]

由于题目说明不存在重复的值，所以我们可以将中序遍历的结果与位置索引建立一个 map，通过前序遍历中的头结点即可在时间复杂度为 O(1) 的情况下从 map 中获取左右区间的边界。

在 map 之外我们还需要双指针来分别标记位于中序遍历中的左右子树边界；还需要一个全局变量指明当前 root 位于前序遍历的索引；根据这些值通过递归不断的压缩左右区间，最终重建二叉树。

```java
class Solution {
    private Map<Integer, Integer> mCache = new HashMap<>();
    private int mCurRootIndexOnPreorder = 0;
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        // 中序遍历是关系，需要将值和位置的关系存储起来
        for (int i = 0; i < inorder.length; i++) {
            mCache.put(inorder[i], i);
        }

        return buildTree(preorder, inorder, 0, inorder.length - 1);
    }

    private TreeNode buildTree(int[] preorder, int[] inorder, int start, int end) {
        // 边界条件
        if (start > end) {
            return null;
        }
        // 获取头结点的值
        int rootVal = preorder[mCurRootIndexOnPreorder++];
        // 获取头结点在中序遍历的位置
        int rootIndexOfInoder = mCache.get(rootVal);
        TreeNode root = new TreeNode(rootVal);
        // 递归建立左右子树
        root.left = buildTree(preorder, inorder, start, rootIndexOfInoder - 1);
        root.right = buildTree(preorder, inorder, rootIndexOfInoder + 1, end);
        return root;
    }
}

```





## [33、二叉搜索树的后序遍历序列](https://leetcode-cn.com/problems/er-cha-sou-suo-shu-de-hou-xu-bian-li-xu-lie-lcof/)

这道题使用单调栈可以获得最优解，而且能更好的理解。

首先明确什么是二叉搜索树：左节点 <根节点 < 右节点（left < root < right）即越往右数值越大，但是此题给出的是后续遍： left -> right -> root 并不满足单调性。但是我们将顺序反过来就形成了 root -> right -> left 即可以得到一个从小 -> 大 -> 小的两个严格单调区间。此时我们就可以使用单调栈来判断题目中提出的数组是否满足二叉搜索树的递增关系了。

一开始我们将反序后的数组逐个入栈，如果不满足单调递增性，这说明进入了 [left 左子树区间]。此时要做的是找到当前值（左子树值）所对应的根节点。怎么做呢？拿栈顶元素与当前值进行比较，直到把比当前值大的数全部出栈为止，最后一个出栈的就是当前值对应的根节点。

这里要注意的是 root > left 这个关系，如果当前值小于了 [left 左子树区间] 的值，则返回 false。



就这样一路比下去，如果所有的左子树值都小于所对应的 root 则满足二叉搜索树特点，否则就不是。

```java
class Solution {
    public boolean verifyPostorder(int[] postorder) {
        // 判定边界条件
        if (postorder == null || postorder.length == 0) {
            return true;
        }
        final Stack<Integer> stack = new Stack<>();
        // 简化判断左子树与 root 关系的判定逻辑
        int root = Integer.MAX_VALUE;
        // 将数组反过来得到单调关系
        for (int i = postorder.length - 1; i >= 0; i--) {
            // 左子树大于了 root， 不满足二叉搜索树特性
            if (postorder[i] > root) {
                return false;
            }
            // 不满足单调性，说明进入了左子树区间，开始寻找当前左子树对应的 root 
            while (!stack.isEmpty() && postorder[i] < stack.peek()) {
                root = stack.pop();
            }
            stack.push(postorder[i]);
        }
        return true;
    }
}
```

# [17、打印从 1 到最大的 n 位数](https://leetcode-cn.com/problems/da-yin-cong-1dao-zui-da-de-nwei-shu-lcof/)



# 分治算法

## [07. 重建二叉树](https://leetcode-cn.com/leetbook/read/illustration-of-algorithm/99lxci/)

这道题要求面试者熟悉二叉树的遍历特点：

1. 前序遍历：|头节点|左子树|右子树|
2. 中序遍历：|左子树|头节点|右子树|

根据这个特点可以设计一个递归算法，时间和空间复杂度都为 O(n)

### 模板代码：

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    // 记录当前根节点索引
    private int mCurRootIndex = 0;
    // 用于确定当前根节点在 inorder 的索引位置
    private Map<Integer, Integer> cache = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int idx = 0;
        for (int nums : inorder) {
            cache.put(nums, idx++);
        }
        return buildTree(preorder, inorder, 0, inorder.length - 1);
    }

    private TreeNode buildTree(int[] preorder, int[] inorder, int first, int last) {
        // 递归完毕
        if (first > last) {
            return null;
        }
        // 当前根节点的值，取值后索引++
        int curRootNums = preorder[mCurRootIndex++];
        // 当前根节点在 inorder 中的位置，从而确定左右子数范围：|左子数集合|curRoot|右子数集合|（这是 inorder）
        int curRootIdxInOrder = cache.get(curRootNums);
        // 建立当前 root
        TreeNode curRoot = new TreeNode(curRootNums);
        // 遍历左子数集合
        curRoot.left = buildTree(preorder, inorder, first, curRootIdxInOrder - 1);
        // 遍历右子数集合
        curRoot.right = buildTree(preorder, inorder, curRootIdxInOrder + 1, last);
        return curRoot;
    }
}
```

### 易错点：

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    private int mCurRootIndex = 0;
    private Map<Integer, Integer> mMap = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int index = 0;
        for (int num : inorder) {
            mMap.put(num, index++);
        }
        return buildTree(preorder, inorder, 0, inorder.length - 1);
    }

    private TreeNode buildTree(int[] preorder, int[] inorder, 
                               int firstIndex, int lastIndex) {
                                           
        if (firstIndex > lastIndex) {
            return null;
        }
        
        // 错误点: 忘记累加 mCurRootIndex
        int rootVal = preorder[mCurRootIndex++];
        int rootIndexOnInorder = mMap.get(rootVal);
        TreeNode root = new TreeNode(rootVal);
        // 错误点：rootIndexOnInorder 没有 -1
        root.left = buildTree(preorder, inorder, 
                                     firstIndex, rootIndexOnInorder - 1);
        root.right = buildTree(preorder, inorder, 
                                     rootIndexOnInorder + 1, lastIndex);
        return root;
     }
}
```



## [16、数值的整数次方](https://leetcode-cn.com/problems/shu-zhi-de-zheng-shu-ci-fang-lcof/)

2020/09/13

这道题看似简单其实暗藏了各种边界条件的判断：：x = 0、n = 0、n = 1、n < 0 等等。

此题的主要运用了二分查找和位运算的方式解决

1. Java 代码中 int32 变量 $n \in [-2147483648, 2147483647] $，因此当 $n = -2147483648$ 时执行 $n = -n$ 会因越界而赋值出错。解决方法是先将 n 存入 ==long== 变量 exponent ，后面用 exponent 操作即可。
2. 要注意 result 类型必须是 double 否则会出现精度丢失
3. $$a^n = \begin{cases} a^{n/2} *  a^{n/2} & \text{$n 为偶数$} \\a^{(n - 1)/2} *  a^{(n - 1)/2} & \text {$n 为奇数$}\end{cases}  $$



## 解法1：递推，时间复杂度 O(logn)、空间复杂度O(1)

```java
class Solution {
    public double myPow(double x, int n) {
        // 0 的任何次方都无意义。
        if (x == 0) {
            return 0;
        }
        // 任何数的 0 次方都等于 1
        if (n == 0) {
            return 1;
        }
        // 任何数的 1 次方都等于原数字
        if (n == 1) {
            return x;
        }
        // 见上文解释
        long exponent = n;
        // 如果 n < 0 着进行转化
        if (exponent < 0) {
            x = 1 / x;
            exponent = -exponent;
        }
        // ！易错点：不是所有的初始值都是 0，写成了  result = 0
        double result = 1;
       // 快速幂等算法
        while (exponent > 0) {
            // 判断二进制最右一位是否为 1,为 1 即为奇数
            if ((exponent & 1) == 1) {
                result *= x;
            }
            // 每次计算都计算 x^2 即可
            x *= x;
            // 相当于除以 2
            exponent >>= 1;
        }
        return result;
    }
}
```

## 解法2: 递归，时间复杂度 O(logn)、空间复杂度O(n)

```java
class Solution {
    public double myPow(double x, int n) {
        if (x == 0) {
            return 0;
        }
        if (n == 0) {
            // 任何数的 0 次方都等于 1
            return 1;
        }
        if (n == 1) {
            // 任何数的 1 次方都等于原数字
            return x;
        }
        long exponent = n;
        if (exponent < 0) {
            x = 1 / x;
            exponent = -exponent;
        }
       // 使用公式完成递推
        return ((exponent & 1) == 1) ? x * myPow(x * x, (int)(exponent >> 1))
                                      :myPow(x * x, (int)(exponent >> 1)) ;
    }
}
```



## [51. 数组中的逆序对](https://leetcode-cn.com/leetbook/read/illustration-of-algorithm/o58jfs/)

这道题是典型的归并排序题，是需要写出排序算法，并统计 “逆序对” 的数量即可。

时空复杂度和归并算法一样：时间复杂度 O(nLogn)、空间复杂度 O(n){因为引入了辅助数组}

### 模板代码：

```java
class Solution {
    public int reversePairs(int[] nums) {
        return mergeSort(nums, 0 , nums.length - 1);
    }

        /**
     * 进行归并排序
     *
     * @param arr        需要排序的数组
     * @param startIndex 数组的开始索引
     * @param endIndex   数组最后一个元素的索引
     */
    public int mergeSort(int[] arr, int startIndex, int endIndex) {
        // 当子序列中只有一个元素时结束递归
        if (startIndex >= endIndex) {
            return 0;
        }
        // 划分子序列
        int mid = startIndex + (endIndex - startIndex) / 2;
        // 对左侧子序列进行递归排序: 左右闭区间
        int leftPairs = mergeSort(arr, startIndex, mid);
        // 对右侧子序列进行递归排序: 左右闭区间
        int rightPairs = mergeSort(arr, mid + 1, endIndex);
        // 合并
        int mergePairs = merge(arr, startIndex, mid, endIndex);

        return leftPairs + rightPairs + mergePairs;
    }

   /**
     * 两路归并算法，两个排好序的子序列合并为一个子序列
     *
     * @param arr   需要合并的数组
     * @param left  左边起始索引
     * @param mid   中间索引
     * @param right 右边起始索引
     */
      public int merge(int[] arr, int first, int mid, int last) {
        //辅助数组
        int[] tmp = new int[last + 1 - first];
        //left、right 是检测指针，k 是进度指针
        int left = first, right = mid + 1, k = 0;
        int count = 0;
        while (left <= mid && right <= last) {
            // 取等是保证排序的稳定性！！！
            if (arr[left] <= arr[right]) {
                tmp[k++] = arr[left++];
            } else {
                tmp[k++] = arr[right++];
                count += mid - left + 1;
            }
        }

        //如果第一个序列未检测完，直接将后面所有元素加到合并的序列中
        while (left <= mid) {
            tmp[k++] = arr[left++];
        }

        //如果第二个序列未检测完，直接将后面所有元素加到合并的序列中
        while (right <= last) {
            tmp[k++] = arr[right++];
        }

        // 复制回原素组
        for (int i = 0; i <= last - first; i++) {
            arr[first + i] = tmp[i];
        }
        return count;
    }
}
```



### 易错点：

```java
class Solution {
    public int reversePairs(int[] nums) {
        return mergeSort(nums, 0, nums.length - 1);
    }

    public int mergeSort(int[] nums, int beginIndex, int lastIndex) {
        // 出错点：这个判断反了写成了：beginIndex <= lastIndex 或者 beginIndex < lastIndex
        // 这个因为不熟悉，不严谨，下意识写出来的, 
        if (beginIndex >= lastIndex) {
            return 0;
        }
        int mind = beginIndex + (lastIndex - beginIndex) / 2;
        int leftPairs = mergeSort(nums, beginIndex, mind);
        int rightPairs = mergeSort(nums, mind + 1, lastIndex);
        int mindPairs = merge(nums, beginIndex, mind, lastIndex);
        return leftPairs + rightPairs + mindPairs;
    }

    public int merge(int[] nums, int beginIndex, int mind, int lastIndex) {
        int res = 0;
        int[] tmp = new int[lastIndex - beginIndex + 1];
        int left = beginIndex;
        int right = mind + 1;
        int index = 0;
        // 出错点：left <= mind，写成了 left <= right
        // 对于 merge sort 原理不熟悉，导致了边界判读错误
        while (left <= mind && right <= lastIndex) {
            if (nums[left] <= nums[right]) {
                tmp[index++] = nums[left++];
            } else {
                tmp[index++] = nums[right++];
                // !! 注意统计逆序对数量的计算过程！！
                res += mind - left + 1;
            }
        }

        // 错误点和原因同上
        while (left <= mind) {
            tmp[index++] = nums[left++];
        }

        while (right <= lastIndex) {
            tmp[index++] = nums[right++];
        }

        // 出错点 i <= lastIndex - beginIndex，写成了 i <= nums.length - 1。这可以写成  i < nums.length - 1
        // 这完全是不够熟悉了
        for (int i = 0; i <= lastIndex - beginIndex; i++) {
            nums[beginIndex + i] = tmp[i];
        }
        return res;
    }
}
```

