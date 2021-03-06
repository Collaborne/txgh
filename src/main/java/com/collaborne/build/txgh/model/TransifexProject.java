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

import java.util.HashMap;
import java.util.Map;

import com.collaborne.build.txgh.TransifexApi;

public class TransifexProject {

    private final TransifexProjectConfig transifexProjectConfig;
    private final TransifexConfig transifexConfig;
    private final TransifexCredentials transifexCredentials;
    private final GitHubProject gitHubProject;

    public TransifexProject(TransifexProjectConfig transifexProjectConfig, TransifexConfig transifexConfig, TransifexCredentials transifexCredentials, GitHubProject gitHubProject) {
        this.transifexProjectConfig = transifexProjectConfig;
        this.transifexConfig = transifexConfig;
        this.transifexCredentials = transifexCredentials;
        this.gitHubProject = gitHubProject;
    }

    public TransifexResource getTransifexResource(String slug) {
        return transifexConfig.getResourceMap().get(slug);
    }

    public Map<String, TransifexResource> getSourceFileMap() {

        Map<String, TransifexResource> sourceFileMap = new HashMap<>();

        for (TransifexResource transifexResource : transifexConfig.getResourceMap().values()) {
            sourceFileMap.put(transifexResource.getSourceFile(), transifexResource);
        }

        return sourceFileMap;
    }

    public String getLanguageData(String language) {
        Map<String, String> languageMap = transifexConfig.getLanguageMap();
        return languageMap.containsKey(language) ? languageMap.get(language) : language;
    }

    public TransifexApi getTransifexApi() {
        return new TransifexApi(transifexCredentials);
    }

    public TransifexProjectConfig getConfig() {
        return transifexProjectConfig;
    }

    public TransifexCredentials getTransifexCredentials() {
        return transifexCredentials;
    }

    public GitHubProject getGitHubProject() {
        return gitHubProject;
    }

}
