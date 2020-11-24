package com.seomse.stock.kiwoom.process;

import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.date.DateUtil;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <pre>
 *  파 일 명 : KiwoomProcessMonitorService.java
 *  설    명 : 키움 자동 시작을 위한 프로세스 관리
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

public class KiwoomProcessMonitorService extends Service {
    private static final Logger logger = getLogger(KiwoomProcessMonitorService.class);
    private String lastExecuteDate = "19700101";

    public KiwoomProcessMonitorService(){
    }
    @Override
    public void work() {


        String nowYmd = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyyMMdd");
        String hh = DateUtil.getDateYmd(System.currentTimeMillis(),"HH");
        int mm = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(),"mm"));
//        logger.debug("KiwoomProcessMonitorService started.. "+nowYmd+hh+mm);
        if(hh.equals("8") && !nowYmd.equals(lastExecuteDate)){
            if(mm <= 10){
                return;
            } else {
                KiwoomProcess.startVersionUp(nowYmd);
                lastExecuteDate = nowYmd;
            }
        } else {
            return;
        }
    }

    public static void main(String [] args){
//        Service service = new KiwoomProcessMonitorService();
//        service.setSleepTime(1000l);
//        service.setState(State.START);
//        service.start();

        //new KiwoomProcessMonitorService().checkProcess();
    }

}
