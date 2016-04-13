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

import java.io.IOException;

import com.collaborne.build.txgh.GitHubApi;
import com.collaborne.build.txgh.Settings;

public class GitHubProject {

    private final String project;
    private final GitHubProjectConfig config;

    public GitHubProject(String project, GitHubProjectConfig config) {
        this.project = project;
        this.config = config;
    }

    public TransifexProject getTransifexProject() throws IOException {
        return Settings.getTransifexProject(config.getTransifexProjectName());
    }
    
    public GitHubApi getGitHubApi() {
        return new GitHubApi(config);
    }

    public String getProject() {
        return project;
    }

}
