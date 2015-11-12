/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.elasticsearch.ingest.processor;

import org.elasticsearch.ingest.Data;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * A processor implementation may modify the data belonging to a document.
 * Whether changes are made and what exactly is modified is up to the implementation.
 */
public interface Processor {

    /**
     * Introspect and potentially modify the incoming data.
     */
    void execute(Data data);

    /**
     * Gets the type of a processor
     */
    String getType();

    /**
     * A factory that knows how to construct a processor based on a map of maps.
     */
    interface Factory<P extends Processor> extends Closeable {

        /**
         * Creates a processor based on the specified map of maps config
         */
        P create(Map<String, Object> config) throws IOException;

        /**
         * Sets the configuration directory when needed to read additional config files
         */
        default void setConfigDirectory(Path configDirectory) {
        }


        @Override
        default void close() throws IOException {
        }
        
    }

}
