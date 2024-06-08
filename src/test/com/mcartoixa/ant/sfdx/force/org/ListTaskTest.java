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
package com.mcartoixa.ant.sfdx.force.org;

import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ListTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public ListTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sfdx/force/org/list.xml", Project.MSG_DEBUG);
    }

    @Test
    public void executeShouldSetStatusProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("Status property should be set", "0", buildRule.getProject().getProperty("execute.status"));
    }

    @Test
    public void executeShouldAddJsonArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --json argument", buildRule.getFullLog().contains("'--json'"));
    }

    @Test
    public void executeShouldAddNoPromptArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -p argument", buildRule.getFullLog().contains("'-p'"));
    }

    @Test
    public void executeShouldLogOrgSummary() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Non scratch orgs should be logged", buildRule.getLog().contains("ant-sfdx (00000000000000000H)  [test@ant-sfdx.org]"));
        Assert.assertTrue("Scratch orgs should be logged", buildRule.getLog().contains("\t- ant-sfdx-scratch (000000000000000000) Ant SFDX [test@ant-sfdx.org]"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }

    @Test
    public void executeWithAllShouldAddAllArgument() {
        buildRule.executeTarget("execute-with-all");
        Assert.assertTrue("Full log should contain --all argument", buildRule.getFullLog().contains("'--all'"));
    }

    @Test
    public void executeWithCleanShouldAddAllArgument() {
        buildRule.executeTarget("execute-with-clean");
        Assert.assertTrue("Full log should contain --clean argument", buildRule.getFullLog().contains("'--clean'"));
    }
}
