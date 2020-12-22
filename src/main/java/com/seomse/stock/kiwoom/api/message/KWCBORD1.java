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

package com.seomse.stock.kiwoom.api.message;

import com.seomse.api.ApiMessage;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.stock.kiwoom.api.KiwoomApiCallbackData;
import com.seomse.stock.kiwoom.api.KiwoomApiCallbackStore;
import com.seomse.system.commons.SystemMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KWCBORD1 extends ApiMessage {

    private static final Logger logger = LoggerFactory.getLogger(KWCBORD1.class);
    private static final String PARAM_SEPARATOR = ",";
    private static final String CALLBACK_PACKAGE="com.seomse.stock.kiwoom.api.callback.control";
    @Override
    public void receive(String message) {
        try{
            String kiwoomApiCode = message.split(PARAM_SEPARATOR)[0];
            String data = message.substring(kiwoomApiCode.length()+1);

            logger.debug("CALLBACK Recieved : " + kiwoomApiCode + " data size :  "+data.split("\n").length);

        }catch(Exception e){
            logger.error(ExceptionUtil.getStackTrace(e));
            sendMessage(SystemMessageType.FAIL + ExceptionUtil.getStackTrace(e));
        }
    }
}
