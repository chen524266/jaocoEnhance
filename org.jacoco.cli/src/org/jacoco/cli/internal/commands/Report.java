/*******************************************************************************
 * Copyright (c) 2009, 2021 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    John Keeping - initial implementation
 *    Marc R. Hoffmann - rework
 *
 *******************************************************************************/
package org.jacoco.cli.internal.commands;

import org.jacoco.cli.internal.Command;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.internal.diff.JsonReadUtil;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.*;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>report</code> command.
 */
public class Report extends Command {

	@Argument(usage = "list of JaCoCo *.exec files to read", metaVar = "<execfiles>")
	List<File> execfiles = new ArrayList<File>();

	@Option(name = "--classfiles", usage = "location of Java class files", metaVar = "<path>", required = true)
	List<File> classfiles = new ArrayList<File>();

	@Option(name = "--sourcefiles", usage = "location of the source files", metaVar = "<path>")
	List<File> sourcefiles = new ArrayList<File>();

	// 多版本覆盖率合并用，需要合并版本的exec文件
	@Option(name = "--mergeExecfilepath", usage = "need to merge execfile", metaVar = "<path>")
	List<File> mergeExecfiles = new ArrayList<File>();;

	// 需要合并版本的class文件，插桩必须要用到老的class文件
	@Option(name = "--mergeClassfilepath", usage = "location of Java class files need to merge", metaVar = "<path>")
	List<File> mergeClassfiles = new ArrayList<File>();;

	@Option(name = "--diffCode", usage = "input String for diff", metaVar = "<file>")
	String diffCode;

	@Option(name = "--diffCodeFiles", usage = "input file for diff", metaVar = "<path>")
	String diffCodeFiles;

	@Option(name = "--tabwith", usage = "tab stop width for the source pages (default 4)", metaVar = "<n>")
	int tabwidth = 4;

	@Option(name = "--name", usage = "name used for this report", metaVar = "<name>")
	String name = "JaCoCo Coverage Report";

	@Option(name = "--encoding", usage = "source file encoding (by default platform encoding is used)", metaVar = "<charset>")
	String encoding;

	@Option(name = "--onlyMergeExec", usage = "only merger exec,not create report", metaVar = "<charset>")
	String onlyMergeExec;

	@Option(name = "--mergeExec", usage = "only merger exec,not create report", metaVar = "<charset>")
	String mergeExec;

	@Option(name = "--xml", usage = "output file for the XML report", metaVar = "<file>")
	File xml;

	@Option(name = "--csv", usage = "output file for the CSV report", metaVar = "<file>")
	File csv;

	@Option(name = "--html", usage = "output directory for the HTML report", metaVar = "<dir>")
	File html;

	@Override
	public String description() {
		return "Generate reports in different formats by reading exec and Java class files.";
	}

	@Override
	public int execute(final PrintWriter out, final PrintWriter err)
			throws IOException {
		// 需要合并exec文件，同个方法就合并方法的指令的覆盖率
		if (this.mergeExecfiles.size() != 0
				&& this.mergeClassfiles.size() != 0) {
			final ExecFileLoader loader = loadExecutionData(out,
					mergeExecfiles);
			analyze(loader.getExecutionDataStore(), out, mergeClassfiles, true);
		}
		try {
			StringWriter nowoutStringWriter = new StringWriter();
			PrintWriter nowout = new PrintWriter(nowoutStringWriter);
			final ExecFileLoader loader = loadExecutionData(out, this.execfiles);
			final IBundleCoverage bundle = analyze(loader.getExecutionDataStore(), nowout, classfiles, false);
			// 只合并exec文件，不生成报告
			if (onlyMergeExec != null && onlyMergeExec.equals("true")) {
				loader.save(new File(mergeExec), true);
			} else {
				writeReports(bundle, loader, nowout);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ExecFileLoader.instrunctionsThreadLocal.remove();
			ExecFileLoader.classInfo.remove();
		}
		return 0;
	}

	/**
	 * 加载exec文件
	 *
	 * @param out
	 * @return
	 * @throws IOException
	 */
	private ExecFileLoader loadExecutionData(final PrintWriter out,
			List<File> execfiles) throws IOException {
		final ExecFileLoader loader = new ExecFileLoader();
		if (execfiles.isEmpty()) {
			out.println("[WARN] No execution data files provided.");
		} else {
			for (final File file : execfiles) {
				out.printf("[INFO] Loading execution data file %s.%n",
						file.getAbsolutePath());
				loader.load(file);
			}
		}
		return loader;
	}

	private IBundleCoverage analyze(final ExecutionDataStore data,
			final PrintWriter out, List<File> classfiles, boolean isOnlyAnaly)
			throws IOException {
		CoverageBuilder builder;
		// 如果有增量参数将其设置进去
		if (null != this.diffCodeFiles) {
			builder = new CoverageBuilder(
					JsonReadUtil.readJsonToString(this.diffCodeFiles));
		} else if (null != this.diffCode) {
			builder = new CoverageBuilder(this.diffCode);
		} else {
			builder = new CoverageBuilder();
		}
		builder.setOnlyAnaly(isOnlyAnaly);
		final Analyzer analyzer = new Analyzer(data, builder);
		// class类用于类方法的比较，源码只用于最后的着色
		for (final File f : classfiles) {
			analyzer.analyzeAll(f);
		}
		printNoMatchWarning(builder.getNoMatchClasses(), out);
		return builder.getBundle(name);
	}

	private void printNoMatchWarning(final Collection<IClassCoverage> nomatch,
			final PrintWriter out) {
		if (!nomatch.isEmpty()) {
			out.println(
					"[WARN] Some classes do not match with execution data.");
			out.println(
					"[WARN] For report generation the same class files must be used as at runtime.");
			for (final IClassCoverage c : nomatch) {
				out.printf(
						"[WARN] Execution data for class %s does not match.%n",
						c.getName());
			}
		}
	}

	private void writeReports(final IBundleCoverage bundle,
			final ExecFileLoader loader, final PrintWriter out)
			throws IOException {
		out.printf("[INFO] Analyzing %s classes.%n",
				Integer.valueOf(bundle.getClassCounter().getTotalCount()));
		final IReportVisitor visitor = createReportVisitor();
		visitor.visitInfo(loader.getSessionInfoStore().getInfos(),
				loader.getExecutionDataStore().getContents());
		visitor.visitBundle(bundle, getSourceLocator());
		visitor.visitEnd();
	}

	private IReportVisitor createReportVisitor() throws IOException {
		final List<IReportVisitor> visitors = new ArrayList<IReportVisitor>();

		if (xml != null) {
			final XMLFormatter formatter = new XMLFormatter();
			visitors.add(formatter.createVisitor(new FileOutputStream(xml)));
		}

		if (csv != null) {
			final CSVFormatter formatter = new CSVFormatter();
			visitors.add(formatter.createVisitor(new FileOutputStream(csv)));
		}

		if (html != null) {
			final HTMLFormatter formatter = new HTMLFormatter();
			visitors.add(
					formatter.createVisitor(new FileMultiReportOutput(html)));
		}

		return new MultiReportVisitor(visitors);
	}

	private ISourceFileLocator getSourceLocator() {
		final MultiSourceFileLocator multi = new MultiSourceFileLocator(
				tabwidth);
		for (final File f : sourcefiles) {
			multi.add(new DirectorySourceFileLocator(f, encoding, tabwidth));
		}
		return multi;
	}

}
