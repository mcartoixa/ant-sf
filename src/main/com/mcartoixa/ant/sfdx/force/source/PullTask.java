/*
 * Copyright 2023 Mathieu Cartoixa.
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
package com.mcartoixa.ant.sfdx.force.source;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class PullTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            final JSONArray error = json.optJSONArray("result");
            if (error != null) {
                for (int i = 0; i < error.length(); i++) {
                    final Object value = error.get(i);
                    if (value instanceof JSONObject) {
                        final JSONObject object = (JSONObject) value;
                        final String message = String.format(
                                "%s in %s %s",
                                object.getString("state"),
                                object.getString("type"),
                                object.getString("fullName")
                        );
                        this.log(message, Project.MSG_ERR);
                    }
                }
            }

            final JSONObject success = json.optJSONObject("result");
            if (success != null) {
                final JSONArray pushedSource = success.optJSONArray("pulledSource");
                if (pushedSource != null) {
                    for (int i = 0; i < pushedSource.length(); i++) {
                        final Object value = pushedSource.get(i);
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

    public PullTask() {
        super();
    }

    public void setForceOverwrite(final boolean forceOverwrite) {
        if (forceOverwrite) {
            getCommandline().createArgument().setValue("-f");
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    public void setWait(final int wait) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-w");
        arg.setValue(Integer.toString(wait));
    }

    @Override
    protected String getCommand() {
        return "force:source:pull";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new PullTask.JsonParser();
    }
}
