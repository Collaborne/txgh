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

public class TransifexResource {

    private final String projectSlug;
    private final String resourceSlug;
    private final String type;
    private final String sourceLanguage;
    private final String sourceFile;
    private final String fileFilter;
    private final Map<String, String> translationPaths;

    public TransifexResource(String projectSlug, String resourceSlug, String type, String sourceLanguage, String sourceFile, String fileFilter, Map<String, String> translationPaths) {
        this.projectSlug = projectSlug;
        this.resourceSlug = resourceSlug;
        this.type = type;
        this.sourceLanguage = sourceLanguage;
        this.sourceFile = sourceFile;
        this.fileFilter = fileFilter;
        this.translationPaths = translationPaths;
    }

    public String getTranslationPath(String language) {
        String result = translationPaths.get(language);
        if (result == null && fileFilter != null) {
            result = fileFilter.replace("<lang>", language);
        }
        return result;
    }

    public String getProjectSlug() {
        return projectSlug;
    }

    public String getResourceSlug() {
        return resourceSlug;
    }

    public String getType() {
        return type;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public String getFileFilter() {
        return fileFilter;
    }
}
