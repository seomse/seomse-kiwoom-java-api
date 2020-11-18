package com.seomse.stock.kiwoom.data.no;

import com.seomse.jdbc.annotation.DateTime;
import com.seomse.jdbc.annotation.PrimaryKey;
import com.seomse.jdbc.annotation.Table;

/**
 * <pre>
 *  파 일 명 : KiwoomCrawlDailyCheckNo.java
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

@Table(name="T_CRAWLING_KIWOOM_TR")
public class KiwoomCrawlCheckNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    @PrimaryKey(seq = 2)
    private String CRAWL_TP;
    @PrimaryKey(seq = 1)
    private String TIME_CD;
    private String YMD_LAST;
    private String YMD_FIRST;
    private Long CNT_DATA;
    @DateTime
    private Long REG_DT = System.currentTimeMillis();
    @DateTime
    private Long UPT_DT = System.currentTimeMillis();

    public String getITEM_CD() {
        return ITEM_CD;
    }

    public void setITEM_CD(String ITEM_CD) {
        this.ITEM_CD = ITEM_CD;
    }

    public String getCRAWL_TP() {
        return CRAWL_TP;
    }

    public void setCRAWL_TP(String CRAWL_TP) {
        this.CRAWL_TP = CRAWL_TP;
    }

    public String getTIME_CD() {
        return TIME_CD;
    }

    public void setTIME_CD(String TIME_CD) {
        this.TIME_CD = TIME_CD;
    }

    public String getYMD_LAST() {
        return YMD_LAST;
    }

    public void setYMD_LAST(String YMD_LAST) {
        this.YMD_LAST = YMD_LAST;
    }

    public String getYMD_FIRST() {
        return YMD_FIRST;
    }

    public void setYMD_FIRST(String YMD_FIRST) {
        this.YMD_FIRST = YMD_FIRST;
    }

    public Long getCNT_DATA() {
        return CNT_DATA;
    }

    public void setCNT_DATA(Long CNT_DATA) {
        this.CNT_DATA = CNT_DATA;
    }

    public Long getREG_DT() {
        return REG_DT;
    }

    public void setREG_DT(Long REG_DT) {
        this.REG_DT = REG_DT;
    }

    public Long getUPT_DT() {
        return UPT_DT;
    }

    public void setUPT_DT(Long UPT_DT) {
        this.UPT_DT = UPT_DT;
    }
}
