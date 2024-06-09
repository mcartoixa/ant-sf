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
package com.mcartoixa.ant.sf.org;

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ListTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();

            this.nonScratchOrgs = new ArrayList<>();
            this.scratchOrgs = new ArrayList<>();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (!ListTask.this.getQuiet()) {
                nonScratchOrgs.forEach((nso) -> {
                    this.log(nso.toString(), Project.MSG_INFO);
                    scratchOrgs.forEach((so) -> {
                        if (nso.getOrgId() == null ? so.getDevHubOrgId() == null : so.getDevHubOrgId().equals(nso.getOrgId())) {
                            this.log("\t- " + so.toString(), Project.MSG_INFO);
                        }
                    });
                });
            }
        }

        @SuppressWarnings("PMD.NPathComplexity")
        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            final int plio = property.lastIndexOf('.');
            int nsolio = property.lastIndexOf(".nonscratchorgs.");
            if (nsolio >= 0) {
                nsolio += ".nonscratchorgs.".length() - 1;
            }
            int solio = property.lastIndexOf(".scratchorgs.");
            if (solio >= 0) {
                solio += ".scratchorgs.".length() - 1;
            }
            if (nsolio >= 0 && plio == nsolio) {
                final int index = Integer.parseInt(property.substring(nsolio + 1));
                if (nonScratchOrgs.size() <= index) {
                    currentOrg = new Organization();
                    nonScratchOrgs.add(currentOrg);
                }
            } else if (solio >= 0 && plio == solio) {
                final int index = Integer.parseInt(property.substring(solio + 1));
                if (scratchOrgs.size() <= index) {
                    currentOrg = new Organization();
                    scratchOrgs.add(currentOrg);
                }
            }

            if (!ListTask.this.getQuiet()) {
                switch (key) {
                    case "alias":
                        if (currentOrg != null) {
                            currentOrg.setAlias(value);
                        }
                        break;
                    case "devhuborgid":
                        if (currentOrg != null) {
                            currentOrg.setDevHubOrgId(value);
                        }
                        break;
                    case "expirationdate":
                        if (currentOrg != null) {
                            currentOrg.setExpirationDate(value);
                        }
                        break;
                    case "orgid":
                        if (currentOrg != null) {
                            currentOrg.setOrgId(value);
                        }
                        break;
                    case "orgname":
                        if (currentOrg != null) {
                            currentOrg.setOrgName(value);
                        }
                        break;
                    case "username":
                        if (currentOrg != null) {
                            currentOrg.setUserName(value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private transient Organization currentOrg;
        private final transient List<Organization> nonScratchOrgs;
        private final transient List<Organization> scratchOrgs;
    }

    public ListTask() {
        super();
    }

    @Override
    protected String[] getCommand() {
        return new String[] { "org", "list" };
    }

    public void setAll(final boolean all) {
        if (all) {
            getCommandline().createArgument().setValue("--all");
        }
    }

    public void setClean(final boolean clean) {
        if (clean) {
            getCommandline().createArgument().setValue("--clean");
        }
    }

    @Override
    protected void createArguments() {
        this.getCommandline().createArgument().setValue("--no-prompt");

        super.createArguments();
    }

    @Override
    protected ISfJsonParser getParser() {
        return new ListTask.JsonParser();
    }
}
