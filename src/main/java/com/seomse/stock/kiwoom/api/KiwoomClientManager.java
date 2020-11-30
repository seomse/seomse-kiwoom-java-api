package com.seomse.stock.kiwoom.api;

import com.seomse.api.ApiRequest;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.stock.kiwoom.process.KiwoomProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 *  파 일 명 : KiwoomClientManager.java
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

public class KiwoomClientManager {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Logger logger = LoggerFactory.getLogger(KiwoomClientManager.class);
    private static class SingleTonHolder{ private static final KiwoomClientManager INSTANCE = new KiwoomClientManager();}
    private KiwoomClientManager(){}
    public static KiwoomClientManager getInstance(){return SingleTonHolder.INSTANCE;}
//    private Map<String,KiwoomClient> kiwoomClientMap = new HashMap<>();
    KiwoomClient client=null;

    private static final String PARAM_CODE_SEPARATOR = ",";
    private static final String PARAM_DATA_SEPARATOR = "|";

    public void addClient(String clientId, ApiRequest request){
        lock.lock();
        KiwoomClient newClient = new KiwoomClient(clientId , request);
        this.client = newClient;
        lock.unlock();
    }

    public void getDatePriceData(String stockCode,String date){
        KiwoomClient kiwoomClient = getClient();
        final ApiRequest request = kiwoomClient.getRequest();
        request.sendMessage(makeCodeParam("KWTR0001","OPT10086"),makeDataParam(stockCode,date,"1"));
    }

    public void sendOrder(String itemCode , String depositNum , String orderType,
                          int orderCount , int orderPrice , String hoga , String preOrderNum ){
        KiwoomClient kiwoomClient = getClient();
        final ApiRequest request = kiwoomClient.getRequest();
        String callbackId = UUID.randomUUID().toString();
        KiwoomApiLock.getInstance().putCallbackId(callbackId);

        ///<param name="arg1">사용자구분 : 주식주문</param>
        ///<param name="arg3">계좌번호 : 계좌번호10자리</param>
        ///<param name="arg4">주문유형 : 1:신규매수, 2:신규매도 3:매수취소, 4:매도취소, 5:매수정정, 6:매도정정</param>
        ///<param name="arg5">종목코드 : 종목코드</param>
        ///<param name="arg6">주문수량 : 주문수량</param>
        ///<param name="arg7">주문가격 : 주문가격</param>
        ///<param name="arg8">호가구분 : 00:지정가,03:시장가,05:조건부지정가,06:최유리지정가,07:최우선지정가,10:지정가IOC,13:시장가IOC,16:최유리IOC,20:지정가FOK,23:시장가FOK,26:최유리FOK,61:장전시간외종가,62:시간외단일가매매,81:장후시간외종가</param>
        ///<param name="arg9">원주문번호 : 신규주문에는 공백, 정정(취소)주문할 원주문번호를 입력합니다.</param>

//        request.sendMessage(makeCodeParam("KWTRD001")
//                ,makeDataParam(itemCode,));

    }
    int callbackNumber = 0 ;
    public synchronized String getDateCreditData(String stockCode , String date){
        KiwoomClient kiwoomClient = getClient();
        ApiRequest request = kiwoomClient.getRequest();
        String callbackId = (callbackNumber++) + "";
        KiwoomApiLock.getInstance().putCallbackId(callbackId);

        request.sendMessage(makeCodeParam("KWTR0001","OPT10013",callbackId)
                ,makeDataParam(stockCode,date,"1"));

        int tryCount = 0;
        int MAX_TRY_COUNT = 300; // 3초
        while(true){
            try {
                Thread.sleep(10L);
                String callbackData = KiwoomApiLock.getInstance().getCallbackData(callbackId);
                if(callbackData != null){
                    return callbackData;
                }
            } catch (InterruptedException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }

            // 3초 초과시 키움API 종료후 재시도.
            if(++tryCount > MAX_TRY_COUNT) {

                kiwoomClient = getClient();
                request = kiwoomClient.getRequest();

                request.sendMessage(makeCodeParam("KWTR0001","OPT10013",callbackId)
                        ,makeDataParam(stockCode,date,"1"));
                tryCount = 0;
            }
        }
    }

    public void getMinuteData(String stockCode,String date){
        KiwoomClient kiwoomClient = getClient();
        final ApiRequest request = kiwoomClient.getRequest();
//        new Thread(() -> {
//            try {
//                Thread.sleep(15000l);
//            } catch (InterruptedException e) {
//            }
//        }).start();

        request.sendMessage(makeCodeParam("KWTR0001","OPT10015"),makeDataParam(stockCode,date));
    }

    private KiwoomClient getClient() {
        lock.lock();
        lock.unlock();
        while(true) {
            KiwoomClient kiwoomClient = this.client;
            if (kiwoomClient == null) {
                logger.error("kiwoomClient is null");
                KiwoomProcess.rerunKiwoomApi();
            } else {
                return kiwoomClient;
            }
        }
    }

    public String makeDataParam(String ... params){
        StringBuilder result = new StringBuilder();
        for (String param : params) {
            result.append(param).append(PARAM_DATA_SEPARATOR);
        }
        if(result.length() > 0){
            result.setLength(result.length()-1);
        }
        return result.toString();
    }

    public String makeCodeParam(String ... params){
        StringBuilder result = new StringBuilder();
        for (String param : params) {
            result.append(param).append(PARAM_CODE_SEPARATOR);
        }
        if(result.length() > 0){
            result.setLength(result.length()-1);
        }
        return result.toString();
    }

}
