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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class KiwoomApiLock {
    private static class SingleTonHolder{ private static final KiwoomApiLock INSTANCE = new KiwoomApiLock();}
    private KiwoomApiLock(){}
    public static KiwoomApiLock getInstance(){return SingleTonHolder.INSTANCE;}
    private ReentrantLock lock = new ReentrantLock();
    private Map<String,String> callbackMap = new HashMap<>();

    /**
     * put callback ID to map
     * @param callbackId
     */
    public void putCallbackId(String callbackId){
        lock.lock();
        callbackMap.put(callbackId,null);
        lock.unlock();
    }

    public void putCallbackData(String callbackId , String data){
        lock.lock();
        callbackMap.put(callbackId,data);
        lock.unlock();
    }

    public String getCallbackData(String callbackId){
        lock.lock();
        String data = callbackMap.get(callbackId);
        if(data != null){
            callbackMap.remove(callbackId);
        }
        lock.unlock();
        return data;
    }


}
