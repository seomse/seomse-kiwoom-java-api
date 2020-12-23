/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seomse.stock.kiwoom.config;

import com.seomse.commons.utils.FileUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class KiwoomConfig {
    static JSONObject jsonObject;
    static String configPath = "config/kiwoom_config";
    static {
        String fileContents = FileUtil.getFileContents(new File(configPath), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        jsonObject = new JSONObject(fileContents);
    }
    public static void setConfigPath(String newConfigPath) {
        configPath = newConfigPath;
        String fileContents = FileUtil.getFileContents(new File(configPath), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        jsonObject = new JSONObject(fileContents);
    }
    public static void reLoad(){
        String fileContents = FileUtil.getFileContents(new File(configPath), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        jsonObject = new JSONObject(fileContents);
    }


    public static String getConfig(String configKey){
        String result=null;
        try{
            result = jsonObject.getString(configKey);
        } catch(JSONException e){
            return null;
        }
        return result;
    }
    public static void main(String [] args){
        System.out.println(KiwoomConfig.getConfig("process_file_path"));
    }
}
