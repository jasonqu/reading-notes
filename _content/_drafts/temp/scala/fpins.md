https://github.com/fpinscala/fpinscala

https://github.com/fpinscala/fpinscala/wiki

wiki 有笔记：

第一章
p10 referencial transparency and purity 参考透明性定义
https://github.com/fpinscala/fpinscala/wiki/Chapter-1:-What-is-functional-programming%3F#notes

第二章
def binarySearch[@specialized A](as: Array[A], key: A,
	gt: (A,A) => Boolean): Int
specialized注解可以去掉不必要的装箱来优化性能，不过受制于其它函数和数据结构，这个优化可能会打折扣

