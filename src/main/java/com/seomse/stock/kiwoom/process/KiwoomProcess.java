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
package com.seomse.stock.kiwoom.process;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.date.DateUtil;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

public class KiwoomProcess {

    private static final Logger logger = getLogger(KiwoomProcess.class);

    private static String apiProcessName=null;
    private static String apiProcessPath=null;
    private static String apiVersionUpPath=null;

    private static final String TASKLIST = "tasklist";
    private static final String KILL = "taskkill /F /IM ";

    /**
     * CONFIG 설정
     */
    static {
        String fileContents = FileUtil.getFileContents(new File("config/kiwoom_config"), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        JSONObject jsonObject = new JSONObject(fileContents);
        apiProcessName = jsonObject.getString("process_name") ;
        apiProcessPath = jsonObject.getString("process_file_path") ;
        apiVersionUpPath = jsonObject.getString("version_up_file_path") ;
    }

    /**
     * 키움 API를 실행한다.
     * 이미 실행시, 종료후 실행 한다.
     */
    public static void rerunKiwoomApi(){
        logger.info("checkProcess..");
        new Thread(() -> {
            try {
                final Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();

                Scanner sc = new Scanner(process.getInputStream());
                if (sc.hasNextLine()) sc.nextLine();
                boolean doKillProcess = false;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    String unq = parts[0].substring(1).replaceFirst(".$", "");
                    if(unq.toLowerCase().equals(apiProcessName.toLowerCase() )){
                        try {
                            killProcess(unq);
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                logger.error(ExceptionUtil.getStackTrace(e));
                            }
                            doKillProcess = true;
                        } catch (Exception e) {
                            logger.error(ExceptionUtil.getStackTrace(e));
                        }
                    }
                }
                if(doKillProcess) {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        logger.error(ExceptionUtil.getStackTrace(e));
                    }
                }
                logger.info("runProcess");
                ProcessRunner.runProcess(apiProcessPath,true);
            }catch (IOException e) {}
        }).start();
        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 키움 API의 버전업을 실행 한다.
     */
    public static void startVersionUp(){
        String nowYmd = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyy-MM-dd");
        logger.info("startVersionUp process.. ["+nowYmd+"] ");
        try {Thread.sleep(1000l * 60l);} catch (InterruptedException e) {}

        try {
            final Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
            new Thread(() -> {
                Scanner sc = new Scanner(process.getInputStream());
                if (sc.hasNextLine()) sc.nextLine();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    String unq = parts[0].substring(1).replaceFirst(".$", "");
                    if(unq.equals("opversionup.exe")){
                        if(process.isAlive()){
                            logger.info("process kill opversionup.exe ["+nowYmd+"] ");
                        }
                    }
                }
            }).start();
            process.waitFor();
        }
        catch (IOException | InterruptedException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        ProcessRunner.runProcess(apiVersionUpPath,true);
    }

    /**
     * 프로세스를 종료 한다
     * @param serviceName 서비스명
     * @throws Exception
     */
    public static void killProcess(String serviceName) throws Exception {

        try {
            Runtime.getRuntime().exec(KILL + serviceName);
            logger.info(serviceName+" killed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
