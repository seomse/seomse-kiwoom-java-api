package com.seomse.stock.kiwoom.api.callback.control;

import com.seomse.stock.kiwoom.api.KiwoomApiCallbackStore;

/**
 * <pre>
 *  파 일 명 : OPT10013.java
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

public class OPT10013 extends DefaultCallbackController{
    private String itemCode = null;
    public OPT10013( String callbackId , String message) {
        super(callbackId,message);

    }

    @Override
    public void disposeMessage() {
        KiwoomApiCallbackStore.getInstance().putCallbackData(callbackId,message);
    }
}
