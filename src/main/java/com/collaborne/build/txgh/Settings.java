/* 
 * Copyright (c) 2014 Jan Tošovský <jan.tosovsky.cz@gmail.com>
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
package com.collaborne.build.txgh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaborne.build.txgh.model.GitHubCredentials;
import com.collaborne.build.txgh.model.GitHubProjectConfig;
import com.collaborne.build.txgh.model.GitHubUser;
import com.collaborne.build.txgh.model.TXGHProject;
import com.collaborne.build.txgh.model.TransifexCredentials;
import com.collaborne.build.txgh.model.TransifexProjectConfig;

public class Settings {
	private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);

	private static GitHubCredentials getDefaultGitHubCredentials() {

		GitHubCredentials defaultGitHubCredentials = null;

		String defaultGitHubUser = System.getenv("TXGH_DEFAULT_GITHUB_USER");
		String defaultGitHubPassword = System.getenv("TXGH_DEFAULT_GITHUB_PASSWORD");

		if (defaultGitHubUser != null && defaultGitHubPassword != null) {
			defaultGitHubCredentials = new GitHubCredentials(defaultGitHubUser, defaultGitHubPassword);
		}

		return defaultGitHubCredentials;
	}

	private static TransifexCredentials getDefaultTransifexCredentials() {

		TransifexCredentials defaultTransifexCredentials = null;

		String defaultTransifexUser = System.getenv("TXGH_DEFAULT_TRANSIFEX_USER");
		String defaultTransifexPassword = System.getenv("TXGH_DEFAULT_TRANSIFEX_PASSWORD");

		if (defaultTransifexUser != null && defaultTransifexPassword != null) {
			defaultTransifexCredentials = new TransifexCredentials(defaultTransifexUser, defaultTransifexPassword);
		}

		return defaultTransifexCredentials;
	}

	private static Connection getDatabaseConnection() throws SQLException {
		String dbUrl = System.getenv("JDBC_DATABASE_URL");
		return DriverManager.getConnection(dbUrl);
	}

	private static GitHubProjectConfig getGitHubProjectConfig(Connection connection, String projectName) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM public.github WHERE project = ?")) {
			stmt.setString(1, projectName);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String project = rs.getString("project");
					String name = rs.getString("name");
					String email = rs.getString("email");

					String secret = rs.getString("secret");
					String transifexProject = rs.getString("transifexproject");

					String userId = rs.getString("userid");
					String credentials = rs.getString("credentials");
					GitHubCredentials gitHubCredentials;
					if (userId != null && credentials != null) {
						gitHubCredentials = new GitHubCredentials(userId, credentials);
					} else {
						gitHubCredentials = getDefaultGitHubCredentials();
					}

					return new GitHubProjectConfig(project, gitHubCredentials, new GitHubUser(name, email), secret, transifexProject);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Cannot load github project configuration for '{}': {}", projectName, e.getMessage());
		}

		return null;
	}

	private static TransifexProjectConfig getTransifexProjectConfig(Connection connection, String projectName) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM public.transifex WHERE project = ?")) {
			stmt.setString(1, projectName);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String project = rs.getString("project");
					String gitHubProject = rs.getString("githubproject");

					String userId = rs.getString("userid");
					String password = rs.getString("password");
					TransifexCredentials transifexCredentials;
					if (userId != null && password != null) {
						transifexCredentials = new TransifexCredentials(userId, password);
					} else {
						transifexCredentials = getDefaultTransifexCredentials();
					}

					return new TransifexProjectConfig(project, transifexCredentials, gitHubProject);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Cannot load transifex project configuration for '{}': {}", projectName, e.getMessage());
		}

		return null;
	}

	public static TXGHProject getProject(String projectName) {
		try (Connection connection = getDatabaseConnection()) {
			GitHubProjectConfig gitHubProjectConfig = getGitHubProjectConfig(connection, projectName);
			if (gitHubProjectConfig == null) {
				return null;
			}

			TransifexProjectConfig transifexProjectConfig = getTransifexProjectConfig(connection, gitHubProjectConfig.getTransifexProjectName());
			if (transifexProjectConfig == null) {
				return null;
			}

			return new TXGHProject(gitHubProjectConfig, transifexProjectConfig);
		} catch (SQLException e) {
			LOGGER.error("Cannot get a database connection", e);
		}

		return null;
	}

	public static TXGHProject getProjectByTransifexName(String projectName) {
		try (Connection connection = getDatabaseConnection()) {
			TransifexProjectConfig transifexProjectConfig = getTransifexProjectConfig(connection, projectName);
			if (transifexProjectConfig == null) {
				return null;
			}

			GitHubProjectConfig gitHubProjectConfig = getGitHubProjectConfig(connection, transifexProjectConfig.getGitHubProject());
			if (gitHubProjectConfig == null) {
				return null;
			}

			return new TXGHProject(gitHubProjectConfig, transifexProjectConfig);
		} catch (SQLException e) {
			LOGGER.error("Cannot get a database connection", e);
		}

		return null;
	}
}
