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
package com.mcartoixa.ant.sf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author mcartoixa
 */
public class JsonOutputStream extends ByteArrayOutputStream {

    /**
     * Creates a new instance of this class.
     *
     * @param parser the parser with which to parse the JSON
     */
    public JsonOutputStream(final ISfJsonParser parser) {
        super(INITIAL_SIZE);
        this.parser = parser;
    }

    @Override
    public void close() throws IOException {
        this.processBuffer(new String(this.toByteArray()));
    }

    private void processBuffer(final String string) throws IOException {
        if (string != null && !string.isEmpty()) {
            try {
                final JSONObject json = new JSONObject(string);
                parser.parse(json);
            } catch (JSONException jex) {
                parser.log(jex.getLocalizedMessage(), Project.MSG_WARN);
                parser.log(string, Project.MSG_VERBOSE);
            }
        }
    }

    private final transient ISfJsonParser parser;

    private static final int INITIAL_SIZE = 132;

}
