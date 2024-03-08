### 简介
      
 基于ray大佬的分支二开，感谢大佬的无私奉献。jacoco二开，增量覆盖率和不同时间节点的不同class代码覆盖率合并功能。
 思路请参考博文https://blog.csdn.net/qq_34418450/article/details/135386280?spm=1001.2014.3001.5501
 这里不再赘述。
 如果大家不想编译，可以使用从发布版中下载我已经编译好的agent包和cli包
使用方法:

--diffcode是增量统计，不带则统计全量。可以自己生成这个数据，或者使用ray开源的https://gitee.com/Dray/code-diff获取，支持传入文件参数--diffCodeFiles
--onlyMergeExec=true 不生成报告，仅合并探针数据，生成合并后的exec文件 
--mergeExecfilepath 需要合并的exec探针文件
 --mergeClassfilepath 需要合并的class文件路径
 --mergeExec 探针数据合并后生成新的exec文件路径 

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
