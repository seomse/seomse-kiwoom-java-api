package com.seomse.stock.kiwoom.data.no;

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
public class KiwoomCrawlDailyPriceNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    @PrimaryKey(seq = 2)
    private String YMD;
    private Double LAST_PRC;
    private Double PREVIOUS_PRC;
    private Double START_PRC;
    private Double MAX_PRC;
    private Double MIN_PRC;
    private Double TRADE_VOL;
    private Double STRENGTH_RT;
    private Double INSTITUTION_TRADE_VOL;
    private Double FOREIGN_TRADE_VOL;
    private Double FOREIGNER_TRADE_VOL;
    private Double INDIVIDUAL_TRADE_VOL;
    private Double PROGRAM_TRADE_VOL;
    private Double SLB_VOL;
    private Double SLB_REPAY_VOL;
    private Double SLB_BALANCE_VOL;
    private Double SHORT_SELLING_VOL;
    private Double SHORT_SELLING_BALANCE_VOL;
    private Double CREDIT_RT;

    /**
     * 종목코드
     */
    public String getITEM_CD() {
        return ITEM_CD;
    }
    /**
     * 종목코드
     */
    public void setITEM_CD(String ITEM_CD) {
        this.ITEM_CD = ITEM_CD;
    }
    /**
     * 년월일
     */
    public String getYMD() {
        return YMD;
    }
    /**
     * 년월일
     */
    public void setYMD(String YMD) {
        this.YMD = YMD;
    }
    /**
     * 최종가격
     */
    public Double getLAST_PRC() {
        return LAST_PRC;
    }
    /**
     * 최종가격
     */
    public void setLAST_PRC(Double LAST_PRC) {
        this.LAST_PRC = LAST_PRC;
    }
    /**
     * 전거래일가격
     */
    public Double getPREVIOUS_PRC() {
        return PREVIOUS_PRC;
    }
    /**
     * 전거래일가격
     */
    public void setPREVIOUS_PRC(Double PREVIOUS_PRC) {
        this.PREVIOUS_PRC = PREVIOUS_PRC;
    }
    /**
     * 시가
     */
    public Double getSTART_PRC() {
        return START_PRC;
    }
    /**
     * 시가
     */
    public void setSTART_PRC(Double START_PRC) {
        this.START_PRC = START_PRC;
    }
    /**
     * 고가
     */
    public Double getMAX_PRC() {
        return MAX_PRC;
    }
    /**
     * 고가
     */
    public void setMAX_PRC(Double MAX_PRC) {
        this.MAX_PRC = MAX_PRC;
    }
    /**
     * 저가
     */
    public Double getMIN_PRC() {
        return MIN_PRC;
    }
    /**
     * 저가
     */
    public void setMIN_PRC(Double MIN_PRC) {
        this.MIN_PRC = MIN_PRC;
    }
    /**
     * 거래량
     */
    public Double getTRADE_VOL() {
        return TRADE_VOL;
    }
    /**
     * 거래량
     */
    public void setTRADE_VOL(Double TRADE_VOL) {
        this.TRADE_VOL = TRADE_VOL;
    }
    /**
     * 체결강도
     */
    public Double getSTRENGTH_RT() {
        return STRENGTH_RT;
    }
    /**
     * 체결강도
     */
    public void setSTRENGTH_RT(Double STRENGTH_RT) {
        this.STRENGTH_RT = STRENGTH_RT;
    }
    /**
     * 기관매매량
     */
    public Double getINSTITUTION_TRADE_VOL() {
        return INSTITUTION_TRADE_VOL;
    }
    /**
     * 기관매매량
     */
    public void setINSTITUTION_TRADE_VOL(Double INSTITUTION_TRADE_VOL) {
        this.INSTITUTION_TRADE_VOL = INSTITUTION_TRADE_VOL;
    }
    /**
     * 외국계매매량
     */
    public Double getFOREIGN_TRADE_VOL() {
        return FOREIGN_TRADE_VOL;
    }
    /**
     * 외국계매매량
     */
    public void setFOREIGN_TRADE_VOL(Double FOREIGN_TRADE_VOL) {
        this.FOREIGN_TRADE_VOL = FOREIGN_TRADE_VOL;
    }
    /**
     * 외국인매매량
     */
    public Double getFOREIGNER_TRADE_VOL() {
        return FOREIGNER_TRADE_VOL;
    }
    /**
     * 외국인매매량
     */
    public void setFOREIGNER_TRADE_VOL(Double FOREIGNER_TRADE_VOL) {
        this.FOREIGNER_TRADE_VOL = FOREIGNER_TRADE_VOL;
    }
    /**
     * 개인매매동향
     */
    public Double getINDIVIDUAL_TRADE_VOL() {
        return INDIVIDUAL_TRADE_VOL;
    }
    /**
     * 개인매매동향
     */
    public void setINDIVIDUAL_TRADE_VOL(Double INDIVIDUAL_TRADE_VOL) {
        this.INDIVIDUAL_TRADE_VOL = INDIVIDUAL_TRADE_VOL;
    }
    /**
     * 프로그램매매동향
     */
    public Double getPROGRAM_TRADE_VOL() {
        return PROGRAM_TRADE_VOL;
    }
    /**
     * 프로그램매매동향
     */
    public void setPROGRAM_TRADE_VOL(Double PROGRAM_TRADE_VOL) {
        this.PROGRAM_TRADE_VOL = PROGRAM_TRADE_VOL;
    }
    /**
     * 대차체결량
     */
    public Double getSLB_VOL() {
        return SLB_VOL;
    }
    /**
     * 대차체결량
     */
    public void setSLB_VOL(Double SLB_VOL) {
        this.SLB_VOL = SLB_VOL;
    }
    /**
     * 대차상환량
     */
    public Double getSLB_REPAY_VOL() {
        return SLB_REPAY_VOL;
    }
    /**
     * 대차상환량
     */
    public void setSLB_REPAY_VOL(Double SLB_REPAY_VOL) {
        this.SLB_REPAY_VOL = SLB_REPAY_VOL;
    }
    /**
     * 대자잔고수량
     */
    public Double getSLB_BALANCE_VOL() {
        return SLB_BALANCE_VOL;
    }
    /**
     * 대자잔고수량
     */
    public void setSLB_BALANCE_VOL(Double SLB_BALANCE_VOL) {
        this.SLB_BALANCE_VOL = SLB_BALANCE_VOL;
    }
    /**
     * 공매도거래량
     */
    public Double getSHORT_SELLING_VOL() {
        return SHORT_SELLING_VOL;
    }
    /**
     * 공매도거래량
     */
    public void setSHORT_SELLING_VOL(Double SHORT_SELLING_VOL) {
        this.SHORT_SELLING_VOL = SHORT_SELLING_VOL;
    }
    /**
     * 공매도잔고수량
     */
    public Double getSHORT_SELLING_BALANCE_VOL() {
        return SHORT_SELLING_BALANCE_VOL;
    }
    /**
     * 공매도잔고수량
     */
    public void setSHORT_SELLING_BALANCE_VOL(Double SHORT_SELLING_BALANCE_VOL) {
        this.SHORT_SELLING_BALANCE_VOL = SHORT_SELLING_BALANCE_VOL;
    }
    /**
     * 신용거래율
     */
    public Double getCREDIT_RT() {
        return CREDIT_RT;
    }
    /**
     * 신용거래율
     */
    public void setCREDIT_RT(Double CREDIT_RT) {
        this.CREDIT_RT = CREDIT_RT;
    }

}
