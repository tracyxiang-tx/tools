作用：获取SVN提交日志并且格式化成html
步骤：

1.获取SVN日志
svn log G:\SVN\source\ACDP  -v -r {2014-7-15}:{2015-7-19} --xml > D:\svnCommitLogs.xml
2.格式化日志
java -jar svnlogs.jar "P:\\log\\svnCommitLogs.xml"