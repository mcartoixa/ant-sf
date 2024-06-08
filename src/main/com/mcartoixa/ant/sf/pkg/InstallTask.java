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
package com.mcartoixa.ant.sf.pkg;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author mcartoixa
 */
public class InstallTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!InstallTask.this.getQuiet()) {
                switch (key) {
                    case "status":
                        switch (value) {
                            case "IN_PROGRESS":
                                this.log("Package " + InstallTask.this.getPackage() + " installation in progress...", Project.MSG_INFO);
                                break;
                            case "SUCCESS":
                                this.log("Package " + InstallTask.this.getPackage() + " installation succeeded", Project.MSG_INFO);
                                break;
                            default:
                                this.log("Package " + InstallTask.this.getPackage() + " installation status: " + value, Project.MSG_INFO);
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public InstallTask() {
        super();
    }

    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
    @Override
    public void execute() throws BuildException {
        if (!this.getQuiet()) {
            this.log("Package " + this.getPackage() + " is being installed...", Project.MSG_INFO);
        }

        super.execute();
    }

    public void setInstallationKey(final String key) {
        if (key != null && !key.isEmpty()) {
            getCommandline().createArgument().setValue("--installation-key");
            getCommandline().createArgument().setValue(key);
        }
    }

    public void setPackage(final String id) {
        this.packageId = id;

        if (id != null && !id.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setLine("--package " + id);
        }
    }

    public void setPublishWait(final int timeout) {
        if (timeout > 0) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setLine("--publish-wait " + Integer.toString(timeout));
        }
    }

    public void setTargetOrg(final String organization) {
        if (organization != null && !organization.isEmpty()) {
            getCommandline().createArgument().setValue("--target-org");
            getCommandline().createArgument().setValue(organization);
        }
    }

    public void setWait(final int timeout) {
        if (timeout > 0) {
            getCommandline().createArgument().setValue("--wait");
            getCommandline().createArgument().setValue(Integer.toString(timeout));
        }
    }

    @Override
    protected void createArguments() {
        this.getCommandline().createArgument().setValue("--no-prompt");

        super.createArguments();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "package", "install" };
    }

    @Override
    protected ISfJsonParser getParser() {
        return new JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getPackage() {
        return this.packageId;
    }

    private transient String packageId;
}
