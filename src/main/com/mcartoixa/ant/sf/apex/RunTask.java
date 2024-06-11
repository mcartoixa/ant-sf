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
package com.mcartoixa.ant.sf.apex;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import org.apache.tools.ant.Project;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class RunTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings({"PMD.AssignmentInOperand", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity", "PMD.DataflowAnomalyAnalysis", "PMD.NPathComplexity"})
        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (!RunTask.this.getQuiet() && json != null) {
                JSONObject result = json.optJSONObject("data");
                if (result == null) {
                    result = json.getJSONObject("result");
                }
                if (result != null) {
                    if (!result.getBoolean("success")) {
                        final String message = String.format("%s:%d:%d: %s: %s",
                                getFile().getAbsolutePath(),
                                result.optInt("line"),
                                result.optInt("column"),
                                json.optString("name"),
                                result.getBoolean("compiled") ? result.optString("compileProblem") : result.optString("exceptionMessage")
                        );
                        this.log(message, Project.MSG_ERR);

                        if (RunTask.this.getFailOnError()) {
                            RunTask.this.setErrorMessage(json.getString("message"));
                        }
                    }

                    final String logs = result.optString("logs");
                    if (logs != null && !logs.isEmpty()) {
                        this.log(logs, Project.MSG_VERBOSE);

                        final String logProperty = RunTask.this.getLogProperty();
                        if (logProperty != null && !logProperty.isEmpty()) {
                            try (BufferedReader br = new BufferedReader(new StringReader(logs))) {
                                @SuppressWarnings({"UnusedAssignment", "PMD.UnusedAssignment"})
                                String line = br.readLine(); // Always skip the first line
                                while ((line = br.readLine()) != null) {
                                    if (line.startsWith("Execute Anonymous:")) { continue; }
                                    final String[] elements = line.split("\\|");
                                    if ((elements.length == 5) && "USER_DEBUG".equals(elements[1])) {
                                        if ("INFO".equals(elements[3]) && elements[4].startsWith("[property]")) {
                                            final String[] p = elements[4].substring("[property]".length()).split("=", 2);
                                            if (p.length == 2) {
                                                RunTask.this.getProject().setNewProperty(logProperty + "." + p[0].trim().toLowerCase(Locale.ROOT), p[1].trim());
                                            }
                                        } else {
                                            int level;
                                            switch (elements[3]) {
                                                case "ERROR":
                                                    level = Project.MSG_ERR;
                                                    break;
                                                case "WARN":
                                                    level = Project.MSG_WARN;
                                                    break;
                                                case "INFO":
                                                    level = Project.MSG_INFO;
                                                    break;
                                                case "DEBUG":
                                                    level = Project.MSG_VERBOSE;
                                                    break;
                                                default:
                                                    level = Project.MSG_DEBUG;
                                                    break;
                                            }
                                            this.log(elements[4], level);
                                        }
                                    }
                                }
                            } catch (IOException ex) {
                                this.log(ex.getMessage(), Project.MSG_ERR);
                            }
                        }
                    }
                }
            }
        }
    }

    public RunTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "apex", "run" };
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public void setLogProperty(final String logProperty) {
        this.logProperty = logProperty;
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    @Override
    protected void createArguments() {
        super.createArguments();

        if (this.file != null) {
            getCommandline().createArgument().setValue("--file");
            getCommandline().createArgument().setFile(this.file);
        }
    }

    @Override
    protected ISfJsonParser getParser() {
        return new RunTask.JsonParser();
    }

    @Override
    protected void prepareContext() {
        if (!this.getQuiet()) {
            final String message = String.format("Executing script %s...", this.file.getName());
            this.log(message, Project.MSG_INFO);
        }
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ File getFile() {
        return this.file;
    }
    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getLogProperty() {
        return this.logProperty;
    }

    private transient File file;
    private transient String logProperty;
}
