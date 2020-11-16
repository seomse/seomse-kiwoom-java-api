package com.seomse.stock.kiwoom.api.message;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.stock.kiwoom.api.callback.control.DefaultCallbackController;
import com.seomse.system.commons.SystemMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * <pre>
 *  파 일 명 : KWCB0001.java
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

public class KWCBTR01 extends ApiMessage {

    private static final Logger logger = LoggerFactory.getLogger(KWCBTR01.class);
    private static final String PARAM_SEPARATOR = ",";
    private static final String CALLBACK_PACKAGE="com.seomse.stock.kiwoom.api.callback.control";
    @Override
    public void receive(String message) {
        try{
            String kiwoomApiCode = message.split(PARAM_SEPARATOR)[0];
            String result = message.substring(kiwoomApiCode.length()+1);
            String param=null,data=null;
            if(result.contains(PARAM_SEPARATOR)) {
                try {
                    param = result.substring(0, result.lastIndexOf(PARAM_SEPARATOR));
                    data = result.substring(result.lastIndexOf(PARAM_SEPARATOR) + 1);
                } catch(IndexOutOfBoundsException e){
                    logger.error(ExceptionUtil.getStackTrace(e));
                    param = "";
                    data = result;
                }
            } else {
                param = "";
                data = result;
            }
            logger.debug("CALLBACK Recieved : " + kiwoomApiCode + " data size :  "+data.split("\n").length);
            Class<?> callBack = Class.forName(CALLBACK_PACKAGE + "." + kiwoomApiCode);
            Constructor<?> constructor = callBack.getConstructor(String.class,String.class);
            DefaultCallbackController defaultCallbackController = (DefaultCallbackController) constructor.newInstance(param,data);
            defaultCallbackController.disposeMessage();
            sendMessage(SystemMessageType.SUCCESS);
        }catch(Exception e){
            logger.error(ExceptionUtil.getStackTrace(e));
            sendMessage(SystemMessageType.FAIL + ExceptionUtil.getStackTrace(e));
        }
    }
}
