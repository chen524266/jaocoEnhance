/*******************************************************************************
 * Copyright (c) 2009, 2021 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.cli.internal.commands;

import static org.junit.Assert.assertTrue;

import java.io.*;

import org.jacoco.cli.internal.CommandTestBase;
import org.jacoco.cli.internal.Main;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for {@link Report}.
 */
public class ReportTest extends CommandTestBase {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Test
	public void should_print_usage_when_no_options_are_given()
			throws Exception {
		execute("report");
		assertFailure();
		assertContains("\"--classfiles\"", err);
		assertContains(
				"Usage: java -jar jacococli.jar report [<execfiles> ...]", err);
	}

	@Test
	public void should_print_warning_when_no_exec_files_are_provided()
			throws Exception {
		execute("report", "--classfiles", getClassPath());

		assertOk();
		assertContains("[WARN] No execution data files provided.", out);
	}

	@Test
	public void should_print_number_of_analyzed_classes() throws Exception {
		execute("report", "--classfiles", getClassPath());

		assertOk();
		assertContains("[INFO] Analyzing 14 classes.", out);
	}

	@Test
	public void should_print_warning_when_exec_data_does_not_match()
			throws Exception {
		File exec = new File(tmp.getRoot(), "jacoco.exec");
		final FileOutputStream execout = new FileOutputStream(exec);
		ExecutionDataWriter writer = new ExecutionDataWriter(execout);
		// Add probably invalid id for this test class:
		writer.visitClassExecution(
				new ExecutionData(0x123, getClass().getName().replace('.', '/'),
						new boolean[] { true }));
		execout.close();

		execute("report", exec.getAbsolutePath(), "--classfiles",
				getClassPath());

		assertOk();
		assertContains("[WARN] Some classes do not match with execution data.",
				out);
		assertContains(
				"[WARN] For report generation the same class files must be used as at runtime.",
				out);
		assertContains(
				"[WARN] Execution data for class org/jacoco/cli/internal/commands/ReportTest does not match.",
				out);
	}

	@Test
	public void megerReport() throws Exception {
		File html = new File("D:\\home");
		execute("report", "D:\\jacoco_merge_test.exec", "--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\bbzx\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\common\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportsupport\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportview\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\website\\com",
				// "--mergeExecfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616785_616788\\execfiles\\jacoco20240109165235.exec",
				// "--mergeClassfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\bbzx\\com",
				// "--mergeClassfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportview\\com",
				// "--mergeClassfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\common\\com",
				// "--mergeClassfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\reportsupport\\com",
				// "--mergeClassfilepath",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\classfiles\\website\\com",
				// "--sourcefiles",
				// "D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\616788\\sourcefiles",
				// "--onlyMergeExec", "true",
				// "--mergeExec", "D:\\jacoco_merge_test.exec",
				"--html", html.getAbsolutePath());
		assertOk();
	}

	@Test
	public void should_create_xml_report_when_xml_option_is_provided()
			throws Exception {
		File xml = new File(tmp.getRoot(), "coverage.xml");

		execute("report", "--classfiles", getClassPath(), "--xml",
				xml.getAbsolutePath());

		assertOk();
		assertTrue(xml.isFile());
	}

	@Test
	public void should_create_csv_report_when_csv_option_is_provided()
			throws Exception {
		File csv = new File(tmp.getRoot(), "coverage.csv");

		execute("report", "--classfiles", getClassPath(), "--csv",
				csv.getAbsolutePath());

		assertOk();
		assertTrue(csv.isFile());
	}

	@Test
	public void should_create_html_report_when_html_option_is_provided()
			throws Exception {
		File html = new File("D:\\temp");
		execute("report",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617559_617604\\execfiles\\jacoco20240104110844.exec",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\classfiles\\bbzx\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\classfiles\\common\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\classfiles\\reportsupport\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\classfiles\\reportview\\com",
				"--classfiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\classfiles\\website\\com",
				"--sourcefiles",
				"D:\\gitworkplace\\luckframeweb\\coverage\\trunk\\617604\\sourcefiles",
				"--html", html.getAbsolutePath());
		// execute("report", "D:\\home\\jacoco_new.exec","--classfiles",
		// "D:\\gitworkplace\\luckframeweb\\target\\classes\\com\\luckyframe","--sourcefiles","D:\\gitworkplace\\luckframeweb\\src\\main\\java",
		// "--html", html.getAbsolutePath());
		assertOk();
		assertTrue(html.isDirectory());
	}

	@Test
	public void mytest() throws Exception {

		execute("report", "D:\\jacoco\\jacoco-demo.exec", "--classfiles",
				"D:\\IdeaProjects\\base\\base-service\\application\\target\\classes\\com",
				"--sourcefiles",
				"D:\\IdeaProjects\\base\\base-service\\application\\src\\main\\java ",
				"--html", "D:\\jacoco\\report", "--xml",
				"D:\\jacoco\\report.xml", "--diffCode",
				" [\n" + "    {\n"
						+ "      \"classFile\": \"com/dr/application/app/controller/LoginController\",\n"
						+ "      \"methodInfos\": [\n" + "        {\n"
						+ "          \"methodName\": \"testInt\",\n"
						+ "          \"parameters\": \"Map<String,Object>&List<String>&Set<Integer>\"\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"display\",\n"
						+ "          \"parameters\": \"\"\n" + "        },\n"
						+ "        {\n" + "          \"methodName\": \"a\",\n"
						+ "          \"parameters\": \"InnerClass\"\n"
						+ "        }\n" + "      ],\n"
						+ "      \"type\": \"MODIFY\"\n" + "    }\n" + "  ]");
		// execute("report","D:\\jacoco\\jacoco-demo.exec", "--classfiles",
		// "D:\\IdeaProjects\\base\\base-service\\application\\target\\classes\\com",
		// "--sourcefiles",
		// "D:\\IdeaProjects\\base\\base-service\\application\\src\\main\\java
		// ", "--html", "D:\\jacoco\\all\\report","--xml",
		// "D:\\jacoco\\report.xml");

		assertOk();
		// assertTrue(html.isDirectory());
		// assertTrue(new File(html,
		// "org.jacoco.cli.internal.commands/ReportTest.html").isFile());
		// assertTrue(new File(html,
		// "org.jacoco.cli.internal.commands/ReportTest.java.html")
		// .isFile());
	}

	@Test
	public void mytest2() throws Exception {

		execute("report", "/Users/rayduan/jacoco/jacoco-demo.exec",
				"--classfiles",
				"/Users/rayduan/IdeaProjects\\base\\base-service\\application\\target\\classes\\com",
				"--sourcefiles",
				"/Users/rayduan/IdeaProjects\\base\\base-service\\application\\src\\main\\java ",
				"--html", "/Users/rayduan/jacoco/report", "--xml",
				"/Users/rayduan/jacoco/report.xml", "--diffCodeFiles", "");
		assertOk();
	}

	@Test
	public void mytest4() throws Exception {
		File html = new File("D:\\temp\\new\\report");
		StringWriter out = new StringWriter();
		new Main("report", "D:\\temp\\测试(2)\\测试\\第一次\\jacoco.exec", "--classfiles",
				"D:\\temp\\测试(2)\\测试\\第一次\\",
				"--mergeExecfilepath", "D:\\temp\\测试(2)\\测试\\第二次\\jacoco.exec",
				"--mergeClassfilepath","D:\\temp\\测试(2)\\测试\\第二次\\",
				"--sourcefiles",
				"D:\\temp\\测试(2)\\测试\\第一次",
//				"--onlyMergeExec", "true",
//				"--mergeExec", "D:\\temp\\jacoco.exec",
				"--html", html.getAbsolutePath()).execute(new PrintWriter(out),new PrintWriter(out));
	}
}
