package com.seomse.stock.kiwoom;

import com.seomse.api.server.ApiRequestConnectHandler;
import com.seomse.api.server.ApiRequestServer;
import com.seomse.api.server.ApiServer;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.date.DateUtil;
import com.seomse.stock.kiwoom.api.KiwoomClientManager;
import com.seomse.stock.kiwoom.data.KiwoomDateCrawler;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.Socket;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <pre>
 *  파 일 명 : KiwoomApiTest.java
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

public class KiwoomApiServer{
    private static final Logger logger = getLogger(KiwoomApiServer.class);
    int receivePort,sendPort;
    public KiwoomApiServer(int receivePort , int sendPort){
        this.receivePort = receivePort;
        this.sendPort = sendPort;
    }

    public void start(){
        ApiRequestConnectHandler handler =  request -> {

            request.setNotLog();
            Socket socket = request.getSocket();
            InetAddress inetAddress = socket.getInetAddress();
            String nodeKey = inetAddress.getHostAddress() + "," + inetAddress.getHostName();
            KiwoomClientManager.getInstance().addClient(nodeKey,request);
//            KiwoomClientManager.getInstance().getDatePriceData("005930","20200724");
        };

        ApiRequestServer apiRequestServer = new ApiRequestServer(sendPort, handler);
        apiRequestServer.start();

        ApiServer apiServer = new ApiServer(receivePort,"com.seomse.stock");
        apiServer.start();

        try {Thread.sleep(60000l);} catch (InterruptedException e) {}
        logger.info("START SERVER : " + DateUtil.getDateYmd(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));

        KiwoomDateCrawler kiwoomDateCrawler = new KiwoomDateCrawler();
        kiwoomDateCrawler.setState(Service.State.START);
        kiwoomDateCrawler.setSleepTime(60000l);
        kiwoomDateCrawler.start();

    }

    public static void main(String [] args){
        KiwoomApiServer apiServer = new KiwoomApiServer(33333,33334);
        //ApiServer apiServer = new ApiServer(33333,"com.seomse.stock");
        apiServer.start();
        //apiServer.start();
        //KiwoomClientManager.getInstance().clientListCheckStart();
    }
}
