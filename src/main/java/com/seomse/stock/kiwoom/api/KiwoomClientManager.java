package com.seomse.stock.kiwoom.api;

import com.seomse.api.ApiRequest;
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
    private KiwoomClientManager(){
        clientListCheckStart();
    }
    public static KiwoomClientManager getInstance(){return SingleTonHolder.INSTANCE;}
    private Map<String,KiwoomClient> kiwoomClientMap = new HashMap<>();

    private static final String PARAM_CODE_SEPARATOR = ",";
    private static final String PARAM_DATA_SEPARATOR = "|";

    private static final Long CLIENT_CHECK_DELAY = 3000l;

    boolean alreadyCheckStarted = false;
    public void clientListCheckStart(){
        if(alreadyCheckStarted){
            return;
        }
        new Thread(() -> {
            while(true){
                try { Thread.sleep(CLIENT_CHECK_DELAY);} catch (InterruptedException e) { }

                List<KiwoomClient> kiwoomClientList = new ArrayList<>(kiwoomClientMap.values());
                logger.debug("CHECK CLIENT.. ("+kiwoomClientMap.size()+")");
                List<String> removeList = new ArrayList<>();
                for (KiwoomClient client : kiwoomClientList) {
                    try {
                        client.ping();
                    } catch(Exception e){
                        removeList.add(client.getId());
                    }
                }
                for (String clientId : removeList) {
                    removeClient(clientId);
                }
            }
        }).start();
        alreadyCheckStarted = true;
    }

    public void addClient(String clientId, ApiRequest request){
        lock.lock();
        KiwoomClient client = new KiwoomClient(clientId , request);
        kiwoomClientMap.put(clientId , client);
        lock.unlock();
    }

    public void removeClient(String clientId){
        lock.lock();
        kiwoomClientMap.remove(clientId);
        lock.unlock();
    }

    public void getDatePriceData(String stockCode,String date){
        KiwoomClient kiwoomClient = getClient();
        final ApiRequest request = kiwoomClient.getRequest();
        request.sendMessage(makeCodeParam("KWTR0001","OPT10086"),makeDataParam(stockCode,date,"1"));
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
        KiwoomClient kiwoomClient = null;
        int clientSize = kiwoomClientMap.keySet().size();
        int randClient = new Random().nextInt(clientSize);
        List<String> clientIdList = new LinkedList<>( kiwoomClientMap.keySet());
        // 랜덤으로 교체 예정
        for (int i=0; i<clientSize;i++) {
            String clientId  = clientIdList.get(i);
            kiwoomClient = kiwoomClientMap.get(clientId);
            if(kiwoomClient.isAlive()) {
                break;
            } else {
                continue;
            }
        }
        lock.unlock();
        return kiwoomClient;
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
