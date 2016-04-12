/* 
 * Copyright (c) 2014 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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
package com.collaborne.build.txgh.model;

public class TransifexProjectConfig {

    private final String transifexConfigPath;
    private TransifexCredentials transifexCredentials;
    private final String gitHubProjectUrl;

    public TransifexProjectConfig(String transifexConfigPath, TransifexCredentials transifexCredentials, String gitHubProjectUrl) {
        this.transifexConfigPath = transifexConfigPath;
        this.transifexCredentials = transifexCredentials;
        this.gitHubProjectUrl = gitHubProjectUrl;
    }

    public String getTransifexConfigPath() {
        return transifexConfigPath;
    }

    public TransifexCredentials getTransifexCredentials() {
        return transifexCredentials;
    }

    public void setTransifexCredentials(TransifexCredentials transifexCredentials) {
        this.transifexCredentials = transifexCredentials;
    }

    public String getGitHubProjectUrl() {
        return gitHubProjectUrl;
    }

}
