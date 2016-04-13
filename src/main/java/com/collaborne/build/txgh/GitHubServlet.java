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
package com.collaborne.build.txgh;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaborne.build.txgh.model.GitHubProject;
import com.collaborne.build.txgh.model.TXGHProject;
import com.collaborne.build.txgh.model.TransifexProject;
import com.collaborne.build.txgh.model.TransifexResource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHubServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final String SIGNATURE_SHA1_PREFIX = "sha1=";

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubServlet.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String event = request.getHeader("X-GitHub-Event");
        // Filter out irrelevant events quickly, without costly validation.
        if (!"ping".equals(event) && !"push".equals(event)) {
            LOGGER.debug("'{}' event ignored", event);
            return;
        }

        String payload;
        if ("application/json".equals(request.getContentType())) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                sb.append(reader.readLine());
            }
            payload = sb.toString();
        } else if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
            payload = request.getParameter("payload");
        } else {
            LOGGER.error("'{}' event with unexpected content type {}", event, request.getContentType());
            return;
        }

        if (payload == null) {
            LOGGER.error("'{}' event without payload", event);
            return;
        }

        Map<String, Object> parameterMap = request.getParameterMap();
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            if (entry.getValue() instanceof String[]) {
                LOGGER.debug(entry.getKey() + "::" + Arrays.toString((String[]) entry.getValue()));
            }
        }

        JsonObject payloadObject = new JsonParser().parse(payload).getAsJsonObject();
        JsonObject repository = payloadObject.get("repository").getAsJsonObject();
        String gitHubProjectName = repository.get("full_name").getAsString();

        TXGHProject project = Settings.getProject(gitHubProjectName);
        if (project == null) {
            // Nothing to do, we don't know this repository
            LOGGER.info("Ignoring hook for unknown repository '{}'", gitHubProjectName);
            return;
        }

        GitHubProject gitHubProject = project.getGitHubProject();

        // Validate the secret, if we have one: either the project has it configured, then it must
        // also be in the request, or the request has it, and then it must also be configured in the project.
        String signature = request.getHeader("X-Hub-Signature");
        if (signature != null) {
            String secret = gitHubProject.getConfig().getGitHubSecret();
            if (secret == null) {
                LOGGER.error("Secret is not configured for repository '{}', but required. Ignoring request", gitHubProjectName);
                return;
            }

            // Parse the signature into a byte array
            if (!signature.startsWith(SIGNATURE_SHA1_PREFIX)) {
                LOGGER.error("Unexpected signature type for repository '{}': {}", gitHubProjectName, signature);
                return;
            }

            if (!validateSignature(payload, secret, signature.substring(SIGNATURE_SHA1_PREFIX.length()))) {
                LOGGER.error("Invalid signature for repository '{}'", gitHubProjectName);
                return;
            }
        }

        // Handle the event
        if ("ping".equals(event)) {
            LOGGER.info("'ping' event: {}", payloadObject.get("zen").getAsString());
        } else if ("push".equals(event)) {
            processPushEvent(project, payloadObject);
        }
    }

    protected boolean validateSignature(String payload, String secret, String signature) {
        byte[] rawSignature = new byte[20];
        for (int stringIndex = 0, rawIndex = 0; stringIndex < signature.length(); stringIndex += 2, rawIndex++) {
            rawSignature[rawIndex] = (byte) Integer.parseInt(signature.substring(stringIndex, stringIndex + 2), 16);
        }

        try {
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes());
            if (MessageDigest.isEqual(rawHmac, rawSignature)) {
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("Cannot validate signature", e);
        }

        return false;
    }

    protected void processPushEvent(TXGHProject project, JsonObject payloadObject) throws IOException {
        // FIXME: Match up with the 'branch' in the TXGHProject
        if (payloadObject.get("ref").getAsString().equals("refs/heads/master")) {
            GitHubProject gitHubProject = project.getGitHubProject();
            GitHubApi gitHubApi = gitHubProject.getGitHubApi();
            Repository gitHubRepository = gitHubApi.getRepository();
            TransifexProject transifexProject = project.getTransifexProject();

            Map<String, TransifexResource> sourceFileMap = transifexProject.getSourceFileMap();
            Map<String, TransifexResource> updatedTransifexResourceMap = new LinkedHashMap<>();

            for (JsonElement commitElement : payloadObject.get("commits").getAsJsonArray()) {
                JsonObject commitObject = commitElement.getAsJsonObject();
                for (JsonElement modified : commitObject.get("modified").getAsJsonArray()) {
                    String modifiedSourceFile = modified.getAsString();
                    LOGGER.debug("Modified source file: " + modifiedSourceFile);
                    if (sourceFileMap.containsKey(modifiedSourceFile)) {
                        LOGGER.debug("Watched source file has been found: " + modifiedSourceFile);
                        updatedTransifexResourceMap.put(commitObject.get("id").getAsString(), sourceFileMap.get(modifiedSourceFile));
                    }
                }
            }

            for (Entry<String, TransifexResource> entry : updatedTransifexResourceMap.entrySet()) {

                String sourceFile = entry.getValue().getSourceFile();
                LOGGER.debug("Modified source file (watched): " + sourceFile);
                String treeSha = gitHubApi.getCommitTreeSha(gitHubRepository, entry.getKey());
                Tree tree = gitHubApi.getTree(gitHubRepository, treeSha);
                for (TreeEntry file : tree.getTree()) {
                    LOGGER.debug("Repository file: " + file.getPath());
                    if (sourceFile.equals(file.getPath())) {
                        transifexProject.getTransifexApi().update(entry.getValue(), gitHubApi.getFileContent(gitHubRepository, file.getSha()));
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
