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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaborne.build.txgh.GitHubApi;
import com.collaborne.build.txgh.util.TransifexConfigUtil;

public class TXGHProject {
	private static final Logger LOGGER = LoggerFactory.getLogger(TXGHProject.class);

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
				GitHubApi gitHubApi = getGitHubApi();
				Repository repository = gitHubApi.getRepository();

				// Find .tx/config in the 'master' of that project
				// FIXME: We need to handle the branch better!
				String branch = "master";
				String txConfigSha = null;
				Tree tree = gitHubApi.getTree(repository, branch);
				for (TreeEntry treeEntry : tree.getTree()) {
					if (".tx/config".equals(treeEntry.getPath())) {
						txConfigSha = treeEntry.getSha();
					}
				}

				if (txConfigSha == null) {
					LOGGER.error("Cannot find .tx/config in {}#{}", getGitHubProject(), branch);
					throw new FileNotFoundException("No .tx/config");
				}

				String txConfig = gitHubApi.getFileContent(repository, txConfigSha);
				try (Reader reader = new StringReader(txConfig)) {
					TransifexConfig transifexConfig = TransifexConfigUtil.getTransifexConfig(reader);
					TransifexCredentials transifexCredentials = transifexProjectConfig.getTransifexCredentials();

					return new TransifexProject(transifexProjectConfig, transifexConfig, transifexCredentials, this);
				}
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
