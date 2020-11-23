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

package com.seomse.stock.kiwoom.data;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.DateUtil;
import com.seomse.crawling.CrawlingManager;
import com.seomse.crawling.CrawlingServer;
import com.seomse.crawling.core.http.HttpOptionDataKey;
import com.seomse.jdbc.JdbcQuery;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.api.KiwoomClientManager;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyCreditNo;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlStatusNo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static org.slf4j.LoggerFactory.getLogger;

public class KiwoomDateCreditCrawler {
    private static final Logger logger = getLogger(KiwoomDateCreditCrawler.class);

    private static String DATE_YMD = "yyyyMMdd";
    private static final String CREDIT_TYPE= "CREDIT";

    /**
     * 마지막 응답 결과 저장용 객체
     */
    private static class CrawlResponse{
        boolean isSuccess;
        String lastDate;
        String lastTime;
        List<KiwoomCrawlDailyCreditNo> chartNoList;
        public CrawlResponse(boolean isSuccess, String lastDate, String lastTime) {
            this.isSuccess = isSuccess;
            this.lastDate = lastDate;
            this.lastTime = lastTime;
        }
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
        public List<KiwoomCrawlDailyCreditNo> getChartNoList() { return chartNoList; }
        public void setChartNoList(List<KiwoomCrawlDailyCreditNo> chartNoList) { this.chartNoList = chartNoList; }
    }

    /**
     * 종목정보 업데이트
     * @param itemCode 종목 코드
     */
    public void updateSingle(String itemCode ){

        String timeCode = "D";
        /* 종목의 현재 수집 상태값 얻기 */
        KiwoomCrawlStatusNo statusNo = JdbcNaming.getObj(KiwoomCrawlStatusNo.class,"ITEM_CD='"+itemCode+"' AND TIME_CD='"+timeCode+"' AND CRAWL_TP='"+CREDIT_TYPE+"'");
        if(statusNo == null){
            statusNo = new KiwoomCrawlStatusNo();
            statusNo.setITEM_CD(itemCode);
            statusNo.setTIME_CD(timeCode);
            statusNo.setCRAWL_TP(CREDIT_TYPE);
        }
        logger.debug("""
                ITEM CODE [%s] STARTED! LAST CRAWL [%s]
                """.formatted(itemCode,statusNo.getYMD_LAST()));

        /* 날짜 계산, 새벽 ~ 오후 3시 이전엔 전일치를, 그 외엔 당일치를 */
        String nowDate = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyyMMdd");
        int mm = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(), "mm"));
        if(mm <= 15){
            nowDate = DateUtil.addDateYmd(nowDate, Calendar.DAY_OF_MONTH , -1 , "yyyyMMdd");
        }
        String startDate = nowDate;
        String startTime = "235959";

        List<KiwoomCrawlDailyCreditNo> allInsertChartNoList = new ArrayList<>();
        while(true){
            CrawlResponse response = crawlItem(itemCode, startDate);

            if( response.isSuccess() ){

                startDate = response.getLastDate();
                startTime = response.getLastTime();

                List<KiwoomCrawlDailyCreditNo> chartNoList = response.getChartNoList();
                List<KiwoomCrawlDailyCreditNo> insertChartNoList = getInsertChartNoList(chartNoList,statusNo);

                allInsertChartNoList.addAll(insertChartNoList);

                if(! (insertChartNoList.size() == chartNoList.size())){
                    // 수집한 데이터중 기존에 적재된 데이터가 있으면 종료
                    break;
                }
            } else {
                // response 가 실패시 종료
                break;
            }
        }
        if(allInsertChartNoList.size() > 0){
            allInsertChartNoList.sort((o1, o2) -> {
                Long o1Time = DateUtil.getDateTime(o1.getYMD(), DATE_YMD);
                Long o2Time = DateUtil.getDateTime(o2.getYMD(), DATE_YMD);
                return o2Time.compareTo(o1Time);
            });

            for (KiwoomCrawlDailyCreditNo daumCrawlChartNo : allInsertChartNoList) {
                JdbcNaming.insertOrUpdate(daumCrawlChartNo,false);
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
    private static List<KiwoomCrawlDailyCreditNo> getInsertChartNoList(List<KiwoomCrawlDailyCreditNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
        long statusFirst = DateUtil.getDateTime( statusNo.getYMD_FIRST(),DATE_YMD);
        long statusLast = DateUtil.getDateTime(statusNo.getYMD_LAST(),DATE_YMD);

        List<KiwoomCrawlDailyCreditNo> insertList = new ArrayList<>();

        for ( KiwoomCrawlDailyCreditNo chartNo : chartNoList ) {
            long chartTime = DateUtil.getDateTime(chartNo.getYMD(),DATE_YMD);
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
    private static void insertOrUpdateStatusNo(List<KiwoomCrawlDailyCreditNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
        if(chartNoList.size() == 0){
            return ;
        }
        Integer statusCnt = statusNo.getDATA_CNT();
        int insertCnt = chartNoList.size();
        long statusFirst = DateUtil.getDateTime( statusNo.getYMD_FIRST(),DATE_YMD);
        long statusLast = DateUtil.getDateTime(statusNo.getYMD_LAST(),DATE_YMD);

        /* 마지막 날짜와 최초 날짜 업데이트  */
        for (KiwoomCrawlDailyCreditNo chartNo : chartNoList) {
            long chartTime = DateUtil.getDateTime(chartNo.getYMD(),DATE_YMD);
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
     * @param startDate 시작일자
     * @return 수집결과
     */
    private static CrawlResponse crawlItem(String itemCode, String startDate) {


        CrawlResponse response = new CrawlResponse(false,"","");
        /*
        List<KiwoomCrawlDailyCreditNo> chartNoList = new ArrayList<>();
        String lastDate = "";
        try {
            String result = KiwoomClientManager.getInstance().getDateCreditData(itemCode,startDate);


            if (result != null) {
                for(int i=0; i< resultJsonArr.length();i++){
                    DaumCrawlChartNo chartNo = null ;
                    switch (crawlType){
                        case DAY_ITEM ->   chartNo = new DaumCrawlChartDayNo();
                        case DAY_MARKET ->   chartNo = new DaumCrawlChartMarketDayNo();
                        case FIVE_MINUTES_ETF ->  chartNo = new DaumCrawlChartETF5MNo();
                        case FIVE_MINUTES_MARKET ->  chartNo = new DaumCrawlChartMarket5MNo();
                        case FIVE_MINUTES_ITEM ->  chartNo = new DaumCrawlChart5MNo();
                    }

                    JSONObject resultDataJson = resultJsonArr.getJSONObject(i);
                    //String date = resultDataJson.getString("date");

                    String candleTime = resultDataJson.getString("candleTime");
                    String dataDateYmd = candleTime.replace("-","").replace(" ","").replace(":","");
                    dataDateYmd = dataDateYmd.substring(0,14);

                    if(i == 0){
                        lastDate = dataDateYmd.substring(0,8);
                        lastTime = dataDateYmd.substring(8);
                    }

                    Double tradePrice = resultDataJson.getDouble("tradePrice");
                    Double openingPrice = resultDataJson.getDouble("openingPrice");
                    Double highPrice = resultDataJson.getDouble("highPrice");
                    Double lowPrice = resultDataJson.getDouble("lowPrice");
                    Long candleAccTradePrice = resultDataJson.getLong("candleAccTradePrice");
                    Long candleAccTradeVolume = resultDataJson.getLong("candleAccTradeVolume");
                    Long tradeTime = resultDataJson.getLong("tradeTime");
                    Double change = null , changeRate = null, changePrice = null;
                    try {
                        change = resultDataJson.getDouble("change");
                        changeRate = resultDataJson.getDouble("changeRate");
                        changePrice = resultDataJson.getDouble("changePrice");
                    } catch(JSONException e){
                        // 데이터 미존재시 데이터 스킵
                    }


                    chartNo.setCode(itemCode);
                    chartNo.setYmd(dataDateYmd.substring(0,12));
                    chartNo.setOPEN_PRC(openingPrice);
                    chartNo.setHIGH_PRC(highPrice);
                    chartNo.setLOW_PRC(lowPrice);
                    chartNo.setCLOSE_PRC(tradePrice);

                    chartNo.setTRADE_VOL(candleAccTradeVolume);
                    chartNo.setTRADE_PRC_VOL(candleAccTradePrice);
                    chartNoList.add(chartNo);
                }

            }
        } catch(IllegalArgumentException e){
            logger.error(ExceptionUtil.getStackTrace(e));
        }

        if(chartNoList.size() > 0){
            response.setSuccess(true);
            response.setLastDate(lastDate);
            response.setLastTime(lastTime);
            response.setChartNoList(chartNoList);
        }
*/
        return response;
    }


}
