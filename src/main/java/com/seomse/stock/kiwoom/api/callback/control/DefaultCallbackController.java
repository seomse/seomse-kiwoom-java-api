package com.seomse.stock.kiwoom.api.callback.control;

/**
 * <pre>
 *  파 일 명 : DefaultCallbackController.java
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

public abstract class DefaultCallbackController {
    protected String message;
    protected String callbackId;
    protected static final String PARAM_SEPARATOR=",";
    protected static final String DATA_SEPARATOR="\\|";
    public DefaultCallbackController( String callbackId,String message){
        this.message = message;
        this.callbackId = callbackId;
    }

    public abstract void disposeMessage();
}
