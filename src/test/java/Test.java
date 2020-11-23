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

import com.seomse.commons.file.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String [] args){
        List<String> fileContentsList = FileUtil.getFileContentsList(new File("config/test"), "UTF-8");
        List<Integer> lineList = new ArrayList<>();
        for (int i=0; i<fileContentsList.size();i++) {
            String line = fileContentsList.get(i);
            if(line.contains("callbackID")){
                lineList.add(i+1);
            }
        }
        for (int i=0; i<fileContentsList.size();i++) {
            String line = fileContentsList.get(i);
            FileUtil.fileOutput(line+"\n","config/test2",true);
            if(lineList.contains(i)){
                FileUtil.fileOutput("\t\t\tnowCallbackID=callbackID;\n","config/test2",true);
            }

        }




    }
}
