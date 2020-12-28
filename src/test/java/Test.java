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

import com.seomse.commons.file.FileUtil;
import com.seomse.stock.kiwoom.KiwoomApiStart;
import com.seomse.stock.kiwoom.account.KiwoomAccount;
import com.seomse.stock.kiwoom.account.KiwoomItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String [] args){
        KiwoomApiStart kiwoomApiStart = new KiwoomApiStart(33333,33334);
        kiwoomApiStart.start();


        System.out.println("계좌번호:"+ KiwoomAccount.getInstance().getAccountNumber());
        System.out.println("예수금:"+KiwoomAccount.getInstance().getReadyPrice());
        System.out.println("평가액:"+KiwoomAccount.getInstance().getValuePrice());

        Map<String, KiwoomItem> codeKiwoomItemMap = KiwoomAccount.getInstance().getCodeKiwoomItemMap();
        for (String s : codeKiwoomItemMap.keySet()) {
            KiwoomItem kiwoomItem = codeKiwoomItemMap.get(s);
            System.out.println("####################################");
            System.out.println("\t종목코드:"+kiwoomItem.getItemCode());
            System.out.println("\t종목명:"+kiwoomItem.getItemName());
            System.out.println("\t종목수량:"+kiwoomItem.getItemCount());
            System.out.println("\t평단가:"+kiwoomItem.getAvgPrice());
            System.out.println("\t평가액:"+kiwoomItem.getValuePriceTotal());
            System.out.println("\t손익액:"+kiwoomItem.getProfitLossPriceTotal());
        }

//        System.out.println();
    }
}