http://book.douban.com/subject/3519369/
http://cs.ecs.baylor.edu/~donahoo/practical/JavaSockets/textcode.html


选项，没啥用处
http://docs.oracle.com/javase/8/docs/technotes/guides/net/socketOpt.html
http://elf8848.iteye.com/blog/1739598

TCP连接的状态详解以及故障排查
http://network.51cto.com/art/201408/449186_all.htm

关闭链接
http://www.cnblogs.com/fczjuever/archive/2013/04/05/3000680.html
http://www.mathcs.emory.edu/~cheung/Courses/455/Syllabus/7-transport/tcp3.html


jdbc的关闭
http://blog.shinetech.com/2007/08/04/how-to-close-jdbc-resources-properly-every-time/
some dodgy JDBC drivers can do this – I once used one that would leave the underlying database connection open if you didn’t close the ResultSet – even if you closed the Connection object

http://stackoverflow.com/questions/4507440/must-jdbc-resultsets-and-statements-be-closed-separately-although-the-connection
http://www.selikoff.net/2008/07/30/finally-closing-jdbc-resources/

http://k1121.iteye.com/blog/1279063
JDBC数据库连接池connection关闭后Statement和ResultSet未关闭的问题

规范说明：connection.close 自动关闭 Statement.close 自动导致 ResultSet 对象无效(注意只是 ResultSet 对象无效，ResultSet 所占用的资源可能还没有释放)。所以还是应该显式执行connection、Statement、ResultSet的close方法。特别是在使用connection pool的时候，connection.close 并不会导致物理连接的关闭，不执行ResultSet的close可能会导致更多的资源泄露。 

V6使用的是数据库连接池，Connection关闭并不是物理关闭，只是归还连接池，所以Statement和ResultSet有可能被持有，并且实际占用相关的数据库的游标资源，在这种情况下，只要长期运行就有可能报“游标超出数据库允许的最大值”的错误，导致程序无法正常访问数据库。 


jsbc pooling
http://guosxu.iteye.com/blog/1270522
没有选别的，buggy或者是没有github
选择这个
https://github.com/brettwooldridge/HikariCP


