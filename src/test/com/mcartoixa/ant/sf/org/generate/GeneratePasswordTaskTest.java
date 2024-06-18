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
package com.mcartoixa.ant.sf.org.generate;

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
public class GeneratePasswordTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public GeneratePasswordTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sf/org/generate/password.xml", Project.MSG_DEBUG);
    }

    @Test
    public void executeShouldSetStatusProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("Status property should be set", "0", buildRule.getProject().getProperty("execute.status"));
    }

    @Test
    public void executeShouldSetResultOrgIdProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("execute.references property should set", ")off4jyiFtlqv", buildRule.getProject().getProperty("execute.references"));
    }

    @Test
    public void executeShouldAddJsonArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --json argument", buildRule.getFullLog().contains("'--json'"));
    }

    @Test
    public void executeShouldAddComplexityArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --complexity argument", buildRule.getFullLog().contains("'--complexity'" + System.lineSeparator() + "'5'"));
    }

    @Test
    public void executeShouldAddLengthArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --length argument", buildRule.getFullLog().contains("'--length'" + System.lineSeparator() + "'12'"));
    }

    @Test
    public void executeShouldLogUsername() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Username should be logged", buildRule.getLog().contains("Password generated for user test@ant-sf.org"));
    }

    @Test
    public void executeShouldAddOnBehalfOfArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --on-behalf-of argument", buildRule.getFullLog().contains("'--on-behalf-of'" + System.lineSeparator() + "'test@ant-sf.org'"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }
}
