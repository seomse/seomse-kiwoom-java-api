package com.seomse.stock.kiwoom.api;

import com.seomse.api.ApiRequest;

/**
 * <pre>
 *  파 일 명 : KiwoomClient.java
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

public class KiwoomClient {
    private String id ;
    private ApiRequest request;
    public KiwoomClient(String id , ApiRequest request){
        this.id = id;
        this.request = request;
    }

    public void ping() throws Exception{
        try {
            request.sendToReceiveMessage("KWPING01", "PING!");
        } catch(Exception e){
            throw e;
        }
    }

    public ApiRequest getRequest() {
        return request;
    }

    public boolean isAlive(){
        return request.isConnect();
    }

    public String getId() {
        return id;
    }
}
