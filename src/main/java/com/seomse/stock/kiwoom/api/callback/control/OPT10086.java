package com.seomse.stock.kiwoom.api.callback.control;

import com.seomse.commons.utils.date.DateUtil;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyCheckNo;
import com.seomse.stock.kiwoom.data.no.KiwoomCrawlDailyPriceNo;

import java.util.ArrayList;
import java.util.List;

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

public class OPT10086 extends DefaultCallbackController{
    private String itemCode = null;
    public OPT10086(String param, String message) {
        super(param,message);
        itemCode = param;
    }

    @Override
    public void disposeMessage() {
        String[] messageArr = message.split("\n",-1);
        KiwoomCrawlDailyCheckNo checkNo = getCheckNo(param);
        String lastYmd = checkNo.getYMD_LAST();
        List<KiwoomCrawlDailyPriceNo> insertList = new ArrayList<>();
        for(int i=messageArr.length-1;i>=0;i--){
            String data = messageArr[i];
            KiwoomCrawlDailyPriceNo priceNo = makePriceNo(data);
            String priceYmd = priceNo.getYMD();
            long priceDate = DateUtil.getDateTime(priceYmd,"yyyyMMdd") ;
            long lastDate = DateUtil.getDateTime(lastYmd,"yyyyMMdd") ;
            if(priceDate > lastDate){
                insertList.add(priceNo);
                checkNo.setCNT_DATA( checkNo.getCNT_DATA() + 1 );
                checkNo.setYMD_LAST(priceYmd);
                long firstDate = DateUtil.getDateTime(checkNo.getYMD_FIRST(),"yyyyMMdd");
                if(firstDate > priceDate){
                    checkNo.setYMD_FIRST(priceYmd);
                }
            }
        }
        if(insertList.size() > 0) {
            JdbcNaming.insert(insertList);
            JdbcNaming.update(checkNo, false);
        }
    }

    private KiwoomCrawlDailyPriceNo makePriceNo(String message) {
        KiwoomCrawlDailyPriceNo priceNo = new KiwoomCrawlDailyPriceNo();

        priceNo.setITEM_CD(itemCode);
        String [] dataArr = message.split(DATA_SEPARATOR);
        String ymd = dataArr[0];
        Double price = Double.parseDouble( dataArr[4].substring(1) );

        Double startPrice = Double.parseDouble( dataArr[1].substring(1) );
        Double lowPrice = Double.parseDouble( dataArr[2].substring(1) );
        Double highPrice = Double.parseDouble( dataArr[3].substring(1) );

        String updownPrice = dataArr[5];
        Double prePrice = price;
        if(updownPrice.startsWith("-")){
            prePrice -= Integer.parseInt(updownPrice.substring(1));
        } else {
            prePrice += Integer.parseInt(updownPrice.substring(1));
        }
        Double volume = Double.parseDouble( dataArr[7] );

        Double personVolume=Double.parseDouble(dataArr[10].substring(1) );
        Double goverVolume=Double.parseDouble(dataArr[11].substring(1) );
        Double foreignerVolume=Double.parseDouble(dataArr[12].substring(1) );
        Double foreignVolume=Double.parseDouble(dataArr[13].substring(1) );
        Double programVolume=Double.parseDouble(dataArr[13].substring(1) );

        Double creditRate = Double.parseDouble(dataArr[22]);

        priceNo.setYMD(ymd);
        priceNo.setITEM_CD(itemCode);
        priceNo.setSTART_PRC(startPrice);
        priceNo.setLAST_PRC(price);
        priceNo.setPREVIOUS_PRC(prePrice);
        priceNo.setMIN_PRC(lowPrice);
        priceNo.setMAX_PRC(highPrice);
        priceNo.setINDIVIDUAL_TRADE_VOL(personVolume);
        priceNo.setINSTITUTION_TRADE_VOL(goverVolume);
        priceNo.setFOREIGNER_TRADE_VOL(foreignerVolume);
        priceNo.setFOREIGN_TRADE_VOL(foreignVolume);
        priceNo.setPROGRAM_TRADE_VOL(programVolume);
        priceNo.setCREDIT_RT(creditRate);
        priceNo.setTRADE_VOL(volume);

        return priceNo;
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
}
