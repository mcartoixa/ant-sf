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
package com.mcartoixa.ant.sf.project.deploy;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import com.mcartoixa.ant.sf.apex.TestLevel;
import com.mcartoixa.ant.sf.apex.TestNameWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class StartTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            final JSONArray result = json.optJSONArray("result");
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    final Object value = result.get(i);
                    if (value instanceof JSONObject) {
                        final JSONObject object = (JSONObject) value;
                        final String filePath = object.optString("filePath");
                        if (null == filePath || filePath.isEmpty() || "N/A".equals(filePath)) {
                            this.log(object.getString("error"), Project.MSG_ERR);
                        } else {
                            final String lineNumber = object.optString("lineNumber");
                            final String message = String.format(
                                    "%s:%s: %s error: %s",
                                    new File(filePath).getAbsolutePath(),
                                    lineNumber == null || lineNumber.isEmpty() ? "0" : lineNumber,
                                    object.getString("type"),
                                    object.getString("error")
                            );
                            this.log(message, Project.MSG_ERR);
                        }
                    }
                }
            }

            final JSONObject success = json.optJSONObject("result");
            if (success != null) {
                final JSONArray deployedSource = success.optJSONArray("deployedSource");
                if (deployedSource != null) {
                    for (int i = 0; i < deployedSource.length(); i++) {
                        final Object value = deployedSource.get(i);
                        if (value instanceof JSONObject) {
                            final JSONObject object = (JSONObject) value;
                            final String message = String.format(
                                    "%s %s %s",
                                    object.getString("state"),
                                    object.getString("type"),
                                    object.getString("fullName")
                            );
                            this.log(message, Project.MSG_INFO);
                        }
                    }
                }
            }

            super.doParse(json);
        }
    }

    public StartTask() {
        super();
    }

    public void addConfiguredMetadata(final MetadataNameWrapper metadata) {
        this.metadata.add(metadata.getName());
    }

    public void addConfiguredTests(final TestNameWrapper runTests) {
        this.tests.add(runTests.getName());
    }

    public Path createMetadatadir() {
        if (this.metadataDir == null) {
            this.metadataDir = new Path(this.getProject());
        }
        return this.metadataDir.createPath();
    }

    public Path createSourcedir() {
        if (this.sourceDir == null) {
            this.sourceDir = new Path(this.getProject());
        }
        return this.sourceDir.createPath();
    }

    public void setDryRun(final boolean dryRun) {
        if (dryRun) {
            getCommandline().createArgument().setValue("--dry-run");
        }
    }

    public void setIgnoreConflicts(final boolean ignoreConflicts) {
        if (ignoreConflicts) {
            getCommandline().createArgument().setValue("--ignore-conflicts");
        }
    }

    public void setIgnoreErrors(final boolean ignoreErrors) {
        if (ignoreErrors) {
            getCommandline().createArgument().setValue("--ignore-errors");
        }
    }
    public void setIgnoreWarnings(final boolean ignoreWarnings) {
        if (ignoreWarnings) {
            getCommandline().createArgument().setValue("--ignore-warnings");
        }
    }

    public void setManifest(final File manifest) {
        if (manifest != null) {
            getCommandline().createArgument().setValue("--manifest");
            getCommandline().createArgument().setFile(manifest);
        }
    }

    public void setMetadatadir(final Path metadataDir) {
        if (this.metadataDir == null) {
            this.metadataDir = metadataDir;
        } else {
            this.metadataDir.append(metadataDir);
        }
    }

    public void setMetadatadirRef(final Reference ref) {
        this.createMetadatadir().setRefid(ref);
    }

    public void setPostdestructivechanges(final File pdc) {
        if (pdc != null) {
            getCommandline().createArgument().setValue("--post-destructive-changes");
            getCommandline().createArgument().setFile(pdc);
        }
    }

    public void setPredestructivechanges(final File pdc) {
        if (pdc != null) {
            getCommandline().createArgument().setValue("--pre-destructive-changes");
            getCommandline().createArgument().setFile(pdc);
        }
    }

    public void setPurgeondelete(final boolean purgeondelete) {
        if (purgeondelete) {
            getCommandline().createArgument().setValue("--purge-on-delete");
        }
    }

    public void setSinglepackage(final boolean singlepackage) {
        if (singlepackage) {
            getCommandline().createArgument().setValue("--single-package");
        }
    }

    public void setSourcedir(final Path sourceDir) {
        if (this.sourceDir == null) {
            this.sourceDir = sourceDir;
        } else {
            this.sourceDir.append(sourceDir);
        }
    }

    public void setSourcedirRef(final Reference ref) {
        this.createSourcedir().setRefid(ref);

    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    public void setTestLevel(final TestLevel level) {
        getCommandline().createArgument().setValue("--test-level");
        getCommandline().createArgument().setValue(level.name());
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @Override
    protected void createArguments() {
        if (!this.metadata.isEmpty()) {
            getCommandline().createArgument().setValue("--metadata");
            getCommandline().createArgument().setValue(String.join(",", this.metadata));
        }

        if (this.metadataDir != null) {
            // Commandline.Argument cannot join path with commas, so we are faking it
            final Commandline.Argument fakeArg = new Commandline.Argument();
            fakeArg.setPath(this.metadataDir);
            final String[] sp = Arrays.stream(fakeArg.getParts())
                    .map(p -> p.replace(File.pathSeparatorChar, ','))
                    .toArray(String[]::new);

            getCommandline().createArgument().setValue("--metadata-dir");
            getCommandline().createArgument().setValue(String.join(",", sp));
        }

        if (this.sourceDir != null) {
            // Commandline.Argument cannot join path with commas, so we are faking it
            final Commandline.Argument fakeArg = new Commandline.Argument();
            fakeArg.setPath(this.sourceDir);
            final String[] sp = Arrays.stream(fakeArg.getParts())
                    .map(p -> p.replace(File.pathSeparatorChar, ','))
                    .toArray(String[]::new);

            getCommandline().createArgument().setValue("--source-dir");
            getCommandline().createArgument().setValue(String.join(",", sp));
        }

        if (!this.tests.isEmpty()) {
            getCommandline().createArgument().setValue("--tests");
            getCommandline().createArgument().setValue(String.join(",", this.tests));
        }

        getCommandline().createArgument().setValue("--wait");
        getCommandline().createArgument().setValue(Integer.toString(this.wait));

        super.createArguments();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "project", "deploy", "start" };
    }

    @Override
    protected ISfJsonParser getParser() {
        return new StartTask.JsonParser();
    }

    private transient int wait = 33;
    private transient Path metadataDir;
    private transient Path sourceDir;
    private transient final List<String> tests = new ArrayList<>();
    private transient final List<String> metadata = new ArrayList<>();
}
