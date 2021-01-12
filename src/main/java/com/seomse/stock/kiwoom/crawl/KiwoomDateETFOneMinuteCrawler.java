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
import com.seomse.commons.utils.time.DateUtil;
import com.seomse.jdbc.JdbcQuery;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.KiwoomApiStart;
import com.seomse.stock.kiwoom.api.KiwoomApiCallbackData;
import com.seomse.stock.kiwoom.api.KiwoomApiSender;
import com.seomse.stock.kiwoom.crawl.no.KiwoomCrawlDailyETFOneMinuteNo;
import com.seomse.stock.kiwoom.crawl.no.KiwoomCrawlStatusNo;
import com.seomse.stock.kiwoom.process.KiwoomProcess;
import com.seomse.stock.kiwoom.process.KiwoomProcessMonitorService;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class KiwoomDateETFOneMinuteCrawler {
    private static final Logger logger = getLogger(KiwoomDateETFOneMinuteCrawler.class);

    private static final String DATE_YMD = "yyyyMMddHHmm";
    private static final String CRAWL_TYPE = "ETF_1M";
    private static final String DATA_SEPARATOR = "\\|";
    private static final Long CRAWL_SLEEP_TIME = 2000L;

    /**
     * 마지막 응답 결과 저장용 객체
     */
    private class CrawlResponse{
        boolean isSuccess;
        String lastDate;
        String lastTime;
        boolean hasNext=false;
        boolean isExceptionResult=false;
        List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList;
        public CrawlResponse(boolean isSuccess, String lastDate, String lastTime) {
            this.isSuccess = isSuccess;
            this.lastDate = lastDate;
            this.lastTime = lastTime;
        }

        public boolean isExceptionResult() { return isExceptionResult; }
        public void setExceptionResult(boolean exceptionResult) { isExceptionResult = exceptionResult; }
        public boolean hasNext() {return hasNext;}
        public void setHasNext(boolean hasNext) {this.hasNext = hasNext;}
        public boolean isSuccess() {
            return isSuccess;
        }
        public void setSuccess(boolean success) {
            isSuccess = success;
        }
        public String getLastDate() {
            return lastDate;
        }
        public void setLastDate(String lastDate) { this.lastDate = lastDate;}
        public String getLastTime() { return lastTime; }
        public void setLastTime(String lastTime) {
            this.lastTime = lastTime;
        }
        public List<KiwoomCrawlDailyETFOneMinuteNo> getChartNoList() { return chartNoList; }
        public void setChartNoList(List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList) { this.chartNoList = chartNoList; }
    }

    /**
     * 종목정보 업데이트
     * @param itemCode 종목 코드
     */
    public void updateSingle(String itemCode ){

        String timeCode = "1M";
        /* 종목의 현재 수집 상태값 얻기 */
        KiwoomCrawlStatusNo statusNo = JdbcNaming.getObj(KiwoomCrawlStatusNo.class,"ITEM_CD='"+itemCode+"' AND TIME_CD='"+timeCode+"' AND CRAWL_TP='"+ CRAWL_TYPE +"'");
        if(statusNo == null){
            statusNo = new KiwoomCrawlStatusNo();
            statusNo.setITEM_CD(itemCode);
            statusNo.setTIME_CD(timeCode);
            statusNo.setYMD_FIRST( statusNo.getYMD_FIRST() + "0000" );
            statusNo.setYMD_LAST( statusNo.getYMD_LAST()   + "0000" );
            statusNo.setCRAWL_TP(CRAWL_TYPE);
        }
        logger.debug("""
                ITEM CODE [%s] STARTED! LAST CRAWL [%s]
                """.formatted(itemCode,statusNo.getYMD_LAST()));


        List<KiwoomCrawlDailyETFOneMinuteNo> allInsertChartNoList = new ArrayList<>();
        int continueCode = 0;
        while(true){
            CrawlResponse response = crawlItem(itemCode, continueCode);

            if( response.isSuccess() ){
                if(continueCode == 0){
                    continueCode = 2;
                }
                List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList = response.getChartNoList();
                List<KiwoomCrawlDailyETFOneMinuteNo> insertChartNoList = getInsertChartNoList(chartNoList,statusNo);

                allInsertChartNoList.addAll(insertChartNoList);

                if(! (insertChartNoList.size() == chartNoList.size())){
                    // 수집한 데이터중 기존에 적재된 데이터가 있으면 종료
                    break;
                }
                if(!response.hasNext()){
                    break;
                }
            } else if( response.isExceptionResult() ){
                // 오류발생 종료
                return;
            }
            else {
                // response 가 실패시 종료
                break;
            }
        }
        if(allInsertChartNoList.size() > 0){
            allInsertChartNoList.sort((o1, o2) -> {
                Long o1Time = DateUtil.getDateTime(o1.getYMDHM(), DATE_YMD);
                Long o2Time = DateUtil.getDateTime(o2.getYMDHM(), DATE_YMD);
                return o2Time.compareTo(o1Time);
            });

            int chartSize = allInsertChartNoList.size();
            for (int i = 0; i < chartSize; i++) {
                KiwoomCrawlDailyETFOneMinuteNo nowNo = allInsertChartNoList.get(i);
                KiwoomCrawlDailyETFOneMinuteNo preNo = null;
                if(i+1 != chartSize){
                    preNo = allInsertChartNoList.get(i+1);
                    nowNo.setPREVIOUS_PRC( preNo.getCLOSE_PRC() );
                } else {
                    if(statusNo.getLAST_PRC() != null){
                        nowNo.setPREVIOUS_PRC( statusNo.getLAST_PRC() );
                    }
                }
            }

            for (KiwoomCrawlDailyETFOneMinuteNo chartNo : allInsertChartNoList) {
                JdbcNaming.insertOrUpdate(chartNo,false);
            }
            insertOrUpdateStatusNo(allInsertChartNoList,statusNo);
            logger.debug("""
                ITEM CODE [%s] END! COUNT [%d] UPDATE DATE [%s]
                """.formatted(itemCode,allInsertChartNoList.size() , statusNo.getYMD_LAST()));
        }
    }


    /**
     * DB에 적재될 NO 목록을 얻는다
     * @param chartNoList 차트데이터 NO 리스트
     * @param statusNo 수집상태 NO
     * @return 적재 목록
     */
    private List<KiwoomCrawlDailyETFOneMinuteNo> getInsertChartNoList(List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
        long statusFirst = DateUtil.getDateTime( statusNo.getYMD_FIRST(),DATE_YMD);
        long statusLast = DateUtil.getDateTime(statusNo.getYMD_LAST(),DATE_YMD);

        List<KiwoomCrawlDailyETFOneMinuteNo> insertList = new ArrayList<>();

        for ( KiwoomCrawlDailyETFOneMinuteNo chartNo : chartNoList ) {
            long chartTime = DateUtil.getDateTime(chartNo.getYMDHM(),DATE_YMD);
            if( chartTime > statusLast ){ // 마지막 날짜보다 수집 날짜가 큰 경우 업데이트
                insertList.add(chartNo);
            } else if ( chartTime < statusFirst ){ // 첫 날짜보다 수집 날짜가 작은 경우 업데이트
                insertList.add(chartNo);
            }
        }
        return insertList;
    }

    /**
     * 종목별 수집 상태 값 업데이트
     * @param chartNoList 차트데이터 NO 리스트
     * @param statusNo 수집상태 NO
     */
    private void insertOrUpdateStatusNo(List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
        if(chartNoList.size() == 0){
            return ;
        }
        Integer statusCnt = statusNo.getDATA_CNT();
        int insertCnt = chartNoList.size();
        long statusFirst = DateUtil.getDateTime( statusNo.getYMD_FIRST(),DATE_YMD);
        long statusLast = DateUtil.getDateTime(statusNo.getYMD_LAST(),DATE_YMD);

        /* 마지막 날짜와 최초 날짜 업데이트  */
        for (KiwoomCrawlDailyETFOneMinuteNo chartNo : chartNoList) {
            long chartTime = DateUtil.getDateTime(chartNo.getYMDHM(),DATE_YMD);
            if(chartTime > statusLast ){ // 마지막 날짜보다 수집 날짜가 큰 경우 업데이트
                statusLast = chartTime;
            }
            if (chartTime < statusFirst ){ // 첫 날짜보다 수집 날짜가 작은 경우 업데이트
                statusFirst = chartTime;
            }
        }

        /* 상태값 갱신 */
        statusNo.setYMD_FIRST( DateUtil.getDateYmd( statusFirst,DATE_YMD));
        statusNo.setYMD_LAST(DateUtil.getDateYmd(statusLast,DATE_YMD));
        statusNo.setDATA_CNT( statusCnt + insertCnt );
        statusNo.setUPT_DT(System.currentTimeMillis());
        JdbcNaming.insertOrUpdate(statusNo,false);
    }

    /**
     * ItemCode 수집 실행
     * @param itemCode 종목코드
     * @param continueCode 연속조회 코드
     * @return 수집결과
     */
    private CrawlResponse crawlItem(String itemCode, int continueCode) {

        CrawlResponse response = new CrawlResponse(false,"","");

        KiwoomApiCallbackData callbackData = KiwoomApiSender.getInstance().getMinuteData(itemCode,1,0,continueCode);
        if(callbackData == null){
            response.setExceptionResult(true);
            return response;
        }
        String etfOneMinuteAllData = callbackData.getCallbackData();
        if(etfOneMinuteAllData.length() == 0 || etfOneMinuteAllData.equals("FAIL")){
            return response;
        }

        /*
            @return 현재가 / 거래량 / 체결시간 / 시가 / 고가 / 저가 / 그외 나머지 데이터 빈값
     *         숫자앞 +/- 기호 => 상승/하락의 표기뿐, 지워서 숫자값만 사용 해야 함
         */

        String [] etfOneMinuteDataArr = etfOneMinuteAllData.split("\n");
        String lastDate=null;
        List<KiwoomCrawlDailyETFOneMinuteNo> chartNoList = new ArrayList<>();

        for (int i = 0; i < etfOneMinuteDataArr.length; i++) {
            String etfOneMinuteData = etfOneMinuteDataArr[i];

            String [] etfOneMinuteArr = etfOneMinuteData.split(DATA_SEPARATOR);

            int closePrice = Integer.parseInt(etfOneMinuteArr[0].substring(1));
            long tradeVolume = Long.parseLong(etfOneMinuteArr[1]);
            String date = etfOneMinuteArr[2].substring(0,12);
            int openPrice = Integer.parseInt(etfOneMinuteArr[3].substring(1));
            int highPrice = Integer.parseInt(etfOneMinuteArr[4].substring(1));
            int lowPrice = Integer.parseInt(etfOneMinuteArr[5].substring(1));


            KiwoomCrawlDailyETFOneMinuteNo chartNo = new KiwoomCrawlDailyETFOneMinuteNo();
            chartNo.setETF_CD(itemCode);
            chartNo.setYMDHM(date);
            chartNo.setCLOSE_PRC(closePrice);
            chartNo.setHIGH_PRC(highPrice);
            chartNo.setLOW_PRC(lowPrice);
            chartNo.setOPEN_PRC(openPrice);
            chartNo.setTRADE_VOL(tradeVolume);
            chartNoList.add(chartNo);
        }

        if(chartNoList.size() > 0){
            response.setSuccess(true);
            response.setLastDate(lastDate);
            response.setChartNoList(chartNoList);
        }
        if(callbackData.hasNext()){
            response.setHasNext(true);
        }

        try {
            Thread.sleep(CRAWL_SLEEP_TIME);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return response;
    }

    public static void main(String [] args){

        KiwoomApiStart apiServer = new KiwoomApiStart(33333,33334);
        apiServer.start();
        new Thread(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
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

            logger.info("ETF 1 MIN complete");

        }).start();
    }
}