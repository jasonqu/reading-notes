核心逻辑是这个

一个皇后q(x,y)能被满足以下条件的皇后q(row,col)吃掉
1）x=row(在纵向不能有两个皇后)
2)  y=col（横向）
3）col + row = y+x;（斜向正方向）
4)  col - row = y-x;（斜向反方向）

http://www.cnblogs.com/jillzhang/archive/2007/10/21/922830.html

算法就是回溯

http://rosettacode.org/wiki/N-queens_problem#Java

