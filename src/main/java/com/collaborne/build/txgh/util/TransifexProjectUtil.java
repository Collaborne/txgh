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
package com.collaborne.build.txgh.util;

import java.io.IOException;

import com.collaborne.build.txgh.Settings;
import com.collaborne.build.txgh.model.GitHubProject;
import com.collaborne.build.txgh.model.TransifexConfig;
import com.collaborne.build.txgh.model.TransifexCredentials;
import com.collaborne.build.txgh.model.TransifexProject;
import com.collaborne.build.txgh.model.TransifexProjectConfig;

public class TransifexProjectUtil {

    public static TransifexProject getTransifexProject(String projectName) throws IOException {

        TransifexProjectConfig transifexProjectConfig = Settings.getConfig().getTransifexProjectConfigMap().get(projectName);
        TransifexConfig transifexConfig = TransifexConfigUtil.getTransifexConfig(transifexProjectConfig.getTransifexConfigPath());
        TransifexCredentials transifexCredentials = transifexProjectConfig.getTransifexCredentials();
        GitHubProject gitHubProject = new GitHubProject(transifexProjectConfig.getGitHubProjectUrl());

        return new TransifexProject(projectName, transifexConfig, transifexCredentials, gitHubProject);
    }
}
