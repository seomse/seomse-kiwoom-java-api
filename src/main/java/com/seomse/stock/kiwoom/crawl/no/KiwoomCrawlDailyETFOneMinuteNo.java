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
import oracle.sql.NUMBER;

@Table(name="T_STOCK_ETF_1M")
public class KiwoomCrawlDailyETFOneMinuteNo {
    @PrimaryKey(seq = 1)
    private String ETF_CD;
    @PrimaryKey(seq = 2)
    private String YMDHM;
    private Integer CLOSE_PRC;
    private Integer PREVIOUS_PRC;
    private Integer OPEN_PRC;
    private Integer HIGH_PRC;
    private Integer LOW_PRC;
    private Long TRADE_VOL;

    public String getETF_CD() {
        return ETF_CD;
    }

    public void setETF_CD(String ETF_CD) {
        this.ETF_CD = ETF_CD;
    }

    public String getYMDHM() {
        return YMDHM;
    }

    public void setYMDHM(String YMDHM) {
        this.YMDHM = YMDHM;
    }

    public Integer getCLOSE_PRC() {
        return CLOSE_PRC;
    }

    public void setCLOSE_PRC(Integer CLOSE_PRC) {
        this.CLOSE_PRC = CLOSE_PRC;
    }

    public Integer getPREVIOUS_PRC() {
        return PREVIOUS_PRC;
    }

    public void setPREVIOUS_PRC(Integer PREVIOUS_PRC) {
        this.PREVIOUS_PRC = PREVIOUS_PRC;
    }

    public Integer getOPEN_PRC() {
        return OPEN_PRC;
    }

    public void setOPEN_PRC(Integer OPEN_PRC) {
        this.OPEN_PRC = OPEN_PRC;
    }

    public Integer getHIGH_PRC() {
        return HIGH_PRC;
    }

    public void setHIGH_PRC(Integer HIGH_PRC) {
        this.HIGH_PRC = HIGH_PRC;
    }

    public Integer getLOW_PRC() {
        return LOW_PRC;
    }

    public void setLOW_PRC(Integer LOW_PRC) {
        this.LOW_PRC = LOW_PRC;
    }

    public Long getTRADE_VOL() {
        return TRADE_VOL;
    }

    public void setTRADE_VOL(Long TRADE_VOL) {
        this.TRADE_VOL = TRADE_VOL;
    }
}
