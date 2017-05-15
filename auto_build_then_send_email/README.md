作用：编译AA和ACA的源代码，如果出错则发邮件和编译log
步骤：
1.替换start.bat中的参数
start /wait F:\tracy\autoBuild\autoBuild.bat AA_Prod AA_8.0.2.0.0_FP E:\GitHub-Code "peter.peng@missionsky.com,louis.he@missionsky.com" "leo.liu@missionsky.com,accela-support-team@missionsky.com"
 F:\tracy\autoBuild\autoBuild.bat：替换为autoBuild.bat的绝对路径
 AA_Prod： 产品名：AA_Prod或者ACA_Prod
 AA_8.0.2.0.0_FP：产品名对应的分支名
 E:\GitHub-Code：产品名所在目录
 "peter.peng@missionsky.com,louis.he@missionsky.com"： 需要发送的邮件地址，多个邮件用逗号隔开
 "leo.liu@missionsky.com,accela-support-team@missionsky.com": 需要抄送的邮件地址，多个邮件用逗号隔开

2.替换1中autoBuild.bat的参数
 F:\tracy\autoBuild:本地放文件的目录
 
3.在命令行运行start.bat

