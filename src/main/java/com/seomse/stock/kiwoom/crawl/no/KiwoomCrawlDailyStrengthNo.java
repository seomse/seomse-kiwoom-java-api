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
