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
package com.seomse.stock.kiwoom.crawl;

import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.date.DateUtil;
import com.seomse.jdbc.JdbcQuery;
import com.seomse.stock.kiwoom.KiwoomApiStart;
import com.seomse.stock.kiwoom.process.KiwoomProcess;
import com.seomse.stock.kiwoom.process.KiwoomProcessMonitorService;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class KiwoomDateAllCrawler {

    private static final Logger logger = getLogger(KiwoomDateAllCrawler.class);

    public void start(){

        String fileContents = FileUtil.getFileContents(new File("config/kiwoom_config"), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        JSONObject jsonObject = new JSONObject(fileContents);
        int receivePort = jsonObject.getInt("api_receive_port") ;
        int sendPort = jsonObject.getInt("api_send_port") ;


        while(true){

            int nowTime = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(),"HH"));
            String nowDate = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyy-MM-dd");
            logger.debug("nowTime:"+nowTime+"");
            if(nowTime <= 6) {


                KiwoomApiStart apiServer = new KiwoomApiStart(receivePort,sendPort);
                apiServer.start();

                logger.debug("""
                        [%s] KIWOOM ALL DATE CRAWL START
                        """.formatted(nowDate));

                updateItem();

                logger.debug("""
                        [%s] KIWOOM ALL DATE CRAWL END!
                        """.formatted(nowDate));
                KiwoomProcess.killKiwoomApi();
                try {
                    Thread.sleep(1000L * 60L * 60L * 10L);
                } catch (InterruptedException e) {
                    logger.error(ExceptionUtil.getStackTrace(e));
                }
            } else {
                // 10분 대기
                try {
                    logger.debug("SLEEP 10 MINUTES..");
                    Thread.sleep(1000L * 60L * 10L);
                } catch (InterruptedException e) {
                    logger.error(ExceptionUtil.getStackTrace(e));
                }

            }

        }

    }

    /**
     * 신용/체결강도 정보를 수집 한다.
     */
    public void updateItem(){
        List<String> codeList = JdbcQuery.getStringList("SELECT ITEM_CD FROM T_STOCK_ITEM WHERE DELISTING_DT IS NULL");
//
        for (String code : codeList) {
            new KiwoomDateStrengthCrawler().updateSingle(code);
        }
//
        for (String code : codeList) {
            new KiwoomDateCreditCrawler().updateSingle(code);
        }

        codeList = JdbcQuery.getStringList("SELECT ETF_CD FROM T_STOCK_ETF WHERE DELISTING_DT IS NULL");

        for (String code : codeList) {
            new KiwoomDateETFStrengthCrawler().updateSingle(code);
        }

        String [] etfCodeArr = {
                "252670",
                "114800",
                "122630",
                "251340",
                "233740",

                "252710",
                "069500",
                "310970",
                "229200",
                "305720",

                "364980",
                "305540",
                "204480",
                "292150",
                "102110"
        };
        for (String etfCode : etfCodeArr) {
            logger.info("ETF 1 MIN [" + etfCode+"] start");
            new KiwoomDateETFOneMinuteCrawler().updateSingle(etfCode);
            logger.info("ETF 1 MIN [" + etfCode+"] end");
        }

    }




    public static void main(String[] args){
        logger.debug("KiwoomDateAllCrawler STARTED");
        new KiwoomDateAllCrawler().start();
    }
}
