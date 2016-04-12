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

import java.util.Map;

public class Config {

    private final Map<String, GitHubProjectConfig> gitHubProjectConfigMap;
    private final Map<String, TransifexProjectConfig> transifexProjectConfigMap;

    public Config(Map<String, GitHubProjectConfig> gitHubProjectConfigMap, Map<String, TransifexProjectConfig> transifexProjectConfigMap) {
        this.gitHubProjectConfigMap = gitHubProjectConfigMap;
        this.transifexProjectConfigMap = transifexProjectConfigMap;
    }

    public Map<String, GitHubProjectConfig> getGitHubProjectConfigMap() {
        return gitHubProjectConfigMap;
    }

    public Map<String, TransifexProjectConfig> getTransifexProjectConfigMap() {
        return transifexProjectConfigMap;
    }
}
