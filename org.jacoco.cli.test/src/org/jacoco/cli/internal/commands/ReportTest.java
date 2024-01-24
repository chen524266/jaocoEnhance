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

import java.io.File;
import java.io.FileOutputStream;

import org.jacoco.cli.internal.CommandTestBase;
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

		execute("report", "H:\\jacoco\\jacoco-demo.exec", "--classfiles",
				"D:\\IdeaProjects\\code-diff\\target\\classes\\com",
				"--sourcefiles", "D:\\IdeaProjects\\code-diff\\src\\main\\java",
				"--html", "H:\\jacoco\\report2");
		assertOk();
	}

	@Test
	public void mytest3() throws Exception {

		execute("report", "H:\\jacoco\\jacoco-demo.exec", "--classfiles",
				"D:\\IdeaProjects\\code-diff\\target\\classes\\com",
				"--sourcefiles", "D:\\IdeaProjects\\code-diff\\src\\main\\java",
				"--html", "H:\\jacoco\\report", "--diffCode",
				"[\n" + "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/service/CodeDiffService\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 4,\n"
						+ "          \"startLineNum\": 2,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 27,\n"
						+ "          \"startLineNum\": 26,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"getDiffCode\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"DiffMethodParams\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/controller/CodeDiffController\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 5,\n"
						+ "          \"startLineNum\": 4,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 56,\n"
						+ "          \"startLineNum\": 53,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 77,\n"
						+ "          \"startLineNum\": 74,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 95,\n"
						+ "          \"startLineNum\": 92,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 120,\n"
						+ "          \"startLineNum\": 117,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"getGitList\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"getSvnList\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"getSvnBranchList\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"getSvnBranchReversionList\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/service/impl/CodeDiffServiceImpl\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 5,\n"
						+ "          \"startLineNum\": 2,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 36,\n"
						+ "          \"startLineNum\": 35,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"getDiffCode\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"DiffMethodParams\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/vercontrol/VersionControlHandlerFactory\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 3,\n"
						+ "          \"startLineNum\": 2,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 5,\n"
						+ "          \"startLineNum\": 4,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 30,\n"
						+ "          \"startLineNum\": 29,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 34,\n"
						+ "          \"startLineNum\": 33,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 40,\n"
						+ "          \"startLineNum\": 39,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 57,\n"
						+ "          \"startLineNum\": 51,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 60,\n"
						+ "          \"startLineNum\": 58,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n" + "          \"methodName\": \"run\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"processHandler\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"VersionControlDto\"\n"
						+ "          ]\n" + "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/config/JsonConfig\",\n"
						+ "      \"lines\": [],\n"
						+ "      \"methodInfos\": [\n" + "        {\n"
						+ "          \"methodName\": \"configureMessageConverters\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"List\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/vercontrol/git/GitAbstractVersionControl\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 15,\n"
						+ "          \"startLineNum\": 14,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 24,\n"
						+ "          \"startLineNum\": 22,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 35,\n"
						+ "          \"startLineNum\": 31,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 75,\n"
						+ "          \"startLineNum\": 67,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 84,\n"
						+ "          \"startLineNum\": 82,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 86,\n"
						+ "          \"startLineNum\": 85,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 90,\n"
						+ "          \"startLineNum\": 89,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 126,\n"
						+ "          \"startLineNum\": 125,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"getDiffCodeClasses\",\n"
						+ "          \"parameters\": []\n" + "        }\n"
						+ "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/util/MethodParserUtils\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 13,\n"
						+ "          \"startLineNum\": 10,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 15,\n"
						+ "          \"startLineNum\": 14,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 17,\n"
						+ "          \"startLineNum\": 16,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 38,\n"
						+ "          \"startLineNum\": 37,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 45,\n"
						+ "          \"startLineNum\": 44,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 55,\n"
						+ "          \"startLineNum\": 49,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 72,\n"
						+ "          \"startLineNum\": 69,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 76,\n"
						+ "          \"startLineNum\": 73,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 79,\n"
						+ "          \"startLineNum\": 78,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 83,\n"
						+ "          \"startLineNum\": 82,\n"
						+ "          \"type\": \"INSERT\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"parseMethods\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"visit\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"MethodDeclaration\",\n"
						+ "            \"List\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/common/utils/file/FileUtil\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 39,\n"
						+ "          \"startLineNum\": 0,\n"
						+ "          \"type\": \"INSERT\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [],\n"
						+ "      \"moduleName\": \"common\",\n"
						+ "      \"type\": \"ADD\"\n" + "    },\n" + "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/vercontrol/AbstractVersionControl\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 7,\n"
						+ "          \"startLineNum\": 2,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 15,\n"
						+ "          \"startLineNum\": 14,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 18,\n"
						+ "          \"startLineNum\": 17,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 50,\n"
						+ "          \"startLineNum\": 49,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 53,\n"
						+ "          \"startLineNum\": 52,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 77,\n"
						+ "          \"startLineNum\": 71,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 86,\n"
						+ "          \"startLineNum\": 85,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 97,\n"
						+ "          \"startLineNum\": 95,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 101,\n"
						+ "          \"startLineNum\": 99,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 107,\n"
						+ "          \"startLineNum\": 104,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 113,\n"
						+ "          \"startLineNum\": 112,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 122,\n"
						+ "          \"startLineNum\": 121,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 125,\n"
						+ "          \"startLineNum\": 124,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 129,\n"
						+ "          \"startLineNum\": 128,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 138,\n"
						+ "          \"startLineNum\": 137,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 144,\n"
						+ "          \"startLineNum\": 143,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 156,\n"
						+ "          \"startLineNum\": 155,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"handler\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"VersionControlDto\"\n"
						+ "          ]\n" + "        },\n" + "        {\n"
						+ "          \"methodName\": \"getDiffCodeMethods\",\n"
						+ "          \"parameters\": []\n" + "        },\n"
						+ "        {\n"
						+ "          \"methodName\": \"getClassMethods\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"DiffEntryDto\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    },\n"
						+ "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/dto/ClassInfoResult\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 71,\n"
						+ "          \"startLineNum\": 0,\n"
						+ "          \"type\": \"INSERT\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"ADD\"\n" + "    },\n" + "    {\n"
						+ "      \"classFile\": \"com/dr/code/diff/util/GitRepoUtil\",\n"
						+ "      \"lines\": [\n" + "        {\n"
						+ "          \"endLineNum\": 6,\n"
						+ "          \"startLineNum\": 5,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 10,\n"
						+ "          \"startLineNum\": 9,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 18,\n"
						+ "          \"startLineNum\": 17,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 41,\n"
						+ "          \"startLineNum\": 40,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 50,\n"
						+ "          \"startLineNum\": 49,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 62,\n"
						+ "          \"startLineNum\": 54,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 70,\n"
						+ "          \"startLineNum\": 69,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 74,\n"
						+ "          \"startLineNum\": 73,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 95,\n"
						+ "          \"startLineNum\": 94,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 113,\n"
						+ "          \"startLineNum\": 111,\n"
						+ "          \"type\": \"INSERT\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 137,\n"
						+ "          \"startLineNum\": 136,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        },\n"
						+ "        {\n" + "          \"endLineNum\": 161,\n"
						+ "          \"startLineNum\": 160,\n"
						+ "          \"type\": \"REPLACE\"\n" + "        }\n"
						+ "      ],\n" + "      \"methodInfos\": [\n"
						+ "        {\n"
						+ "          \"methodName\": \"cloneRepository\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"checkGitWorkSpace\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        },\n" + "        {\n"
						+ "          \"methodName\": \"getLocalDir\",\n"
						+ "          \"parameters\": [\n"
						+ "            \"String\",\n"
						+ "            \"String\",\n"
						+ "            \"String\"\n" + "          ]\n"
						+ "        }\n" + "      ],\n"
						+ "      \"moduleName\": \"application\",\n"
						+ "      \"type\": \"MODIFY\"\n" + "    }\n" + "  ]");
		assertOk();
	}

	@Test
	public void should_use_all_values_when_multiple_classfiles_options_are_provided()
			throws Exception {
		File html = new File(tmp.getRoot(), "coverage");

		final String c1 = getClassPath()
				+ "/org/jacoco/cli/internal/commands/ReportTest.class";
		final String c2 = getClassPath()
				+ "/org/jacoco/cli/internal/commands/DumpTest.class";

		execute("report", "--classfiles", c1, "--classfiles", c2, "--html",
				html.getAbsolutePath());

		assertOk();
		assertTrue(html.isDirectory());
		assertTrue(new File(html,
				"org.jacoco.cli.internal.commands/ReportTest.html").isFile());
		assertTrue(
				new File(html, "org.jacoco.cli.internal.commands/DumpTest.html")
						.isFile());
	}

}
