作用：将本地的SVN项目上传到github上，由于空文件夹不能上传到Github上面，所以需要先将.gitkeep文件拷贝到本地空文件夹中
步骤：
1.检查文件目录结构
${FolderName}\commons-io-2.4.jar
${FolderName}\CopyFile.java
${FolderName}\git\.gitkeep


2.执行下面的命令编译运行java文件
javac -cp .;commons-io-2.4.jar CopyFile.java
java -classpath commons-io-2.4.jar; CopyFile

Note:输入参数为包含空目录的文件夹