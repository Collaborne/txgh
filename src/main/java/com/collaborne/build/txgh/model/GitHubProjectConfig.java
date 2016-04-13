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

public class GitHubProjectConfig {

    private String gitHubProject;
    private GitHubCredentials gitHubCredentials;
    private final GitHubUser gitHubUser;
    private final String transifexProjectName;

    public GitHubProjectConfig(String gitHubProject, GitHubCredentials gitHubCredentials, GitHubUser gitHubUser, String transifexProjectName) {
        this.gitHubProject = gitHubProject;
        this.gitHubCredentials = gitHubCredentials;
        this.gitHubUser = gitHubUser;
        this.transifexProjectName = transifexProjectName;
    }

    public void setGitHubProject(String gitHubProject) {
        this.gitHubProject = gitHubProject;
    }

    public String getGitHubProject() {
        return gitHubProject;
    }

    public GitHubCredentials getGitHubCredentials() {
        return gitHubCredentials;
    }

    public void setGitHubCredentials(GitHubCredentials gitHubCredentials) {
        this.gitHubCredentials = gitHubCredentials;
    }

    public GitHubUser getGitHubUser() {
        return gitHubUser;
    }

    public String getTransifexProjectName() {
        return transifexProjectName;
    }

}
