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
package com.mcartoixa.ant.sf.org.create;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Mathieu Cartoixa
 */
public class CreateUserTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!CreateUserTask.this.getQuiet()) {
                switch (key) {
                    case "id":
                        final String refProperty = CreateUserTask.this.getReferenceProperty();
                        if (refProperty != null && !refProperty.isEmpty()) {
                            CreateUserTask.this.getProject().setNewProperty(refProperty, value);
                        }
                        break;
                    case "username":
                        this.log("User " + value + " created", Project.MSG_INFO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public CreateUserTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "org", "create", "user" };
    }

    public Property createParam() {
        final Property ret = new Property();
        this.params.add(ret);
        return ret;
    }

    public void setAlias(final String alias) {
        if (alias != null && !alias.isEmpty()) {
            getCommandline().createArgument().setValue("--alias");
            getCommandline().createArgument().setValue(alias);
        }
    }

    public void setDefinitionFile(final File definitionFile) {
        if (definitionFile != null) {
            getCommandline().createArgument().setValue("--definition-file");
            getCommandline().createArgument().setFile(definitionFile);
        }
    }

    public void setReferenceProperty(final String refProperty) {
        this.refProperty = refProperty;
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    public void setUniqueUsername(final boolean isUnique) {
        if (isUnique) {
            getCommandline().createArgument().setValue("--set-unique-username");
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        this.params.forEach((p) -> {
            final Commandline.Argument arg = this.getCommandline().createArgument();
            arg.setPrefix(p.getName() + "=");
            arg.setValue(p.getValue());
        });

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new CreateUserTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getReferenceProperty() {
        return this.refProperty;
    }

    private final transient List<Property> params = new ArrayList<>();
    private transient String refProperty;
}
