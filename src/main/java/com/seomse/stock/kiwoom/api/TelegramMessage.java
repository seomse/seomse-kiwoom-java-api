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
package com.seomse.stock.kiwoom.api;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.stock.kiwoom.config.KiwoomConfig;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

public class TelegramMessage {
    
    private static final Logger logger = getLogger(TelegramMessage.class);

    private static String token = "1405493802:AAGNODKnzSyRHiN72IzaUx0GYP4UrLxmemo";
    private static String chatId = "1417951085";
    static {
        token = KiwoomConfig.getConfig(KiwoomConfig.TELEGRAM_TOKEN);
        chatId = KiwoomConfig.getConfig(KiwoomConfig.TELEGRAM_CHAT_ID);
    }

    /**
     * Telegram message to me
     * @param text text
     * @return
     */
    public static String toMe(String text){
        return toMe(token , chatId , text);
    }

    /**
     * Telegram message to me
     * @param token token
     * @param chatId chatId
     * @param text text
     * @return
     */
    public static String toMe(String token , String chatId , String text){

        BufferedReader in = null;
        StringBuilder response = new StringBuilder();
        try {
            String utfText = URLEncoder.encode(text,"UTF-8");
            URL obj = new URL("https://api.telegram.org/bot" + token + "/sendmessage?chat_id=" + chatId + "&parse_mode=markdown&text=" + utfText); // 호출할 url

            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line;


            while((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }

        } catch(Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        } finally {
            if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); }
            return response.toString();
        }
    }

    public static String toStockMessageToMe(String itemName , int price , int volume , String depositNum , int depositVolume , boolean isSell){
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.KOREA);
        String priceStr = nf.format(price);
        String depositVolumeStr = nf.format(depositVolume);
        return toMe(
        """
                \\[%s]\n*%s*   __%s__원   %s__%s__주\n계좌잔액(%s) : \\[__%s__]원
            """.formatted( isSell?"매도":"매수", itemName,priceStr,  isSell?"-":"+" , volume+"",depositNum,depositVolumeStr)
        );
    }


    public static void main(String [] args){
        toStockMessageToMe("삼성전자",1230300,100,"101-32-13131",823981293,true);
    }
}
