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

@Table(name="T_STOCK_CRWL_DAILY_KW")
public class KiwoomCrawlDailyCheckNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    private String YMD_LAST;
    private String YMD_FIRST;
    private Long CNT_DATA;
    @DateTime
    private Long DT_REG_FST = System.currentTimeMillis();

    public String getITEM_CD() {
        return ITEM_CD;
    }

    public void setITEM_CD(String ITEM_CD) {
        this.ITEM_CD = ITEM_CD;
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

    public Long getDT_REG_FST() {
        return DT_REG_FST;
    }

    public void setDT_REG_FST(Long DT_REG_FST) {
        this.DT_REG_FST = DT_REG_FST;
    }
}
