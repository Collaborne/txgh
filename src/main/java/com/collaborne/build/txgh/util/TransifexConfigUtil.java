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
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile;

import com.collaborne.build.txgh.model.TransifexConfig;
import com.collaborne.build.txgh.model.TransifexResource;

public class TransifexConfigUtil {

    public static TransifexConfig getTransifexConfig(Reader reader) throws IOException {

        Map<String, String> languageMap = new HashMap<>();
        Map<String, TransifexResource> resourceMap = new HashMap<>();

        Ini ini = new Ini(reader);

        for (String sectionName : ini.keySet()) {
            if (sectionName.equals("main")) {
                languageMap.putAll(getLanguageMap(ini.get(sectionName).get("lang_map")));
            } else {
                resourceMap.putAll(getResourceMap(sectionName, ini.get(sectionName)));
            }
        }

        return new TransifexConfig(languageMap, resourceMap);
    }

    private static Map<String, String> getLanguageMap(String delimitedLanguages) {

        Map<String, String> languageMap = new HashMap<>();

        if (delimitedLanguages != null && !delimitedLanguages.isEmpty()) {

            String[] languages = delimitedLanguages.split(",");
            for (String language : languages) {
                String[] codes = language.split(":");
                if (codes.length > 1) {
                    languageMap.put(codes[0].trim(), codes[1].trim());
                }
            }
        }

        return languageMap;
    }

    private static Map<String, TransifexResource> getResourceMap(String sectionName, Profile.Section section) {

        Map<String, TransifexResource> resourceMap = new HashMap<>();

        String[] ids = sectionName.split("\\.");
        if (ids.length > 1) {
            Map<String, String> translationPaths = new HashMap<>();
            for (Map.Entry<String, String> entry : section.entrySet()) {
                if (entry.getKey().startsWith("trans.")) {
                    translationPaths.put(entry.getKey().substring("trans.".length()), entry.getValue());
                }
            }
            resourceMap.put(ids[1], new TransifexResource(ids[0], ids[1], section.get("type"), section.get("source_lang"), section.get("source_file"), section.get("file_filter"), translationPaths));
        }

        return resourceMap;
    }

}
