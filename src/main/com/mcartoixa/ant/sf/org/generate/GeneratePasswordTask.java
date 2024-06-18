/*
 * Copyright 2024 mcartoixa.
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
package com.mcartoixa.ant.sf.org.generate;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mcartoixa
 */
public class GeneratePasswordTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }
        @Override
        protected void doParse(final JSONObject json) {
            final JSONObject result = json.optJSONObject("result");
            if (result != null) {
                handleUserPassword(result, -1);
            } else {
                final JSONArray results = json.optJSONArray("result");
                if (results != null) {
                    for (int i = 0; i < results.length(); i++) {
                        final Object value = results.get(i);
                        if (value instanceof JSONObject) {
                            handleUserPassword((JSONObject)value, i);
                        }
                    }
                }
            }

            super.doParse(json);
        }
        
        private void handleUserPassword(final JSONObject value, final int index) {
            final String username = value.getString("username");
            final String password = value.getString("password");

            if (!GeneratePasswordTask.this.getQuiet()) {
                this.log(
                    String.format(
                        "Password generated for user %s",
                        username
                    ),
                    Project.MSG_INFO
                );
            }
            
            String refProperty = GeneratePasswordTask.this.getReferencesProperty();
            if (refProperty != null && !refProperty.isEmpty()) {
                if (index >= 0) {
                    refProperty = String.format(
                        "%s.password%d",
                        refProperty,
                        index
                    );
                }
                GeneratePasswordTask.this.getProject().setNewProperty(refProperty, password);
            }

        }
    }

    public GeneratePasswordTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "org", "generate", "password" };
    }

    public void addConfiguredOnbehalfof(final UsernameWrapper username) {
        this.usernames.add(username.getName());
    }

    public void setComplexity(final int complexity) {
        getCommandline().createArgument().setValue("--complexity");
        getCommandline().createArgument().setValue(Integer.toString(complexity));
    }

    public void setLength(final int length) {
        getCommandline().createArgument().setValue("--length");
        getCommandline().createArgument().setValue(Integer.toString(length));
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

    @Override
    protected void createArguments() {
        if (!this.usernames.isEmpty()) {
            for (final String u: usernames) {
                getCommandline().createArgument().setValue("--on-behalf-of");
                final PropertyHelper ph = PropertyHelper.getPropertyHelper(this.getProject());
                getCommandline().createArgument().setValue(ph.replaceProperties(u));
            }
        }

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new GeneratePasswordTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getReferencesProperty() {
        return this.refProperty;
    }

    private transient String refProperty;
    private transient final List<String> usernames = new ArrayList<>();
}
