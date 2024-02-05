### 简介
      基于ray大佬的分支二开，感谢大佬的无私奉献。jacoco二开，增量覆盖率和不同时间节点的不同class代码覆盖率合并功能。
 思路请参考博文https://blog.csdn.net/qq_34418450/article/details/135386280?spm=1001.2014.3001.5501
 这里不再赘述。

使用方法:

--diffcode是增量统计，不带则统计全量。可以自己生成这个数据，或者使用ray开源的https://gitee.com/Dray/code-diff获取，支持传入文件参数--diffCodeFiles
--onlyMergeExec=true 不生成报告，仅合并探针数据，生成合并后的exec文件 
--mergeExecfilepath 需要合并的exec探针文件
 --mergeClassfilepath 需要合并的class文件路径
 --mergeExec 探针数据合并后生成新的exec文件路径 

合并探针数据生成exec的例子，建议在项目中引入jacoco-cli包，在代码中直接执行命令模式: 
         new Main("report",
                    current.getExec(),
                  "--diffCode", diff,
                    "--classfiles", currentBaseclassFiles + ProjectCoverageServiceImpl.bbzxDir + File.separator + "com",
                    "--classfiles", currentBaseclassFiles + ProjectCoverageServiceImpl.reportViewDir + File.separator + "com",
                    "--classfiles", currentBaseclassFiles + ProjectCoverageServiceImpl.websiteDir + File.separator + "com",
                    "--classfiles", currentBaseclassFiles + ProjectCoverageServiceImpl.commonDir + File.separator + "com",
                    "--classfiles", currentBaseclassFiles + ProjectCoverageServiceImpl.reportSupportDir + File.separator + "com",
                    "--mergeClassfilepath", mergeBaseclassFiles + ProjectCoverageServiceImpl.bbzxDir + File.separator + "com",
                    "--mergeClassfilepath", mergeBaseclassFiles + ProjectCoverageServiceImpl.reportViewDir + File.separator + "com",
                    "--mergeClassfilepath", mergeBaseclassFiles + ProjectCoverageServiceImpl.websiteDir + File.separator + "com",
                    "--mergeClassfilepath", mergeBaseclassFiles + ProjectCoverageServiceImpl.commonDir + File.separator + "com",
                    "--mergeClassfilepath", mergeBaseclassFiles + ProjectCoverageServiceImpl.reportSupportDir + File.separator + "com",
                    "--mergeExecfilepath", report.getExec(),
                    "--sourcefiles", baseCoverageDir + current.getSvnType() + File.separator + current.getSvnEnd() + File.separator + ProjectCoverageServiceImpl.sourcefiles + File.separator + ProjectCoverageServiceImpl.bbzxDir,
                    "--sourcefiles", baseCoverageDir + current.getSvnType() + File.separator + current.getSvnEnd() + File.separator + ProjectCoverageServiceImpl.sourcefiles + File.separator + ProjectCoverageServiceImpl.reportViewDir,
                    "--sourcefiles", baseCoverageDir + current.getSvnType() + File.separator + current.getSvnEnd() + File.separator + ProjectCoverageServiceImpl.sourcefiles + File.separator + ProjectCoverageServiceImpl.websiteDir,
                    "--sourcefiles", baseCoverageDir + current.getSvnType() + File.separator + current.getSvnEnd() + File.separator + ProjectCoverageServiceImpl.sourcefiles + File.separator + ProjectCoverageServiceImpl.commonDir,
                    "--sourcefiles", baseCoverageDir + current.getSvnType() + File.separator + current.getSvnEnd() + File.separator + ProjectCoverageServiceImpl.sourcefiles + File.separator + ProjectCoverageServiceImpl.reportSupportDir,
                    "--onlyMergeExec", "true",
                    "--mergeExec", mergeTempExec).execute(new PrintWriter(outS),
                    new PrintWriter(err));
或者通过jacoco-cli.jar包调用方式，java -jar  jacoco-cli.jar report --diffCode diff  --classfiles classdir --args value等等            
