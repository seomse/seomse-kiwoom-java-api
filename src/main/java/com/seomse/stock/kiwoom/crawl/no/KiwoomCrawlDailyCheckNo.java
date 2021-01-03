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
