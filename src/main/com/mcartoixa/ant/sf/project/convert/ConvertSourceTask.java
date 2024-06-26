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

import com.mcartoixa.ant.sf.ISfJsonParser;
import com.mcartoixa.ant.sf.SfTask;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ConvertSourceTask extends SfTask {

    /* default */ class JsonParser extends SfTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            switch (key) {
                case "location":
                    this.log("Metadata stored in " + value, Project.MSG_INFO);
                    break;
                default:
                    break;
            }
        }
    }

    public ConvertSourceTask() {
        super();
    }

    public void addConfiguredMetadata(final MetadataNameWrapper metadata) {
        this.metadata.add(metadata.getName());
    }

    public Path createSourcedir() {
        if (this.sourceDir == null) {
            this.sourceDir = new Path(this.getProject());
        }
        return this.sourceDir.createPath();
    }

    public void setManifest(final File manifest) {
        if (manifest != null) {
            getCommandline().createArgument().setValue("--manifest");
            getCommandline().createArgument().setFile(manifest);
        }
    }

    public void setOutputDir(final File outputDir) {
        if (outputDir != null) {
            final File dir = !outputDir.exists() || outputDir.isDirectory() ? outputDir : outputDir.getParentFile();

            getCommandline().createArgument().setValue("--output-dir");
            getCommandline().createArgument().setValue(dir.getAbsolutePath());
        }
    }

    public void setPackageName(final String name) {
        if (name != null && !name.isEmpty()) {
            getCommandline().createArgument().setValue("--package-name");
            getCommandline().createArgument().setValue(name);
        }
    }

    public void setRootDir(final File rootDir) {
        if (rootDir != null) {
            final File dir = !rootDir.exists() || rootDir.isDirectory() ? rootDir : rootDir.getParentFile();

            getCommandline().createArgument().setValue("--root-dir");
            getCommandline().createArgument().setValue(dir.getAbsolutePath());
        }
    }

    public void setSourceDir(final Path sourcePath) {
        if (this.sourceDir == null) {
            this.sourceDir = sourcePath;
        } else {
            this.sourceDir.append(sourcePath);
        }
    }

    public void setSourcedirRef(final Reference ref) {
        this.createSourcedir().setRefid(ref);

    }

    @Override
    protected String[] getCommand() {
        return new String[] { "project", "convert", "source" };
    }

    @Override
    protected ISfJsonParser getParser() {
        return new ConvertSourceTask.JsonParser();
    }

    @Override
    protected void createArguments() {
        if (!this.metadata.isEmpty()) {
            getCommandline().createArgument().setValue("--metadata");
            getCommandline().createArgument().setValue(String.join(",", this.metadata));
        }

        if (this.sourceDir != null) {
            // Commandline.Argument cannot join path with commas, so we are faking it
            final Commandline.Argument fakeArg = new Commandline.Argument();
            fakeArg.setPath(this.sourceDir);
            final String[] sp = Arrays.stream(fakeArg.getParts())
                    .map(p -> p.replace(File.pathSeparatorChar, ','))
                    .toArray(String[]::new);

            getCommandline().createArgument().setValue("--source-dir");
            getCommandline().createArgument().setValue(String.join(",", sp));
        }

        super.createArguments();
    }

    private transient Path sourceDir;
    private transient final List<String> metadata = new ArrayList<>();
}
