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
package in.drifted.txgh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import in.drifted.txgh.model.Config;
import in.drifted.txgh.model.GitHubCredentials;
import in.drifted.txgh.model.GitHubProjectConfig;
import in.drifted.txgh.model.TransifexCredentials;
import in.drifted.txgh.model.TransifexProjectConfig;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

public class Settings {

    public static Config getConfig() throws IOException {

        Config config;
        
        Path localResourcesPath = getLocalResourcesPath();

        try (Reader reader = localResourcesPath != null ? Files.newBufferedReader(localResourcesPath.resolve("config.json"), StandardCharsets.UTF_8) : new InputStreamReader(Settings.class.getResourceAsStream("config.json"), StandardCharsets.UTF_8)) {
            GitHubCredentials defaultGitHubCredentials = getDefaultGitHubCredentials();
            TransifexCredentials defaultTransifexCredentials = getDefaultTransifexCredentials();

            Gson gson = new GsonBuilder().create();
            config = gson.fromJson(reader, Config.class);

            for (Entry<String, GitHubProjectConfig> entry : config.getGitHubProjectConfigMap().entrySet()) {
                entry.getValue().setGitHubProjectUrl(entry.getKey());
                if (entry.getValue().getGitHubCredentials() == null) {
                    entry.getValue().setGitHubCredentials(defaultGitHubCredentials);
                }
            }

            for (Entry<String, TransifexProjectConfig> entry : config.getTransifexProjectConfigMap().entrySet()) {
                if (entry.getValue().getTransifexCredentials() == null) {
                    entry.getValue().setTransifexCredentials(defaultTransifexCredentials);
                }
            }
        }

        return config;
    }

    public static Path getLocalResourcesPath() {
                
        Path localResourcePath = null;
        
        String configPath = System.getenv("TXGH_CONFIG_PATH");
        
        if (configPath != null) {
            Path candidatePath = Paths.get(configPath);
            if (Files.exists(candidatePath)) {
                localResourcePath = candidatePath;
            }
        }
                
        return localResourcePath;
    }
    
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
}
