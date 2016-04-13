/*
 * Copyright (c) 2016 Collaborne B.V. <opensource@collaborne.com>
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

import com.collaborne.build.txgh.util.TransifexConfigUtil;

public class TXGHProject {
	private final GitHubProjectConfig gitHubProjectConfig;
	private final TransifexProjectConfig transifexProjectConfig;

	public TXGHProject(GitHubProjectConfig gitHubProjectConfig, TransifexProjectConfig transifexProjectConfig) {
		this.gitHubProjectConfig = gitHubProjectConfig;
		this.transifexProjectConfig = transifexProjectConfig;
	}

	public GitHubProjectConfig getGitHubProjectConfig() {
		return gitHubProjectConfig;
	}

	public GitHubProject getGitHubProject() {
		return new GitHubProject(gitHubProjectConfig) {
			@Override
			public TransifexProject getTransifexProject() throws IOException {
				TransifexConfig transifexConfig = TransifexConfigUtil.getTransifexConfig(transifexProjectConfig.getTransifexConfigPath());
				TransifexCredentials transifexCredentials = transifexProjectConfig.getTransifexCredentials();

				return new TransifexProject(transifexProjectConfig, transifexConfig, transifexCredentials, this);
			}
		};
	}

	public TransifexProjectConfig getTransifexProjectConfig() {
		return transifexProjectConfig;
	}

	public TransifexProject getTransifexProject() throws IOException {
		return getGitHubProject().getTransifexProject();
	}
}
