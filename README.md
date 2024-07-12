### 简介
 
 最新版本支持自定义数据结构，如请求者IP，以IP划分探针数据，此版本暂时不考虑开源，需付费支持。
 
 基于ray大佬的分支二开，感谢大佬的无私奉献。jacoco二开，增量覆盖率和不同时间节点的不同class代码覆盖率合并功能。
 思路请参考博文https://blog.csdn.net/qq_34418450/article/details/135386280?spm=1001.2014.3001.5501
 这里不再赘述。
 如果大家不想编译，可以使用从发布版中下载我已经编译好的agent包和cli包
 
 编译方法:已经去掉了一些不必要的模块和插件，直接执行mvn命令:mvn clean install -Dmaven.test.skip=true -Dmaven.javadoc.skip=true
使用方法:

--diffcode是增量统计，不带则统计全量。可以自己生成这个数据，或者使用ray开源的https://gitee.com/Dray/code-diff获取，支持传入文件参数--diffCodeFiles
--onlyMergeExec=true 不生成报告，仅合并探针数据，生成合并后的exec文件 
--mergeExecfilepath 需要合并的exec探针文件
 --mergeClassfilepath 需要合并的class文件路径
 --mergeExec 探针数据合并后生成新的exec文件路径 
 
测试例子，请参考测试类
org.jacoco.cli.internal.commands.ReportTest.mytest4()

合并探针数据生成exec的例子，建议在项目中引入jacoco-cli包，在代码中直接执行命令模式: 
		File html = new File("D:\\home");
		new Main().execute("report", "D:\\jacoco_merge_test.exec", 
		"--classfiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\bbzx\\com",
		"--classfiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\common\\com",
		"--classfiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportsupport\\com",
		"--classfiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportview\\com",
		"--classfiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\website\\com",
        "--mergeExecfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616785_616788\\execfiles\\jacoco20240109165235.exec",
        "--mergeClassfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\bbzx\\com",
        "--mergeClassfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportview\\com",
        "--mergeClassfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\common\\com",
        "--mergeClassfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportsupport\\com",
        "--mergeClassfilepath","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\website\\com",
        "--sourcefiles","D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\sourcefiles",
        "--onlyMergeExec", "true",
        "--mergeExec", "D:\\jacoco_merge_test.exec",
        "--html", html.getAbsolutePath());
或者通过jacoco-cli.jar包调用方式，java -jar  jacoco-cli.jar report --diffCode diff  --classfiles classdir --args value等等    

jar包方式执行如下(jdk1.8):

1. 直接生成报告的命令
java -jar org.jacoco.cli-0.8.7-SNAPSHOT-nodeps.jar report F:\webDemo\exec\2.exec --classfiles F:\webDemo\target\classes\ --mergeExecfilepath F:\webDemo\exec\1.exec --mergeClassfilepath F:\webDemo\classes_1\ --diffCodeFiles F:\home\code_diff\webDemo\1111\diff.json --sourcefiles F:\webDemo\src\main\java --html F:\webDemo\exec\report_diff_2


2. 合并exec
java -jar org.jacoco.cli-0.8.7-SNAPSHOT-nodeps.jar report F:\webDemo\exec\2.exec --classfiles F:\webDemo\target\classes\ --mergeExecfilepath F:\webDemo\exec\1.exec --mergeClassfilepath F:\webDemo\classes_1\ --onlyMergeExec true --mergeExec F:\webDemo\exec\merged.exec

第二次合并, 之前合并的exec文件，mergeClassfilepath参数设置为最后一次合并版本的class路径,举例

合并exec的时候，需要指定 mergeClassfilepath
现在A.exec 和B.exec 合并 成AB.exec
此时又来一个C.exec
AB.exec和C.exec合并的时候， AB.exec 的 mergeClassfilepath 应为B的class文件夹路径



3.生成增量报告
java -jar org.jacoco.cli-0.8.7-SNAPSHOT-nodeps.jar report F:\webDemo\exec\merged.exec --classfiles F:\webDemo\target\classes --sourcefiles F:\webDemo\src\main\java --html F:\webDemo\exec\report_diff --diffCodeFiles F:\home\code_diff\webDemo\1111\diff.json --encoding=utf8

4.生成全量报告
java -jar org.jacoco.cli-0.8.7-SNAPSHOT-nodeps.jar report F:\webDemo\exec\merged.exec --classfiles F:\webDemo\target\classes --sourcefiles F:\webDemo\src\main\java --html F:\webDemo\exec\report_full

   如有任何疑问，可以加作者微信群交流    
![输入图片说明](image.png)
