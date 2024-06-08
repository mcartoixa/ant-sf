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
package com.mcartoixa.ant.sf.org.delete;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import org.apache.tools.ant.Project;

/**
 *
 * @author Mathieu Cartoixa
 */
public class DeleteScratchTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!DeleteScratchTask.this.getQuiet()) {
                switch (key) {
                    case "orgid":
                        this.log("Org " + value + " deleted", Project.MSG_INFO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public DeleteScratchTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "org", "delete", "scratch" };
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    @Override
    protected void createArguments() {
        this.getCommandline().createArgument().setValue("--no-prompt");

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new DeleteScratchTask.JsonParser();
    }
}
