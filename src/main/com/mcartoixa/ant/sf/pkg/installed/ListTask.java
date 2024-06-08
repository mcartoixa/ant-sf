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
package com.mcartoixa.ant.sf.pkg.installed;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import org.apache.tools.ant.Project;

/**
 *
 * @author mcartoixa
 */
public class ListTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CollapsibleIfStatements"})
        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!ListTask.this.getQuiet()) {
                if ("SubscriberPackageName".equals(key)) {
                    this.log("Package " + value + " is installed", Project.MSG_INFO);
                }
            }
        }
    }

    public ListTask() {
        super();
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "package", "installed", "list" };
    }

    @Override
    protected ISfJsonParser getParser() {
        return new ListTask.JsonParser();
    }
}
