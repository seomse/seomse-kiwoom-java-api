package com.seomse.stock.kiwoom.api.callback.control;

import com.seomse.commons.utils.date.DateUtil;
import com.seomse.jdbc.naming.JdbcNaming;
import com.seomse.stock.kiwoom.api.KiwoomApiLock;
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
    public OPT10015( String callbackId , String message) {
        super(callbackId,message);

    }

    @Override
    public void disposeMessage() {
        KiwoomApiLock.getInstance().putCallbackData(callbackId,message);
    }
}
