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
public class CreateUserTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public CreateUserTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sf/org/create/user.xml", Project.MSG_DEBUG);
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
    public void executeShouldAddTargetdevhubArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --target-org argument", buildRule.getFullLog().contains("'--target-org'" + System.lineSeparator() + "'testorganization'"));
    }

    @Test
    public void executeShouldAddParamsArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain ParamName=paramValue argument value", buildRule.getFullLog().contains("ParamName=paramValue"));
    }

    @Test
    public void executeShouldSetReferenceProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("Reference property should be set", "00000000000000000V", buildRule.getProject().getProperty("execute.reference"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }
}
