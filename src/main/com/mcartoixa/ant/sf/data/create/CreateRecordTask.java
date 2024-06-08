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
package com.mcartoixa.ant.sf.data.create;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class CreateRecordTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (json != null) {
                final JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    if (result.getBoolean("success")) {
                        this.log("Record " + result.getString("id") + " created", Project.MSG_INFO);
                    } else {
                        if (json.has("errors")) {
                            final JSONArray errors = json.getJSONArray("errors");
                            if (errors != null) {
                                for (int i = 0; i < errors.length(); i++) {
                                    final String e = errors.getString(i);
                                    if (e != null && !e.isEmpty()) {
                                        this.log(e, Project.MSG_ERR);
                                        if (CreateRecordTask.this.getFailOnError() && !CreateRecordTask.this.hasErrorMessage()) {
                                            CreateRecordTask.this.setErrorMessage("The record could not be created.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public CreateRecordTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "data", "create", "record" };
    }

    public Property createField() {
        final Property ret = new Property();
        this.fields.add(ret);
        return ret;
    }

    public void setSObject(final String sObject) {
        if (sObject != null && !sObject.isEmpty()) {
            getCommandline().createArgument().setValue("--sobject");
            getCommandline().createArgument().setValue(sObject);
        }
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    public void setUseToolingApi(final boolean useToolingApi) {
        if (useToolingApi) {
            getCommandline().createArgument().setValue("--use-tooling-api");
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        if (!this.fields.isEmpty()) {
            final StringBuilder record = new StringBuilder();
            this.fields.forEach((f) -> {
                if (record.length() > 0) {
                    record.append(" ");
                }
                record.append(f.getName());
                record.append("='");
                record.append(f.getValue());
                record.append("'");
            });
            getCommandline().createArgument().setLine("--values \"" + record + "\"");
        }

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new CreateRecordTask.JsonParser();
    }

    private final transient List<Property> fields = new ArrayList<>();
}
