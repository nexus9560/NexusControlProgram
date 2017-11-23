/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reed.corporation.nexuscontrolprogram;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Disingenuous Donuts
 */
public class MuteManager {
    
    private final HashMap<String,String> userMute = new HashMap<>();
    
    public void addMute(String id,long startMilli){
        userMute.put(id, (startMilli+(long)300000)+"");
    }
    
    public boolean checkMuteStatus(String id){
        return Long.parseLong(userMute.get(id))<System.currentTimeMillis();
    }
    
    public String autoUnmute(){
        String ret="";
        if(!userMute.isEmpty()){
            Iterator<String> i = userMute.keySet().iterator();
            while(i.hasNext()){
                String t = i.next();
                if(checkMuteStatus(t))
                    removeMute(t);
            }
            ret = "Users automatically unmuted";
        }else{
            ret = "Nobody is muted ^-^";
        }
        return ret;
    }
    
    public void removeMute(String id){
        boolean ret = Long.parseLong(userMute.get(id))<=System.currentTimeMillis();
        if(ret){
            userMute.remove(id);
        }
    }
    
    public String toString(){
        String ret="";
        Iterator<String> i = userMute.keySet().iterator();
        while(i.hasNext()){
            String t = i.next();
            ret += t+" : "+(checkMuteStatus(t)?"Due for unmuting":"Still serving penance\n");
        }
        
        return ret;
    }
    
    
}
