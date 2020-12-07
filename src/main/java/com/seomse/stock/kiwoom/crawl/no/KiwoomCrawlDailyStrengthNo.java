package com.seomse.stock.kiwoom.crawl.no;

import com.seomse.jdbc.annotation.PrimaryKey;
import com.seomse.jdbc.annotation.Table;

/**
 * <pre>
 *  파 일 명 : KiwoomCrawlDailyPriceNo.java
 *  설    명 :
 *
 *  작 성 자 : yhheo(허영회)
 *  작 성 일 : 2020.07
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author
 */

@Table(name="T_STOCK_ITEM_DAILY")
public class KiwoomCrawlDailyStrengthNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    @PrimaryKey(seq = 2)
    private String YMD;

    private Double STRENGTH_RT;

    public String getITEM_CD() {
        return ITEM_CD;
    }

    public void setITEM_CD(String ITEM_CD) {
        this.ITEM_CD = ITEM_CD;
    }

    public String getYMD() {
        return YMD;
    }

    public void setYMD(String YMD) {
        this.YMD = YMD;
    }

    public Double getSTRENGTH_RT() {
        return STRENGTH_RT;
    }

    public void setSTRENGTH_RT(Double STRENGTH_RT) {
        this.STRENGTH_RT = STRENGTH_RT;
    }
}
