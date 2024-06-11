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
package com.mcartoixa.ant.sf.data.upsert;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.io.File;
import org.apache.tools.ant.Project;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class UpsertBulkTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.EmptyCatchBlock")
        @Override
        protected void doParse(final JSONObject json) {
            if (!UpsertBulkTask.this.getQuiet()) {
                final JSONObject result = json.optJSONObject("result");
                if (result != null) {
                    final JSONObject records = result.optJSONObject("records");
                    if (records != null) {
                        final JSONArray failedResults = records.optJSONArray("failedResults");
                        if (failedResults != null) {
                            for (int i = 0; i < failedResults.length(); i++) {
                                final Object value = failedResults.get(i);
                                if (value instanceof JSONObject) {
                                    final JSONObject fr = (JSONObject) value;
                                    final String message = fr.optString("sf__Error");
                                    if (message != null) {
                                        this.log(message, Project.MSG_ERR);
                                    }
                                }
                            }
                        }
                    }

                    final JSONObject jobInfo = result.optJSONObject("jobInfo");
                    if (jobInfo != null) {
                        this.log(
                            String.format(
                                "%s records processed: %d, Failed: %d",
                                UpsertBulkTask.this.getSobject(),
                                jobInfo.optInt("numberRecordsProcessed", 0),
                                jobInfo.optInt("numberRecordsFailed", 0)
                            ),
                            Project.MSG_INFO
                        );
                    }
                }
            }

            super.doParse(json);
        }
    }

    public UpsertBulkTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "data", "upsert", "bulk" };
    }

    public void setExternalId(final String externalId) {
        if (externalId != null) {
            getCommandline().createArgument().setValue("--external-id");
            getCommandline().createArgument().setValue(externalId);
        }
    }

    public void setFile(final File file) {
        if (file != null) {
            getCommandline().createArgument().setValue("--file");
            getCommandline().createArgument().setValue(file.getPath());
        }
    }

    public void setSobject(final String sobject) {
        this.sobject = sobject;
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    public void setWait(final int wait) {
        getCommandline().createArgument().setValue("--wait");
        getCommandline().createArgument().setValue(Integer.toString(wait));
    }

    @Override
    protected void createArguments() {
        super.createArguments();

        if (this.sobject != null) {
            getCommandline().createArgument().setValue("--sobject");
            getCommandline().createArgument().setValue(this.sobject);
        }
    }

    @Override
    protected ISfJsonParser getParser() {
        return new UpsertBulkTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getSobject() {
        return this.sobject;
    }
    
    private transient String sobject;
}
