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
import org.apache.tools.ant.types.Commandline;
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

        @SuppressWarnings({"PMD.AssignmentInOperand", "PMD.AvoidLiteralsInIfCondition", "PMD.CognitiveComplexity", "PMD.DataflowAnomalyAnalysis"})
        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (json != null) {
                final JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    if (!result.getBoolean("success")) {
                        final String message = String.format(
                                "%s:%d:%d: %s: %s",
                                getFile().getAbsolutePath(),
                                result.optInt("line"),
                                result.optInt("column"),
                                result.getBoolean("compiled") ? "error" : "exception",
                                result.optString("message")
                        );
                        this.log(message, Project.MSG_ERR);
                        if (RunTask.this.getFailOnError()) {
                            RunTask.this.setErrorMessage(
                                    result.getBoolean("compiled")
                                    ? "An error ocurred during APEX compilation."
                                    : "An exception occurred dunring APEX execution."
                            );
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
                                    if ((elements.length == 5) && "USER_DEBUG".equals(elements[1]) && "INFO".equals(elements[3]) && elements[4].startsWith("[property]")) {
                                        final String[] p = elements[4].substring("[property]".length()).split("=", 2);
                                        if (p.length == 2) {
                                            RunTask.this.getProject().setNewProperty(logProperty + "." + p[0].trim().toLowerCase(Locale.ROOT), p[1].trim());
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

        if (file != null) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("--file");
            arg.setFile(file);
        }
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
    protected ISfJsonParser getParser() {
        return new RunTask.JsonParser();
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
