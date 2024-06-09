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
package com.mcartoixa.ant.sf.project.convert;

import java.io.File;
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
public class ConvertSourceTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public ConvertSourceTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sf/project/convert/source.xml", Project.MSG_DEBUG);
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
    public void executeShouldAddManifestArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --manifest argument", buildRule.getFullLog().contains("'--manifest'" + System.lineSeparator() + "'" + new File(buildRule.getProject().getBaseDir(), "testmanifest").getAbsolutePath() + "'"));
    }

    @Test
    public void executeShouldAddOutputDirArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --output-dir argument", buildRule.getFullLog().contains("'--output-dir'" + System.lineSeparator() + "'" + new File(buildRule.getProject().getBaseDir(), "ant-sf").getAbsolutePath() + "'"));
    }

    @Test
    public void executeShouldAddMetadataArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --metadata argument", buildRule.getFullLog().contains("'--metadata'" + System.lineSeparator() + "'TestComponent1,TestComponent2'"));
    }

    @Test
    public void executeShouldAddPackageNameArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --package-name argument", buildRule.getFullLog().contains("'--package-name'" + System.lineSeparator() + "'testpackage'"));
    }

    @Test
    public void executeShouldAddRootDirArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --root-dir argument", buildRule.getFullLog().contains("'--root-dir'" + System.lineSeparator() + "'" + buildRule.getProject().getBaseDir().getAbsolutePath() + "'"));
    }

    @Test
    public void executeShouldAddSourceDirArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --source-dir argument", buildRule.getFullLog().contains("'--source-dir'" + System.lineSeparator() + "'" + new File(buildRule.getProject().getBaseDir(), "testsourcepath").getAbsolutePath() + "'"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }
}
