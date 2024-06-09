/*
 * Copyright 2024 Mathieu Cartoixa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcartoixa.ant.sf.apex.run;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import com.mcartoixa.ant.sf.apex.TestLevel;
import com.mcartoixa.ant.sf.apex.TestNameWrapper;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer;
import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.apache.tools.ant.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Mathieu Cartoixa
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TestClassWithoutTestCases"})
public class RunTestTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            final JSONObject result = json.optJSONObject("result");
            if (result != null) {
                final JSONObject summary = result.optJSONObject("summary");
                if (summary != null) {
                    this.log(
                            String.format(
                                    "\nTests run: %d, Failures: %d, Skipped: %d, Time elapsed: %s\nCode coverage: %s\n\n",
                                    summary.optInt("testsRan"),
                                    summary.optInt("failing"),
                                    summary.optInt("skipped"),
                                    summary.optString("testExecutionTime"),
                                    summary.optString("testRunCoverage")
                            ),
                            Project.MSG_INFO
                    );
                }

                final JSONArray tests = result.optJSONArray("tests");
                if (tests != null) {
                    for (int i = 0; i < tests.length(); i++) {
                        final JSONObject test = tests.getJSONObject(i);
                        switch (test.getString("Outcome")) {
                            case "Fail":
                                this.log(
                                        String.format(
                                                "Testcase: %s:\tFAILED\n%s\n\tat %s\n\n",
                                                test.optString("FullName"),
                                                test.optString("Message"),
                                                test.optString("StackTrace")
                                        ),
                                        Project.MSG_ERR
                                );
                                if (RunTestTask.this.getFailOnError()) {
                                    RunTestTask.this.setErrorMessage("Some tests failed; see above for details.");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            switch (key) {
                case "testrunid":
                    RunTestTask.this.setRunId(value);
                    this.log(
                            String.format(
                                    "Test run %s was launched",
                                    value
                            ),
                            Project.MSG_INFO
                    );
                    break;
                default:
                    break;
            }
        }
    }

    private final static class TempDirectoryHelper {

        private TempDirectoryHelper() {
        }

        // temporary directory location
        private static final File TMPDIR = new File(System.getProperty("java.io.tmpdir"));

        @SuppressWarnings("PMD.DefaultPackage")
        /* default */ static File location() {
            return TMPDIR;
        }

        // file name generation
        private static final SecureRandom RANDOM = new SecureRandom();

        @SuppressWarnings("PMD.DefaultPackage")
        /* default */ static File generateDirectory(final String prefix, final String suffix, final File dir)
                throws IOException {
            long n = RANDOM.nextLong();
            if (n == Long.MIN_VALUE) {
                n = 0;      // corner case
            } else {
                n = Math.abs(n);
            }

            // Use only the file name from the supplied prefix
            final String p = new File(prefix).getName();

            final String name = p + Long.toString(n) + suffix;
            return new File(dir, name);
        }
    }

    public RunTestTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "apex", "run", "test" };
    }

    public void addConfiguredClass(final TestNameWrapper className) {
        this.classes.add(className.getName());
    }

    public void addConfiguredSuite(final TestNameWrapper suiteName) {
        this.suites.add(suiteName.getName());
    }

    public void addConfiguredTest(final TestNameWrapper test) {
        this.tests.add(test.getName());
    }

    public AggregateTransformer createReport() {
        final AggregateTransformer transformer = new AggregateTransformer(this.getXMLResultAggregator());
        transformers.add(transformer);
        return transformer;
    }

    public void setSynchronous(final boolean synchronous) {
        if (synchronous) {
            getCommandline().createArgument().setValue("--synchronous");
        }
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    public void setTestLevel(final TestLevel level) {
        getCommandline().createArgument().setValue("--test-level");
        getCommandline().createArgument().setValue(level.name());
    }

    public void setToDir(final File toDir) {
        if (toDir != null) {
            this.toDir = !toDir.exists() || toDir.isDirectory() ? toDir : toDir.getParentFile();

            getCommandline().createArgument().setValue("--output-dir");
            getCommandline().createArgument().setValue(toDir.getAbsolutePath());
        }
    }

    public void setToFile(final String toFile) {
        if (toFile != null) {
            this.toFile = toFile;
        }
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    protected void checkConfiguration() {
        if (!this.classes.isEmpty() && !this.suites.isEmpty()) {
            throw new BuildException("The class and suite nested elements are mutually exclusive.");
        }

        super.checkConfiguration();
    }

    @Override
    protected void prepareContext() {
        if (this.getToDir() == null) {
            try {
                final File td = TempDirectoryHelper.generateDirectory("test-result-", "", TempDirectoryHelper.location());
                td.mkdirs();
                this.setToDir(td);
            } catch (IOException ex) {
                throw new BuildException(ex, getLocation());
            }
        }

        super.prepareContext();
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    protected void createArguments() {
        if (!this.classes.isEmpty()) {
            getCommandline().createArgument().setValue("--class-names");
            getCommandline().createArgument().setValue(String.join(",", classes));
        } else if (!this.suites.isEmpty()) {
            getCommandline().createArgument().setValue("--suite-names");
            getCommandline().createArgument().setValue(String.join(",", suites));
        }
        for (final String t: this.tests) {
            getCommandline().createArgument().setValue("--tests");
            getCommandline().createArgument().setValue(t);          
        }

        getCommandline().createArgument().setValue("--code-coverage");

        getCommandline().createArgument().setValue("--result-format");
        getCommandline().createArgument().setValue("junit");          

        getCommandline().createArgument().setValue("--wait");
        getCommandline().createArgument().setValue(Integer.toString(this.wait));          

        super.createArguments();
    }

    @SuppressWarnings({"PMD.ConfusingTernary", "PMD.DataflowAnomalyAnalysis"})
    @Override
    protected void onExecuted(final int result) {
        final File[] resultFiles = this.getToDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File directory, final String fileName) {
                return fileName.equals("test-result-" + getRunId() + "-junit.xml");
            }
        });
        if (resultFiles.length > 0) {
            try {
                if (this.toFile != null) {
                    final FileUtils fu = FileUtils.getFileUtils();
                    fu.copyFile(resultFiles[0].getAbsolutePath(), this.toFile);
                } else {
                    // Required by transformers, via the virtual XMLResultAggregator
                    this.toFile = resultFiles[0].getAbsolutePath();
                }

                if (!transformers.isEmpty()) {
                    final Document document = getDocumentBuilder().parse(resultFiles[0].getAbsolutePath());
                    for (final AggregateTransformer transformer : transformers) {
                        transformer.setXmlDocument(document);
                        transformer.transform();
                    }
                }
            } catch (SAXException | IOException ex) {
                throw new BuildException(ex, getLocation());
            }
        }
    }

    @Override
    protected ISfJsonParser getParser() {
        return new RunTestTask.JsonParser();
    }

    protected void setRunId(final String runId) {
        this.runId = runId;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    // AggregateTransformer requires a valid XMLResultAggregator instance
    private XMLResultAggregator getXMLResultAggregator() {
        final XMLResultAggregator ret = new XMLResultAggregator() {
            @Override
            public File getDestinationFile() {
                return new File(getToFile());
            }
        };
        ret.bindToOwner(this);
        return ret;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getRunId() {
        return this.runId;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getToFile() {
        return this.toFile;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ File getToDir() {
        return this.toDir;
    }

    /**
     * Create a new document builder. Will issue an
     * <tt>ExceptionInitializerError</tt>
     * if something is going wrong. It is fatal anyway.
     *
     * @return a new document builder to create a DOM
     */
    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException pcex) {
            throw new ExceptionInInitializerError(pcex);
        }
    }

    private transient int wait = 6;
    private transient String runId;
    private transient String toFile;
    private transient File toDir;
    private transient final List<AggregateTransformer> transformers = new ArrayList<>();
    private transient final List<String> classes = new ArrayList<>();
    private transient final List<String> suites = new ArrayList<>();
    private transient final List<String> tests = new ArrayList<>();
}
