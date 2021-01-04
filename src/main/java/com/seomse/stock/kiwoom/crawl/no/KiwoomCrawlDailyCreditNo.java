/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.seomse.stock.kiwoom.crawl.no;

import com.seomse.jdbc.annotation.PrimaryKey;
import com.seomse.jdbc.annotation.Table;

@Table(name="T_STOCK_ITEM_DAILY")
public class KiwoomCrawlDailyCreditNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    @PrimaryKey(seq = 2)
    private String YMD;
    private Long CREDIT_TOTAL_VOL;
    private Long CREDIT_NEW_VOL;
    private Long CREDIT_REPAY_VOL;
    private Long CREDIT_BALANCE_VOL;
    private Long CREDIT_PRC_VOL;
    private Long CREDIT_CHANGE_VOL;
    private Double CREDIT_EXPOSURE_RT;
    private Double CREDIT_BALANCE_RT;

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

    public Long getCREDIT_TOTAL_VOL() {
        return CREDIT_TOTAL_VOL;
    }

    public void setCREDIT_TOTAL_VOL(Long CREDIT_TOTAL_VOL) {
        this.CREDIT_TOTAL_VOL = CREDIT_TOTAL_VOL;
    }

    public Long getCREDIT_NEW_VOL() {
        return CREDIT_NEW_VOL;
    }

    public void setCREDIT_NEW_VOL(Long CREDIT_NEW_VOL) {
        this.CREDIT_NEW_VOL = CREDIT_NEW_VOL;
    }

    public Long getCREDIT_REPAY_VOL() {
        return CREDIT_REPAY_VOL;
    }

    public void setCREDIT_REPAY_VOL(Long CREDIT_REPAY_VOL) {
        this.CREDIT_REPAY_VOL = CREDIT_REPAY_VOL;
    }

    public Long getCREDIT_BALANCE_VOL() {
        return CREDIT_BALANCE_VOL;
    }

    public void setCREDIT_BALANCE_VOL(Long CREDIT_BALANCE_VOL) {
        this.CREDIT_BALANCE_VOL = CREDIT_BALANCE_VOL;
    }

    public Long getCREDIT_PRC_VOL() {
        return CREDIT_PRC_VOL;
    }

    public void setCREDIT_PRC_VOL(Long CREDIT_PRC_VOL) {
        this.CREDIT_PRC_VOL = CREDIT_PRC_VOL;
    }

    public Long getCREDIT_CHANGE_VOL() {
        return CREDIT_CHANGE_VOL;
    }

    public void setCREDIT_CHANGE_VOL(Long CREDIT_CHANGE_VOL) {
        this.CREDIT_CHANGE_VOL = CREDIT_CHANGE_VOL;
    }

    public Double getCREDIT_EXPOSURE_RT() {
        return CREDIT_EXPOSURE_RT;
    }

    public void setCREDIT_EXPOSURE_RT(Double CREDIT_EXPOSURE_RT) {
        this.CREDIT_EXPOSURE_RT = CREDIT_EXPOSURE_RT;
    }

    public Double getCREDIT_BALANCE_RT() {
        return CREDIT_BALANCE_RT;
    }

    public void setCREDIT_BALANCE_RT(Double CREDIT_BALANCE_RT) {
        this.CREDIT_BALANCE_RT = CREDIT_BALANCE_RT;
    }
}
