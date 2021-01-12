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

import com.seomse.jdbc.annotation.DateTime;
import com.seomse.jdbc.annotation.PrimaryKey;
import com.seomse.jdbc.annotation.Table;

@Table(name="T_CRAWLING_KIWOOM_TR")
public class KiwoomCrawlStatusNo {
    @PrimaryKey(seq = 1)
    private String ITEM_CD;
    @PrimaryKey(seq = 2)
    private String CRAWL_TP;
    @PrimaryKey(seq = 3)
    private String TIME_CD;
    private String YMD_LAST="19000101";
    private String YMD_FIRST="29991231";
    private Integer DATA_CNT=0;
    @DateTime
    private Long REG_DT = System.currentTimeMillis();
    @DateTime
    private Long UPT_DT = System.currentTimeMillis();

    private Integer LAST_PRC;

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

    public Integer getDATA_CNT() {
        return DATA_CNT;
    }

    public void setDATA_CNT(Integer DATA_CNT) {
        this.DATA_CNT = DATA_CNT;
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

    public Integer getLAST_PRC() {
        return LAST_PRC;
    }

    public void setLAST_PRC(Integer LAST_PRC) {
        this.LAST_PRC = LAST_PRC;
    }
}
