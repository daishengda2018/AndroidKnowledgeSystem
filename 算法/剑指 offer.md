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

