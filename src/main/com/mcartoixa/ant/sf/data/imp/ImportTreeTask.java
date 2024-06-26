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
package com.mcartoixa.ant.sf.data.imp;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.Phase;
import com.mcartoixa.ant.sf.SfTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ResourceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ImportTreeTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.EmptyCatchBlock")
        @Override
        protected void doParse(final JSONObject json) {
            final JSONArray result = json.optJSONArray("result");
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    final Object value = result.get(i);
                    if (value instanceof JSONObject) {
                        final JSONObject object = (JSONObject) value;
                        final String refId = object.getString("refId");
                        final String id = object.getString("id");
                        final String refProperty = ImportTreeTask.this.getReferencesProperty();
                        if (refProperty != null && !refProperty.isEmpty()) {
                            ImportTreeTask.this.getProject().setNewProperty(refProperty + "." + refId.toLowerCase(Locale.ROOT), id);
                        }

                        final String message = String.format(
                                "%s imported (%s)",
                                refId,
                                id
                        );
                        this.log(message, Project.MSG_VERBOSE);
                    }
                }
                this.log(
                    String.format(
                        "Records processed: %d",
                        result.length()
                    ),
                    Project.MSG_INFO
                );
            }

            final String errorMessage = json.optString("message");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // JSON embedded inside JSON...
                try {
                    final JSONObject errorJson = new JSONObject(errorMessage);
                    final boolean hasErrors = errorJson.optBoolean("hasErrors", false);
                    if (hasErrors) {
                        final JSONArray results = errorJson.optJSONArray("results");
                        if (results != null) {
                            json.remove("message");
                            ImportTreeTask.this.setErrorMessage(
                                    String.format(
                                            "There were %d error(s) while importing data.",
                                            results.length()
                                    )
                            );
                            for (int i = 0; i < results.length(); i++) {
                                final Object value = results.get(i);
                                if (value instanceof JSONObject) {
                                    final JSONObject object = (JSONObject) value;
                                    final JSONArray errors = object.getJSONArray("errors");
                                    String errorMessages = "";
                                    for (int j = 0; j < errors.length(); j++) {
                                        if (!errorMessages.isEmpty()) {
                                            errorMessages = errorMessages.concat("\n\t");
                                        }
                                        errorMessages = errorMessages.concat(errors.getJSONObject(j).getString("message"));
                                    }
                                    final String message = String.format(
                                            "%s not imported: %s",
                                            object.getString("referenceId"),
                                            errorMessages
                                    );
                                    this.log(message, Project.MSG_ERR);
                                }
                            }
                        }
                    }
                } catch (JSONException jex) {
                    // Error message is not JSON
                }

            }

            super.doParse(json);
        }
    }

    public ImportTreeTask() {
        super();
    }

    @Override
    protected void checkConfiguration() {
        if (this.fileSets.isEmpty() && (this.resources == null) && (this.plan == null)) {
            throw new BuildException("No resources or plan have been specified", getLocation());
        }

        super.checkConfiguration();
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    protected String[] getCommand() {
        switch (this.phase) {
            case beta:
                return new String[] { "data", "import", "beta", "tree" };
            case legacy:
                return new String[] { "data", "import", "legacy", "tree" };
            default:
                return new String[] { "data", "import", "tree" };
        }
    }

    public void addFileset(final FileSet set) {
        this.fileSets.add(set);
    }

    public void addFilelist(final FileList list) {
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(list);
    }

    public void setPhase(final Phase phase) {
        this.phase = phase;
    }

    public void setPlan(final File plan) {
        this.plan = plan;
    }

    public void setReferencesProperty(final String refProperty) {
        this.refProperty = refProperty;
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.NPathComplexity"})
    @Override
    protected void createArguments() {
        final StringBuilder sobjecttreefiles = new StringBuilder();
        for (final FileSet fs : this.fileSets) {
            final DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            for (final String file : ds.getIncludedFiles()) {
                if (sobjecttreefiles.length() > 0) {
                    sobjecttreefiles.append(",");
                }
                try {
                    final Path path = Paths.get(new File(ds.getBasedir(), file).getCanonicalPath());
                    final Path base = Paths.get(this.getProject().getBaseDir().getCanonicalPath());

                    sobjecttreefiles.append(base.relativize(path));
                } catch (IOException ioex) {
                    this.log(ioex.getMessage(), this.getQuiet() ? Project.MSG_WARN : Project.MSG_VERBOSE);
                }
            }
        }
        if (this.resources != null) {
            for (final Resource r : this.resources) {
                final FileProvider fp = r.as(FileProvider.class);
                if (fp != null) {
                    final FileResource fr = ResourceUtils.asFileResource(fp);
                    if (sobjecttreefiles.length() > 0) {
                        sobjecttreefiles.append(",");
                    }
                    try {
                        final Path path = Paths.get(fr.getFile().getCanonicalPath());
                        final Path base = Paths.get(this.getProject().getBaseDir().getCanonicalPath());

                        sobjecttreefiles.append(base.relativize(path));
                    } catch (IOException ioex) {
                        this.log(ioex.getMessage(), this.getQuiet() ? Project.MSG_WARN : Project.MSG_VERBOSE);
                    }
                }
            }
        }
        if (sobjecttreefiles.length() > 0) {
            getCommandline().createArgument().setValue("--files");
            getCommandline().createArgument().setValue(sobjecttreefiles.toString());
        }
        if (this.plan != null) {
            getCommandline().createArgument().setValue("--plan");
            getCommandline().createArgument().setValue(this.plan.getPath());
        }

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new ImportTreeTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getReferencesProperty() {
        return this.refProperty;
    }

    private transient final List<FileSet> fileSets = new ArrayList<>();
    private transient File plan;
    private transient Phase phase = Phase.current;
    private transient String refProperty;
    private transient Union resources = null;
}
