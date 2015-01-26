mysql



mysql -h  -uxxx --default-character-set=gbk

mysql> insert into test2 values('你好', '你好');
Query OK, 1 row affected (0.04 sec)

mysql> select * from test2;
+-------+-------+
| name1 | name2 |
+-------+-------+
| 你好  | 你好  |
+-------+-------+
1 row in set (0.01 sec)

mysql> show create table test2;
+-------+-------------------------------------------------------
--------------------------------------------------------+
| Table | Create Table
                                                        |
+-------+-------------------------------------------------------
--------------------------------------------------------+
| test2 | CREATE TABLE `test2` (
  `name1` varchar(2) DEFAULT NULL,
  `name2` char(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 |
+-------+-------------------------------------------------------
--------------------------------------------------------+
1 row in set (0.00 sec)

mysql>


mysql> select length(name1), length(name2) from test2;
+---------------+---------------+
| length(name1) | length(name2) |
+---------------+---------------+
|             6 |             6 |
+---------------+---------------+
1 row in set (0.00 sec)







mysql -h -uxxx --default-character-set=latin1


mysql>
mysql>
mysql> show create table test2;
+-------+----------------------------------------------------------------------
---------------------------------------------------------+
| Table | Create Table
                                                         |
+-------+----------------------------------------------------------------------
---------------------------------------------------------+
| test2 | CREATE TABLE `test2` (
  `name1` varchar(2) DEFAULT NULL,
  `name` char(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 |
+-------+----------------------------------------------------------------------
---------------------------------------------------------+
1 row in set (0.04 sec)

mysql> insert into test2 values('你好', '你好');
Query OK, 1 row affected, 2 warnings (0.04 sec)

mysql> show warnings;
+---------+------+--------------------------------------------+
| Level   | Code | Message                                    |
+---------+------+--------------------------------------------+
| Warning | 1265 | Data truncated for column 'name1' at row 1 |
| Warning | 1265 | Data truncated for column 'name' at row 1  |
+---------+------+--------------------------------------------+
2 rows in set (0.04 sec)

mysql> select * from test2;
+-------+------+
| name1 | name |
+-------+------+
| 你    | 你   |
+-------+------+
1 row in set (0.04 sec)


mysql> select length(name1), length(name) from test2;
+---------------+--------------+
| length(name1) | length(name) |
+---------------+--------------+
|             2 |            2 |
+---------------+--------------+
1 row in set (0.04 sec)

mysql> quit


字符集
http://www.laruence.com/2008/01/05/12.html
http://blog.csdn.net/ithomer/article/details/5130789
java 字符集
http://www.ibm.com/developerworks/cn/java/j-lo-chinesecoding/
http://blog.csdn.net/ns_code/article/details/14162087
http://zh.wikipedia.org/wiki/Unicode


Mysql 查看连接数,状态
http://blog.csdn.net/starnight_cbj/article/details/4492555
show status;

show variables like '%max_connections%';






