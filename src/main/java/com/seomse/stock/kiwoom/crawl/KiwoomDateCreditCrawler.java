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
import com.seomse.stock.kiwoom.api.KiwoomApiSender;
import com.seomse.stock.kiwoom.crawl.no.KiwoomCrawlDailyCreditNo;
import com.seomse.stock.kiwoom.crawl.no.KiwoomCrawlStatusNo;
import com.seomse.stock.kiwoom.process.KiwoomProcess;
import com.seomse.stock.kiwoom.process.KiwoomProcessMonitorService;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
public class KiwoomDateCreditCrawler {
    private static final Logger logger = getLogger(KiwoomDateCreditCrawler.class);

    private String DATE_YMD = "yyyyMMdd";
    private static final String CRAWL_TYPE = "CREDIT";
    private static final String DATA_SEPARATOR = "\\|";
    private static final Long CRAWL_SLEEP_TIME = 2000L;

    /**
     * 마지막 응답 결과 저장용 객체
     */
    private class CrawlResponse{
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
        KiwoomCrawlStatusNo statusNo = JdbcNaming.getObj(KiwoomCrawlStatusNo.class,"ITEM_CD='"+itemCode+"' AND TIME_CD='"+timeCode+"' AND CRAWL_TP='"+ CRAWL_TYPE +"'");
        if(statusNo == null){
            statusNo = new KiwoomCrawlStatusNo();
            statusNo.setITEM_CD(itemCode);
            statusNo.setTIME_CD(timeCode);
            statusNo.setCRAWL_TP(CRAWL_TYPE);
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

        List<KiwoomCrawlDailyCreditNo> allInsertChartNoList = new ArrayList<>();
        while(true){
            CrawlResponse response = crawlItem(itemCode, startDate);

            if( response.isSuccess() ){

                startDate = response.getLastDate();
                startDate = DateUtil.addDateYmd(startDate,Calendar.DAY_OF_YEAR,-1,"yyyyMMdd");
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
            if(allInsertChartNoList.size() > 0) {
                insertOrUpdateStatusNo(allInsertChartNoList, statusNo);
            }
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
    private List<KiwoomCrawlDailyCreditNo> getInsertChartNoList(List<KiwoomCrawlDailyCreditNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
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
    private void insertOrUpdateStatusNo(List<KiwoomCrawlDailyCreditNo> chartNoList, KiwoomCrawlStatusNo statusNo) {
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
    private CrawlResponse crawlItem(String itemCode, String startDate) {


        CrawlResponse response = new CrawlResponse(false,"","");

        String dateCreditAllData = KiwoomApiSender.getInstance().getDateCreditData(itemCode, startDate);
        if(dateCreditAllData == null || dateCreditAllData.length() == 0 || dateCreditAllData.equals("FAIL")){
            return response;
        }

        //20201112|-10750|5         |-250  |904389|598331|660025|2540150|25643|-61694|25.40  | 5.13
        //   0    |  1   |      2   |  3   |   4  |5     |6     | 7     | 8   |  9   | 10    | 11
        //일자    /현재가  /전일대비기호/전일대비/거래량 /신규   /상환   /잔고    /금액  /대비   /공여율  /잔고율
        /*
        CREDIT_TOTAL_VOL	NUMBER	Yes		23	신용거래량
        CREDIT_NEW_VOL	    NUMBER	Yes		24	신용신규량
        CREDIT_REPAY_VOL	NUMBER	Yes		25	신용상환량
        CREDIT_BALANCE_VOL	NUMBER	Yes		26	신용잔고량
        CREDIT_PRC_VOL	    NUMBER	Yes		27	신용금액량
        CREDIT_CHANGE_VOL	NUMBER	Yes		28	신용변동량
        CREDIT_EXPOSURE_RT	NUMBER	Yes		29	신용공여율
        CREDIT_BALANCE_RT	NUMBER	Yes		22	신용잔고율
         */

        String [] dateCreditDataArr = dateCreditAllData.split("\n");
        String lastDate=null;
        List<KiwoomCrawlDailyCreditNo> chartNoList = new ArrayList<>();

        for (int i = 0; i < dateCreditDataArr.length; i++) {
            String dateCreditData = dateCreditDataArr[i];
            String [] dateCreditArr = dateCreditData.split(DATA_SEPARATOR);
            String date = dateCreditArr[0];
            String creditVolumeStr = dateCreditArr[4];
            String creditNewVolumeStr = dateCreditArr[5];
            String creditRepayVolumeStr = dateCreditArr[6];
            String creditBalanceVolumeStr = dateCreditArr[7];
            String creditPriceVolumeStr = dateCreditArr[8];
            String creditChangeVolumeStr = dateCreditArr[9];
            String creditExposureRateStr = dateCreditArr[10];
            String creditBalanceRateStr = dateCreditArr[11];

            long creditVolume = Long.parseLong(creditVolumeStr);
            long creditNewVolume = Long.parseLong(creditNewVolumeStr);
            long creditRepayVolume = Long.parseLong(creditRepayVolumeStr);
            long creditBalanceVolume = Long.parseLong(creditBalanceVolumeStr);
            long creditPriceVolume = Long.parseLong(creditPriceVolumeStr);
            long creditChangeVolume = Long.parseLong(creditChangeVolumeStr);
            double creditExposureRate = Double.parseDouble(creditExposureRateStr);
            double creditBalanceRate = Double.parseDouble(creditBalanceRateStr);

            KiwoomCrawlDailyCreditNo chartNo = new KiwoomCrawlDailyCreditNo();
            chartNo.setITEM_CD(itemCode);
            chartNo.setYMD(date);
            if(i == dateCreditDataArr.length-1){
                lastDate = date.substring(0,8);
            }
            chartNo.setCREDIT_TOTAL_VOL(creditVolume);
            chartNo.setCREDIT_NEW_VOL(creditNewVolume);
            chartNo.setCREDIT_REPAY_VOL(creditRepayVolume);
            chartNo.setCREDIT_BALANCE_VOL(creditBalanceVolume);
            chartNo.setCREDIT_PRC_VOL(creditPriceVolume);
            chartNo.setCREDIT_CHANGE_VOL(creditChangeVolume);
            chartNo.setCREDIT_EXPOSURE_RT(creditExposureRate);
            chartNo.setCREDIT_BALANCE_RT(creditBalanceRate);
            chartNoList.add(chartNo);
        }

        if(chartNoList.size() > 0){
            response.setSuccess(true);
            response.setLastDate(lastDate);
            response.setChartNoList(chartNoList);
        }

        try {
            Thread.sleep(CRAWL_SLEEP_TIME);
        } catch (InterruptedException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return response;
    }

    public static void main(String [] args){

        String fileContents = FileUtil.getFileContents(new File("config/kiwoom_config"), "UTF-8");
        fileContents = fileContents.replace("\\\\","\\");
        fileContents = fileContents.replace("\\","\\\\");
        JSONObject jsonObject = new JSONObject(fileContents);
        int receivePort = jsonObject.getInt("api_receive_port") ;
        int sendPort = jsonObject.getInt("api_send_port") ;


        KiwoomProcessMonitorService monitorService = new KiwoomProcessMonitorService();
        monitorService.setSleepTime(60000L);
        monitorService.setState(Service.State.START);
        monitorService.start();

        KiwoomApiStart apiServer = new KiwoomApiStart(receivePort,sendPort);
        apiServer.start();

        KiwoomProcess.rerunKiwoomApi();


        new Thread(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }

            logger.info("item trade CREDIT start");
            List<String> codeList = JdbcQuery.getStringList("SELECT ITEM_CD FROM T_STOCK_ITEM WHERE DELISTING_DT IS NULL AND ITEM_CD NOT IN ( SELECT ITEM_CD FROM T_CRAWLING_KIWOOM_TR WHERE YMD_LAST LIKE '202011%' OR YMD_LAST LIKE '202012%' )");

            int index = 0;
            for(int i=codeList.size()-1;i>=0;i--){
                String code = codeList.get(i);
                int nowTime = Integer.parseInt( DateUtil.getDateYmd(System.currentTimeMillis(),"HH") );
                if(nowTime >= 9 && nowTime <= 16){
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        logger.error(ExceptionUtil.getStackTrace(e));
                    }
                    i--;
                    continue;
                }
                try {
                    logger.debug("CREDIT code: " + code + " " + ++index + " " + codeList.size());

                    new KiwoomDateCreditCrawler().updateSingle(code);
                }catch(Exception e){
                    logger.error(ExceptionUtil.getStackTrace(e));
                }
            }
            logger.info("item trade CREDIT complete");

        }).start();
    }
}