package com.reed.corporation.nexuscontrolprogram;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
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
    
    private final HashSet<String> authRoles = new HashSet<>();
    
    private final HashSet<String> ignoredUsers = new HashSet<>();
    
    private final MuteManager muteMan = new MuteManager();
    
    private String guestRoleId = null;
    
    private String muteRoleId = null;
    
    private String muteChannel = null;
    
    private boolean welcoming = false;
    
    private String welcomeMess = null;
    
    private String welcChanID = null;
    
    private String botLog = null;
    
    ServerSpecifics(){
        //authUsers.add("103730295533993984");
    }
    
    public void autoUnMute(){
        muteMan.autoUnmute();
    }
    
    public boolean isIgnored(String s){return ignoredUsers.contains(s);}
    
    public void setIgnoredUsers(String[] s){
        for(String q:s)
            ignoredUsers.add(q.replaceAll("[<:@#>*]",""));
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
        welcomeMess="";
        for(int x=2;x<s.length;x++)
            welcomeMess+=s[x]+" ";
        welcomeMess = welcomeMess.replaceAll("null", "");
    }
    
    public void setMuteChannel(String s){
        muteChannel = s.replaceAll("[<:#@>*]", "");
    }
    
    public void setMuteRoleId(String s){
        muteRoleId = s.replaceAll("[<@#:>*]", "");
    }
    
    public void setGuestRoleId(String s){
        guestRoleId = s.replaceAll("[<@#:>*]", "");
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
            custCmds.put(s.split(",,")[0].toLowerCase(), s.split(",,")[1]);
    }
    
    public void setKOSList(String[] c){
        for(String s:c)
            kos.put(s.split(",,")[0], s.split(",,")[1]);
    }
    
    public void setQuotes(String[] c){
        for(String s:c)
            quotes.add(s);
    }
    
    public void setAuthRoles(String[] c){
        for(String s:c){
            authRoles.add(s.replaceAll("[<@#$&!*]",""));
        }
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
            welcoming = c[4].contains("1");
        if(c.length>5)
            welcomeMess = c[5].split(":")[1];
        if(c.length>6)
            welcChanID = c[6].split(":")[1];
        if(c.length>7)
            botLog = c[7].split(":")[1];
    }
    
    public void mutePerson(String id){
        muteMan.addMute(id, System.currentTimeMillis());
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
    
    public boolean addIgnoredUser(String u){
        ignoredUsers.add(u.trim());
        Iterator<String> i = ignoredUsers.iterator();
        while(i.hasNext()){
            String t = i.next();
            //System.out.*(t);
            if(t.length()!=18)
                ignoredUsers.remove(t);
        }
        return true;
    }
    
    public boolean addCustCmd(String c, String r){
        return custCmds.put(c.toLowerCase(), r)!=null;
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
    
    public void removeIgnored(String s){
        ignoredUsers.remove(s);
    }
    
    public void removeAuthRole(String s){
        authRoles.remove(s);
    }
    
    public void unmutePerson(String id){
        muteMan.removeMute(id);
    }
    
    public String runAutoUnmute(){return muteMan.autoUnmute();}
    
    public boolean confirmAuthorized(String u){
        return authUsers.contains(u)||authRoles.contains(u);
    }
    
    public boolean addIgnoredUser(String[] s){
        HashSet<String> temp = new HashSet<>();
        for(String q:s)
            temp.add(q.replaceAll("[<@#:>*]",""));
        return ignoredUsers.addAll(temp);
    }
    
    public boolean addAuthRole(String u){
        return authRoles.add(u.replaceAll("[<@#:>*]",""));
    }
    
    public void checkMuted(){
        
    }
    
    public String getBotLog(){return botLog;}
    
    public String getWelcChanID(){return welcChanID;}
    
    public boolean getWelcoming(){return welcoming;}
    
    public String getWelcomeMessage(){return welcomeMess;}
    
    public String getMuteChannelId(){return muteChannel;}
    
    public String getMuteRoleId(){return muteRoleId;}
    
    public String getGuessPassId(){return guestRoleId;}
    
    public boolean getLimitedStatus(){return limited;}
    
    public String getMuted(){return muteMan.toString();}
    
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
    
    public HashSet<String> getAuthRoles(){return authRoles;}
    
    public HashMap<String,String> getCustCmds(){return custCmds;}
    
    public HashMap<String,String> getKOSList(){return kos;}
    
    public ArrayList<String> getQuotes(){return quotes;}
    
    public HashSet<String> getIgnoredUsers(){return ignoredUsers;}
    
    public String getSettings(){
        String ret="";
        ret+="limited:"+(limited?1:0)+"\n";
        ret+="GPID   :"+guestRoleId+"\n";
        return ret;
    }
    
    public boolean compareRoles(Message m){
        User u = m.getAuthor();
        for(Role r:u.getRoles(m.getChannelReceiver().getServer()))
            if(authRoles.contains(r.getId()))
                return true;
        return false;
    }
    
    public String[] dumpAll(){
        String[] ret=new String[7];
        for(int x=0;x<ret.length;x++)
            ret[x]="";
        Iterator<String> i = authUsers.iterator();
        while(i.hasNext())
            ret[0]+=i.next()+"\n";
        
        i = ignoredUsers.iterator();
        while(i.hasNext())
            ret[1]+=i.next()+"\n";
        
        i = authRoles.iterator();
        while(i.hasNext())
            ret[2]+=i.next()+"\n";
        
        
        i = custCmds.keySet().iterator();
        while(i.hasNext()){
            String t = i.next();
            ret[3]+=t+",,"+custCmds.get(t)+"\n";
        }
        i = kos.keySet().iterator();
        while(i.hasNext()){
            String t = i.next();
            ret[4]+=t+",,"+kos.get(t)+"\n";
        }
        for(String s:quotes)
            ret[5]+=s+"\n";
        
        ret[6]+="limited:"+(limited?1:0)+"\n";
        ret[6]+="guestID:"+guestRoleId+"\n";
        ret[6]+="muteRID:"+muteRoleId+"\n";
        ret[6]+="muteCID:"+muteChannel+"\n";
        ret[6]+="welcomeStatus:"+(welcoming?1:0)+"\n";
        ret[6]+="welcomeMess:"+welcomeMess+"\n";
        ret[6]+="welcChanID:"+welcChanID+"\n";
        ret[6]+="botLogChan:"+botLog+"\n";
        
        
        return ret;
    }
    
    public String toString(){
        String ret = "\n";
        
        ret +="\tAuthorized Users   : "+authUsers.toString()+"\n";
        ret +="\tIgnored Users      : "+ignoredUsers.toString()+"\n";
        ret +="\tAuthorized Roles   : "+authRoles.toString()+"\n";
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
