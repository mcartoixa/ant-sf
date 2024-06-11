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
import com.google.common.base.CaseFormat;
import java.io.File;
import org.apache.tools.ant.Project;

/**
 *
 * @author Mathieu Cartoixa
 */
public class CreateScratchTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!CreateScratchTask.this.getQuiet()) {
                switch (key) {
                    case "orgid":
                        this.log("Org " + value + " created", Project.MSG_INFO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public CreateScratchTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "org", "create", "scratch" };
    }

    public void setAlias(final String alias) {
        if (alias != null && !alias.isEmpty()) {
            getCommandline().createArgument().setValue("--alias");
            getCommandline().createArgument().setValue(alias);
        }
    }

    public void setClientId(final String clientId) {
        if (clientId != null && !clientId.isEmpty()) {
            getCommandline().createArgument().setValue("--client-id");
            getCommandline().createArgument().setValue(clientId);
        }
    }

    public void setDefault(final boolean isDefault) {
        if (isDefault) {
            getCommandline().createArgument().setValue("--set-default");
        }
    }

    public void setDefinitionFile(final File definitionFile) {
        if (definitionFile != null) {
            getCommandline().createArgument().setValue("--definition-file");
            getCommandline().createArgument().setFile(definitionFile);
        }
    }

    public void setDurationDays(final int days) {
        if (days > 0) {
            getCommandline().createArgument().setValue("--duration-days");
            getCommandline().createArgument().setValue(Integer.toString(days));
        }
    }

    public void setNoAncestors(final boolean noAncestors) {
        if (noAncestors) {
            getCommandline().createArgument().setValue("--no-ancestors");
        }
    }

    public void setNoNamespace(final boolean noNamespace) {
        if (noNamespace) {
            getCommandline().createArgument().setValue("--no-namespace");
        }
    }

    public void setNoTrackSource(final boolean noTrackSource) {
        if (noTrackSource) {
            getCommandline().createArgument().setValue("--no-track-source");
        }
    }

    public void setOrgAdminEmail(final String adminEmail) {
        if (adminEmail != null && !adminEmail.isEmpty()) {
            getCommandline().createArgument().setValue("--admin-email");
            getCommandline().createArgument().setValue(adminEmail);
        }
    }

    public void setOrgDescription(final String description) {
        if (description != null && !description.isEmpty()) {
            getCommandline().createArgument().setValue("--description");
            getCommandline().createArgument().setValue(description);
        }
    }

    public void setOrgEdition(final SalesforceEdition edition) {
        getCommandline().createArgument().setValue("--edition");
        getCommandline().createArgument().setValue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, edition.name()));
    }

    public void setOrgName(final String name) {
        if (name != null && !name.isEmpty()) {
            getCommandline().createArgument().setValue("--name");
            getCommandline().createArgument().setValue(name);
        }
    }

    public void setOrgRelease(final ScratchOrgRelease release) {
        getCommandline().createArgument().setValue("--release");
        getCommandline().createArgument().setValue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, release.name()));
    }

    public void setOrgSourceOrg(final String sourceOrg) {
        if (sourceOrg != null && !sourceOrg.isEmpty()) {
            getCommandline().createArgument().setValue("--source-org");
            getCommandline().createArgument().setValue(sourceOrg);
        }
    }

    public void setOrgUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("--username");
            getCommandline().createArgument().setValue(userName);
        }
    }

    public void setTargetDevHub(final String devHub) {
        if (devHub != null && !devHub.isEmpty()) {
            getCommandline().createArgument().setValue("--target-dev-hub");
            getCommandline().createArgument().setValue(devHub);
        }
    }

    public void setWait(final int timeout) {
        if (timeout > 0) {
            getCommandline().createArgument().setValue("--wait");
            getCommandline().createArgument().setValue(Integer.toString(timeout));
        }
    }

    @Override
    protected ISfJsonParser getParser() {
        return new CreateScratchTask.JsonParser();
    }
}
