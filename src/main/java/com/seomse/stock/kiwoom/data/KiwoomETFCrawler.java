package com.seomse.stock.kiwoom.data;

import com.seomse.commons.service.Service;
import com.seomse.commons.utils.date.DateUtil;
import com.seomse.jdbc.JdbcQuery;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.api.KiwoomClientManager;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyCheckNo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <pre>
 *  파 일 명 : KiwoomDateCrawler.java
 *  설    명 :
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2020.07
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author Copyrights 2014 ~ 2020 by ㈜ WIGO. All right reserved.
 */

public class KiwoomETFCrawler extends Service {

    private static final Logger logger = getLogger(KiwoomETFCrawler.class);
    private String lastExecuteDate = "19700101";

    private List<String> itemList = null;

    private KiwoomClientManager clientManager = null;

    public KiwoomETFCrawler(){
        clientManager = KiwoomClientManager.getInstance();
    }

    @Override
    public void work() {
        String nowYmd = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyyMMdd");

        int hh = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(),"HH"));
        int mm = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(),"mm"));

        //If it is before 9 o'clock, previous day collect
        logger.debug( "nowYmd:" + nowYmd + " hh:" + hh + " mm:" + mm );
        if(hh <= 8){
            nowYmd = DateUtil.getDateYmd(System.currentTimeMillis()- (1000l*60l*60l*24l),"yyyyMMdd");
            logger.debug("nowYmd set previous day : " + nowYmd);
        }

        if((hh >= 16 || hh <= 8)    && !nowYmd.equals(lastExecuteDate)){
            try {
                Thread.sleep(30000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            crawlStart(nowYmd);
            lastExecuteDate = nowYmd;
        }
    }
    public void crawlStart(String date){
        getItemCodeList();
        List<String> crawlList = new ArrayList<>();
        for (String itemCode : itemList) {
            KiwoomCrawlDailyCheckNo checkNo = getCheckNo(itemCode);
            if(!checkNo.getYMD_LAST().equals(date)){
                crawlList.add(itemCode);
            } else {
                logger.debug("itemCode ["+itemCode+"] crawl complete. skip.");
            }
        }
        logger.info("CRAWL START ["+date+"] SIZE("+crawlList.size()+")");
        for (String itemCode : crawlList) {
            try {Thread.sleep(5000l);} catch (InterruptedException e) {}
            crawlItem(itemCode,date);
        }
    }

    private void getItemCodeList(){
        itemList = JdbcQuery.getStringList("SELECT ETF_CD FROM T_STOCK_ETF ");

    }

    private void crawlItem(String itemCode , String date) {
        logger.debug("itemCode : " + itemCode);
        clientManager.getDatePriceData(itemCode,date);
    }

    private KiwoomCrawlDailyCheckNo getCheckNo(String itemCode) {
        KiwoomCrawlDailyCheckNo no = JdbcNaming.getObj(KiwoomCrawlDailyCheckNo.class , "ITEM_CD='"+itemCode+"'");
        if(no == null){
            no = new KiwoomCrawlDailyCheckNo();
            no.setITEM_CD(itemCode);
            no.setCNT_DATA(0l);
            no.setYMD_LAST("19700101");
            no.setYMD_FIRST("22220202");
            JdbcNaming.insert(no);
        }
        return no;
    }


    public static void main(String [] args){
        new KiwoomETFCrawler().crawlStart("20200727");
    }
}
