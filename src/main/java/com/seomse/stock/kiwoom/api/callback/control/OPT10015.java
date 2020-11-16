package com.seomse.stock.kiwoom.api.callback.control;

import com.seomse.commons.utils.date.DateUtil;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyCheckNo;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyPriceNo;

/**
 * <pre>
 *  파 일 명 : OPT10080.java
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

public class OPT10015 extends DefaultCallbackController{
    private String itemCode = null;
    public OPT10015(String param, String message) {
        super(param,message);
        itemCode = param;
    }

    @Override
    public void disposeMessage() {
        String[] messageArr = message.split("\n",-1);
        KiwoomCrawlDailyCheckNo checkNo = getCheckNo(param);
        String lastYmd = checkNo.getYMD_LAST();
        long lastDate = -1;
        if(lastYmd != null){
            lastDate = DateUtil.getDateTime(lastYmd,"yyyyMMdd") ;
        }

        for (String message : messageArr) {
            KiwoomCrawlDailyPriceNo priceNo = makePriceNo(message);
            String priceYmd = priceNo.getYMD();
            long priceDate = DateUtil.getDateTime(priceYmd,"yyyyMMdd") ;
            if(priceDate > lastDate){
                JdbcNaming.insert(priceNo);
            }
        }
    }

    private KiwoomCrawlDailyPriceNo makePriceNo(String message) {
        /*
0            20200724
1            +54200
2            2
3            +100
4            +0.18
5            10994535
6            594538
7            2044
8           +0.01
9           10881139
10          +98.97
11          22065
12          +0.20
13          10994535
14          594538
15
16
17
18
19
20
21
22
23
24          111
25          +0.01
26          588415
27          +98.97
28          1196
29          +0.20
         */
        KiwoomCrawlDailyPriceNo priceNo = new KiwoomCrawlDailyPriceNo();

        priceNo.setITEM_CD(itemCode);
//        priceNo.setYMD();
        String [] dataArr = message.split(DATA_SEPARATOR);
        String ymd = dataArr[0];
        int price = Integer.parseInt( dataArr[1].substring(1) );
        String updownPrice = dataArr[3];
        int prePrice = price;
        if(updownPrice.startsWith("-")){
            prePrice -= Integer.parseInt(updownPrice.substring(1));
        } else {
            prePrice += Integer.parseInt(updownPrice.substring(1));
        }
        int volume = Integer.parseInt( dataArr[5] );
        return null;
    }

    private KiwoomCrawlDailyCheckNo getCheckNo(String itemCode) {
        KiwoomCrawlDailyCheckNo no = JdbcNaming.getObj(KiwoomCrawlDailyCheckNo.class , "ITEM_CD='"+itemCode+"'");
        if(no == null){
            no = new KiwoomCrawlDailyCheckNo();
            no.setITEM_CD(itemCode);
            no.setCNT_DATA(0l);
        }
        return no;
    }
}
