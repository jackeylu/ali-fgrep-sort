# Question
一道阿里的笔试题：

用Java实现以下功能，要求使用多线程，打印语句直接使用
print(xx)，可以不用写try catch

```shell script
cat /home/user/logs/*.log | grep 'Login' | sort | uniq -c | sort -nr 
```

# 解题
1. 在一堆文本文件中找出所有带`Login`的行
2. 将这些文本行排序后去重，并给出每一行出现的次数
3. 根据出现次数重新排序，按次数降序排列。

# 问题简化
将Linux Shell中的正则进行了简化：

1. `*.log` 被简化成文件名是`endWith(".log")`
2. `grep 'Login'`被简化成`s.contain("Login")`这种形式


