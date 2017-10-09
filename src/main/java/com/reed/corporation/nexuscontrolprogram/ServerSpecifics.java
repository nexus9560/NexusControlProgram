package com.reed.corporation.nexuscontrolprogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ServerSpecifics {
    
    private boolean limited=true;
    
    private final HashSet<String> authUsers = new HashSet<>();
    
    private final HashMap<String,String> custCmds = new HashMap<>();
    
    private final HashMap<String,String> kos = new HashMap<>();
    
    private final ArrayList<String> quotes = new ArrayList<>();
    
    private String guestRoleId = null;
    
    private String muteRoleId = null;
    
    private String muteChannel = null;
    
    private boolean welcoming = false;
    
    private String welcomeMess = null;
    
    private String welcChanID = null;
    
    private String botLog = null;
    
    private HashSet<String> authRoles = null;
    

    ServerSpecifics(){
        //authUsers.add("103730295533993984");
    }
    
    public void setBotLog(String s){
        botLog = s.replaceAll("[<@#:>*]", "");
    }
    
    public void setWelcChanID(String s){
        welcChanID = s.replaceAll("[<:@#>*]", "");;
    }
    
    public void setWelcoming(boolean b){
        welcoming=b;
    }
    
    public void setWelcomeMessage(String s){
        welcomeMess=s;
    }
    
    public void setWelcomeMessage(String[] s){
        if(welcomeMess==null)
            welcomeMess="";
        for(int x=2;x<s.length;x++)
            welcomeMess+=s[x]+" ";
        welcomeMess = welcomeMess.replaceAll("null", "");
    }
    
    public void setMuteChannel(String s){
        muteChannel = s.replaceAll("[<:#@>*]", "");;
    }
    
    public void setMuteRoleId(String s){
        muteRoleId = s.replaceAll("[<@#:>*]", "");;
    }
    
    public void setGuestRoleId(String s){
        guestRoleId = s.replaceAll("[<@#:>*]", "");;
    }
    
    public void setLimitedStatus(boolean b){
        limited = b;
    }
    
    public void setAuthUsers(String[] c){
        for(String s:c)
            authUsers.add(s);
    }
    
    public void setCustCmds(String[] c){
        for(String s:c)
            custCmds.put(s.split(",,")[0], s.split(",,")[1]);
    }
    
    public void setKOSList(String[] c){
        for(String s:c)
            kos.put(s.split(",,")[0], s.split(",,")[1]);
    }
    
    public void setQuotes(String[] c){
        for(String s:c)
            quotes.add(s);
    }
    
    public void setSettings(String[] c){
        limited = c[0].contains("1");
        if(c.length>1)
            guestRoleId = c[1].split(":")[1];
        if(c.length>2)
            muteRoleId = c[2].split(":")[1];
        if(c.length>3)
            muteChannel = c[3].split(":")[1];
        if(c.length>4)
            welcoming = Boolean.parseBoolean(c[4].split(":")[1]);
        if(c.length>5)
            welcomeMess = c[5].split(":")[1];
        if(c.length>6)
            welcChanID = c[6].split(":")[1];
        if(c.length>7)
            botLog = c[7].split(":")[1];
    }
    
    public boolean addAuthUser(String u){
        authUsers.add(u.trim());
        Iterator<String> i = authUsers.iterator();
        while(i.hasNext()){
            String t = i.next();
            //System.out.*(t);
            if(t.length()!=18)
                authUsers.remove(t);
        }
        return true;
    }
    
    public boolean addCustCmd(String c, String r){
        return custCmds.put(c, r)!=null;
    }
    
    public boolean addKOS(String u, String r){
        if(!kos.containsKey(u))
            kos.put(u, r);
        else{
            String w = kos.get(u);
            w +="¶"+r;
            kos.put(u, w);
        }
        return true;
    }
    
    public boolean addQuote(String u){
        return quotes.add(u);
    }
    
    public void deleteQuote(int i){
        quotes.remove(i);
    }
    
    public void removeKOS(String s){
        if(kos.containsKey(s))
            kos.remove(s);
    }
    
    public void deleteCmd(String s){
        custCmds.remove(s);
    }
    
    public void removeAuth(String s){
        authUsers.remove(s);
    }
    
    public boolean confirmAuthorized(String u){
        return authUsers.contains(u);
    }
    
    public String getBotLog(){return botLog;}
    
    public String getWelcChanID(){return welcChanID;}
    
    public boolean getWelcoming(){return welcoming;}
    
    public String getWelcomeMessage(){return welcomeMess;}
    
    public String getMuteChannelId(){return muteChannel;}
    
    public String getMuteRoleId(){return muteRoleId;}
    
    public String getGuessPassId(){return guestRoleId;}
    
    public boolean getLimitedStatus(){return limited;}
    
    public String getKOS(String s){
        String ret = "";
        if(!kos.containsKey(s))
            ret = "No, user is not marked KOS";
        else{
            ret = "Yes:\n";
            String w = kos.get(s).replaceAll("¶", "\n");
            String i = "";
            for(String t:w.split("\n"))
                i+="--"+t+"\n";
            ret +=i;
        }
        return ret;
    }
    
    public String chkCmd(String s){return custCmds.get(s);}
    
    public String getLastQuote(){return quotes.get(quotes.size()-1);}
    
    public String getQuote(int i){return (quotes.get(i)!=null?quotes.get(i):null);}
    
    public HashSet<String> getAuthUsers(){return authUsers;}
    
    public HashMap<String,String> getCustCmds(){return custCmds;}
    
    public HashMap<String,String> getKOSList(){return kos;}
    
    public ArrayList<String> getQuotes(){return quotes;}
    
    public String getSettings(){
        String ret="";
        ret+="limited:"+(limited?1:0)+"\n";
        ret+="GPID   :"+guestRoleId+"\n";
        return ret;
    }
    
    public String[] dumpAll(){
        String[] ret=new String[5];
        for(int x=0;x<ret.length;x++)
            ret[x]="";
        Iterator<String> i = authUsers.iterator();
        while(i.hasNext())
            ret[0]+=i.next()+"\n";
        
        i = custCmds.keySet().iterator();
        while(i.hasNext()){
            String t = i.next();
            ret[1]+=t+",,"+custCmds.get(t)+"\n";
        }
        i = kos.keySet().iterator();
        while(i.hasNext()){
            String t = i.next();
            ret[2]+=t+",,"+kos.get(t)+"\n";
        }
        for(String s:quotes)
            ret[3]+=s+"\n";
        
        ret[4]+="limited:"+(limited?1:0)+"\n";
        ret[4]+="guestID:"+guestRoleId+"\n";
        ret[4]+="muteRID:"+muteRoleId+"\n";
        ret[4]+="muteCID:"+muteChannel+"\n";
        ret[4]+="welcomeStatus:"+(welcoming?1:0)+"\n";
        ret[4]+="welcomeMess:"+welcomeMess+"\n";
        ret[4]+="welcChanID:"+welcChanID+"\n";
        ret[4]+="botLogChan:"+botLog+"\n";
        
        
        return ret;
    }
    
    public String toString(){
        String ret = "\n";
        
        ret +="\tAuthorized Users   : "+authUsers.toString()+"\n";
        ret +="\tCustom Commands    : "+custCmds.keySet().size()+"\n";
        ret +="\tKill On Sight tgt  : "+kos.keySet().size()+"\n";
        ret +="\tQuotes             : "+quotes.size()+"\n";
        ret +="\tLimited Status     : "+limited+"\n";
        ret +="\tGuest Pass Role    : "+guestRoleId+"\n";
        ret +="\tMute Role ID       : "+muteRoleId+"\n";
        ret +="\tMute Channel ID    : "+muteChannel+"\n";
        ret +="\tWelcome mess on/off: "+welcoming+"\n";
        ret +="\tWelcome message    : "+welcomeMess+"\n";
        ret +="\twelcChanID         : "+welcChanID+"\n";
        ret +="\tBot Log Channel    : "+botLog+"\n";
        
        return ret;
    }
}
