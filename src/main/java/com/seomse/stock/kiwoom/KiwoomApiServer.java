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

package com.seomse.stock.kiwoom;

import com.seomse.api.server.ApiRequestConnectHandler;
import com.seomse.api.server.ApiRequestServer;
import com.seomse.api.server.ApiServer;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.date.DateUtil;
import com.seomse.stock.kiwoom.api.KiwoomClientManager;
import com.seomse.stock.kiwoom.data.KiwoomDateCrawler;
import com.seomse.stock.kiwoom.process.KiwoomProcessMonitorService;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.net.InetAddress;
import java.net.Socket;

import static org.slf4j.LoggerFactory.getLogger;

public class KiwoomApiServer extends Thread{
    private static final Logger logger = getLogger(KiwoomApiServer.class);
    int receivePort,sendPort;
    public KiwoomApiServer(int receivePort , int sendPort){
        this.receivePort = receivePort;
        this.sendPort = sendPort;
    }
    @Override
    public void run(){
        ApiRequestConnectHandler handler =  request -> {
            Socket socket = request.getSocket();
            InetAddress inetAddress = socket.getInetAddress();
            String nodeKey = inetAddress.getHostAddress() + "," + inetAddress.getHostName();
            logger.debug("NEW NODE CONNECTED : " + nodeKey);
            KiwoomClientManager.getInstance().addClient(nodeKey,request);
        };

        ApiRequestServer apiRequestServer = new ApiRequestServer(sendPort, handler);
        apiRequestServer.start();

        ApiServer apiServer = new ApiServer(receivePort,"com.seomse.stock");
        apiServer.start();

        logger.info("START SERVER : " + DateUtil.getDateYmd(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
    }
}
