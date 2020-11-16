package com.seomse.stock.kiwoom.process;

import com.seomse.commons.service.Service;
import com.seomse.commons.utils.date.DateUtil;
import org.slf4j.Logger;

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
    @Override
    public void work() {
        String nowYmd = DateUtil.getDateYmd(System.currentTimeMillis(),"yyyyMMdd");
        String hh = DateUtil.getDateYmd(System.currentTimeMillis(),"HH");
        int mm = Integer.parseInt(DateUtil.getDateYmd(System.currentTimeMillis(),"mm"));
//        logger.debug("KiwoomProcessMonitorService started.. "+nowYmd+hh+mm);
        if(hh.equals("16") && !nowYmd.equals(lastExecuteDate)){
            if(mm <= 10){
                return;
            } else {
                startVersionUp(nowYmd);
            }
        } else {
            return;
        }
    }
    public void startVersionUp(String nowYmd){
        logger.info("startVersionUp process.. ["+nowYmd+"] ");
        Runtime runtime = Runtime.getRuntime();

        try {Process versionUpprocess = runtime.exec("C:\\OpenAPI\\opversionup.exe");} catch (IOException e) {}
        try {Thread.sleep(1000l * 60l);} catch (InterruptedException e) {}

        try {
            final Process process = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh").start();
            new Thread(() -> {
                Scanner sc = new Scanner(process.getInputStream());
                if (sc.hasNextLine()) sc.nextLine();
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    String[] parts = line.split(",");
                    String unq = parts[0].substring(1).replaceFirst(".$", "");
                    if(unq.equals("opversionup.exe")){
                        if(process.isAlive()){
                            logger.info("process kill opversionup.exe ["+nowYmd+"] ");
                            try {runtime.exec("taskkill /F /IM opversionup.exe");} catch (IOException e) {}
                        }
                    }
                }
            }).start();
            process.waitFor();
        }
        catch (IOException e) {}
        catch (InterruptedException e) {}
        lastExecuteDate = nowYmd;
        System.out.println("Done");
    }
    public static void main(String [] args){
        Service service = new KiwoomProcessMonitorService();
        service.setSleepTime(1000l);
        service.setState(State.START);
        service.start();
    }

}
