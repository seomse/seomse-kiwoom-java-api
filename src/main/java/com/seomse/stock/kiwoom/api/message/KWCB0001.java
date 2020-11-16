package com.seomse.stock.kiwoom.api.message;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.system.commons.SystemMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class KWCB0001 extends ApiMessage {

    private static final Logger logger = LoggerFactory.getLogger(KWCB0001.class);
    private static final String PARAM_SEPARATOR = ",";
    @Override
    public void receive(String message) {
        try{

            String kiwoomApiCode = message.split(PARAM_SEPARATOR)[0];
            String result = message.substring(kiwoomApiCode.length()+1);
            //KiwoomClientManager.getInstance().addClient(apiId , this.communication);
            logger.debug("CALLBACK Recieved : " + kiwoomApiCode);
            logger.debug(result);
            sendMessage(SystemMessageType.SUCCESS);
        }catch(Exception e){
            sendMessage(SystemMessageType.FAIL + ExceptionUtil.getStackTrace(e));
        }
    }
}
