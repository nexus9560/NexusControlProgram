/*
*/
package com.reed.corporation.nexuscontrolprogram;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAttachment;
import de.btobastian.javacord.entities.message.Reaction;
import de.btobastian.javacord.entities.message.embed.Embed;
import de.btobastian.javacord.entities.permissions.Permissions;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;


/**
 * Bot id 339467323251359744
 * @author Disingenuous Donuts
 */
public class CoreProcessor {
    
    private static boolean first=false;
    
    private static CmdProcessor working = null;
    
    private static DiscordAPI god = null;
    
    public CoreProcessor(String token){
        if(god==null)
            god = Javacord.getApi(token, true);
        god.setAutoReconnect(true);
        god.connect(new FutureCallback<DiscordAPI>(){
            public void onSuccess(DiscordAPI apo){
                apo.registerListener(new MessageCreateListener(){
                    public void onMessageCreate(DiscordAPI apc, Message mess){
                        try{
                        if(!mess.getAuthor().getId().equals("339467323251359744"))
                            if(mess.getContent().substring(0,2).contains("&")){
                                if(working==null)
                                    working = new CmdProcessor(apc,mess);
                                working.process(apc, mess);
                            }else if(mess.getContent().contains("hitler did nothing wrong"))
                                mess.reply("stay woke");
                            else if(mess.getContent().contains("\\o\\"))
                                mess.reply("/o/");
                            else if(mess.getContent().contains("/o/"))
                                mess.reply("\\o\\");
                            else if(bieberDetector(mess)){
                                mess.reply("!ban "+mess.getAuthor().getMentionTag());
                            }else if(mess.getContent().contains("F")&&mess.getContent().length()==1){
                                reactionAddF(mess);
                            }
                        }catch(NullPointerException e){
                            e.printStackTrace();
                            String sentMessage = "I have crashed, here is the report: "+e.getMessage();
                            sentMessage+=      "\nIt happened on this server        : "+mess.getChannelReceiver().getServer().getName();
                            sentMessage+=      "\nIn this thread                    : "+mess.getChannelReceiver().getName();
                            sentMessage+=      "\nBecause of this User              : "+mess.getAuthor().getName();
                            sentMessage+=      "\nWas trying to execute this cmd    : "+mess.getContent();
                            god.getServerById("267380702708891648").getChannelById("339622824631205899").sendMessage("```"+sentMessage+"```");
                        }
                    }
                });
            }
            public void onFailure(Throwable t){
                //god.getServerById("267380702708891648").getChannelById("339622824631205899").sendMessage("I have crashed, here is the report:\n"+t);
            }
        });
    }
    
    private void reactionAddF(Message m){
    }
    
    private boolean bieberDetector(Message m){
        String s = m.getContent();
        return s.contains("Justin bieber")||s.contains("justin bieber")||s.contains("justin Bieber");
    }
    
}

class CmdProcessor{
    
    
    private final String myID = "103730295533993984";
    
    private final static String BOTID = "339467323251359744";
    
    private String serverID = "";
    
    private static HashMap<String,ServerSpecifics> specifics = new HashMap<>();
    
    private ServerSpecifics tisServ = null;
    
    private static DiscordAPI god = null;
    
    private Server thisServer = null;
    
    private static boolean aprldd = false;
    
    CmdProcessor(){
        if(!aprldd){
            thisServer = null;
            aprldd = true;
        }
    }
    
    CmdProcessor(DiscordAPI api,Message msg){
        if(god==null){
            expLoadData(api);
            god = api;
        }
        if(!aprldd){
            god = api;
            expLoadData(api);
            thisServer = api.getServerById(getCurrentServerID(api,msg));
            serverID = thisServer.getId();
            aprldd = true;
        }
    }
    
    /*
        $$kos add Maelstrom Dragons : Grade A Autist
    */
    
    public final void process(DiscordAPI api, Message msg){
        
        System.out.println(god.toString());
        
        if(msg.getContent().contains("&kys"))
            terminate(api,msg,true);
        
        if(tisServ==null){
            tisServ = specifics.get(api.getServerById(this.getCurrentServerID(api, msg)).getId());
        }
        String cmd = msg.getContent();
        if(cmd.contains(" "))
            cmd = cmd.substring(0, msg.getContent().indexOf(" "));
        cmd = cmd.replaceAll("&","");
        
        switch(cmd){
            case "auth"         :
            case "authorize"    :authorizeProcessor(msg);break;
            case "cmd"          :custCommands(msg);break;
            case "diag"         :
            case "diagnostics"  :diagnostics(msg);break;
            case "guestPass"    :guestPass(msg);break;
            case "kos"          :kosProcessor(msg);break;
            case "lmds"         :
            case "listMsgDtls"  :listMessageDetails(api, msg);break;
            case "lrids"        :
            case "listRoleIDs"  :listRoleDetails(msg);break;
            case "mute"         :muteAction(msg);break;
            case "quote"        :quoteProcessor(msg);break;
            case "reload"       :expLoadData(api);msg.reply("Reloading saved data...");break;
            case "save"         :expSaveData(api);msg.reply("Saving data to file...");break;
            case "sudo"         :sudoActions(api, msg);break;
            case "unmute"       :unmuteAction(msg);break;
            case "help"         :helpAction(msg);break;
            default             :custCmdProc(msg);
        }
        
    }
    
    private void unmuteAction(Message m){
        if(authorize(m.getAuthor())){
            Server s = m.getChannelReceiver().getServer();
            String[] c = m.getContent().split(" ");
            String i = s.getId();
            Role mute = s.getRoleById(specifics.get(i).getMuteRoleId());
            mute.removeUser(s.getMemberById(c[1]));
        }
    }
    
    private void muteAction(Message m){
        //&mute %USER-ID
        if(authorize(m.getAuthor())){
            Server s = m.getChannelReceiver().getServer();
            String[] c = m.getContent().split(" ");
            String i = s.getId();
            Role mute = s.getRoleById(specifics.get(i).getMuteRoleId());
            mute.addUser(s.getMemberById(c[1]));
        }
    }
    
    //Because netbeans is a bitch sometimes
    
    private void guestPass(Message m){
        Server tws = m.getChannelReceiver().getServer();
        if(m.getAuthor().getRoles(tws).isEmpty()&&tws.getRoleById(specifics.get(tws.getId()).getGuessPassId())!=null){
            Role guest = tws.getRoleById(specifics.get(tws.getId()).getGuessPassId());
            guest.addUser(m.getAuthor());
            m.getAuthor().sendMessage("You now have a guest pass for "+tws.getName());
            m.delete();
        }else{
            m.reply("I'm sorry, but this server does not have a guest pass, my condolences...");
        }
    }
    
    private void sudoActions(DiscordAPI a, Message m){
        if(authorize(m.getAuthor())){
            Server serv = m.getChannelReceiver().getServer();
            String sID = serv.getId();
            //&sudo set guestID %GUEST-ID%
            String mC = m.getContent().replaceAll("&sudo","").trim();
            //set guestID %GUEST-ID%
            String[] cmP = mC.split(" ");
            //["set", "guestID", "%GUEST-ID%"]
            if(cmP[0].equalsIgnoreCase("set")){
                switch(cmP[1]){
                    case "guestID"  :specifics.get(sID).setGuestRoleId(cmP[2]);break;
                    case "muteID"   :specifics.get(sID).setMuteRoleId(cmP[2]);break;
                    case "muteChannel":specifics.get(sID).setMuteChannel(cmP[2]);break;
                }
            }
        }else{
            m.reply("I'm sorry, "+m.getAuthor().getMentionTag()+", I can't let you do that.");
        }
    }
    
    private void listMessageDetails(DiscordAPI apo, Message m){
        String sent = "";
        sent += m.toString()+"\n";
        ArrayList<Embed> embs = new ArrayList<>(m.getEmbeds());
        for(Embed e:embs)
            sent+=e.toString()+"\n";
        ArrayList<MessageAttachment> mats = new ArrayList<>(m.getAttachments());
        for(MessageAttachment mit:mats)
            sent+=mit.toString()+"\n";
        m.getAuthor().sendMessage("```"+sent+"```");
    }
    
    private void listRoleDetails(Message m){
        String sent = "";
        ArrayList<Role> roles = new ArrayList<>(god.getServerById(this.getCurrentServerID(god, m)).getRoles());
        for(Role r:roles){
            sent+=r.toString()+"\n";
            Permissions p = r.getPermissions();
            sent+="\t"+p.toString()+"\n";
        }
        m.getAuthor().sendMessage("```"+sent+"```");
    }
    
    private void helpAction(Message m){
        try{
            Scanner scans = new Scanner(new File("help.nxs"));
            String message = "";
            while(scans.hasNextLine())
                message+=scans.nextLine()+"\n";
            m.getAuthor().sendMessage(message);
            message = "";
            scans.close();
        }catch(Exception e){}
    }
    
    //&cmd add,, lookAtThem,, yadda yadda
    private void custCommands(Message m){
        //&cmd add,, lookAtThem,, yadda yadda
        String payload = m.getContent().replaceAll("&cmd","").trim();
        //add| lookAtThem| yadda yadda
        String[] pyld = payload.split(",, ");
        //["add", "lookAtThem", "yadda yadda"]
        switch(pyld[0]){
            case "add":specifics.get(thisServer.getId()).addCustCmd(pyld[1],pyld[2]);m.reply("Command has been added!");break;
            case "remove":specifics.get(thisServer.getId()).deleteCmd(pyld[1]);m.reply("Command has been removed.");break;
        }
    }
    
    private void custCmdProc(Message m){
        String send = "";
        send = specifics.get(thisServer.getId()).chkCmd(m.getContent().replaceAll("&", ""));
        if(send==null)
            snarkyResponse(m);
        else
            m.reply(send);
    }
    
    private int greaterThanZero(String s){
        int ret = new Integer(s)-1;
        if(ret<0)
            return 0;
        else
            return ret;
    }
    
    //&quote add,, TIMES UP COMMANDUH -- Alan Reed
    private void quoteProcessor(Message m){
        String payload = m.getContent().replaceAll("&quote","").trim();
        //&quote add,, TIMES UP COMMANDUH -- Alan Reed
        String[] pyld = payload.split(",, ");
        String send = "";
        switch(pyld[0]){
            case "get"      :send = specifics.get(thisServer.getId()).getQuote(greaterThanZero(pyld[1]));break;
            case "lastQuote":
            case "getRecent":send = specifics.get(thisServer.getId()).getLastQuote();break;
            case "add"      :specifics.get(thisServer.getId()).addQuote(pyld[1]);send = "Quote has been added!";break;
            case "remove"   :specifics.get(thisServer.getId()).deleteQuote(greaterThanZero(pyld[1]));send = "Quote has been removed.";break;
            default         :send = "Please format your command like so: &quote add,, Famous line -- Famous Person\nOr, &quote get,, N";
        }
        if(send==null)
            m.reply("I'm sorry, the quote number you were referencing doesn't exist.");
        else
            m.reply(send);
    }
    
    private void diagnostics(Message m){
        String ret="";
        if(authorize(m.getAuthor())){
            String chId = m.getChannelReceiver().getId();
            String tID = thisServer.getId();
            Server working = god.getServerById(this.getCurrentServerID(god, m));
            boolean limSta = specifics.get(tID).getLimitedStatus();
            
            ret+="This Server is     : "+working.getName()+"\n";
            ret+="This Servers ID is : "+working.getId()+"\n";
            ret+="HashMap db status  : "+(specifics.keySet().isEmpty()?"Not Populated...":"Populated!")+"\n";
            if(limitedOverride(m)){
                ret+="HashMap db pop stat: "+specifics.keySet()+"\n";
                ret+="Server Lists       : "+god.getServers()+"\n";
            }
            ret+="Server Specific mgr: "+(specifics!=null?specifics.get(thisServer.getId()).toString()+"\n":"Data controller not loaded...\n");
            ret+="First run setup int: "+aprldd+"\n";
            ret+="Limited Mode       : "+specifics.get(tID).getLimitedStatus();
            //m.reply("```"+ret+"```");
            god.getServerById(this.getCurrentServerID(god, m)).getChannelById(chId).sendMessage("```"+ret+"```");
            ret="";
            if(specifics!=null&&(!limSta||limitedOverride(m))){
                Iterator<String> i = specifics.keySet().iterator();
                while(i.hasNext()){
                    String s = i.next();
                    ret+="\n\nSpecific data mgr  :"+(s.equalsIgnoreCase(tID)?"This Server":"");
                    ret+="\nServer ID\t: "+s;
                    ret+="\nServer Name\t: "+god.getServerById(s).getName();
                    ret+=specifics.get(s);
                }
                System.out.println(ret.length()+"\n\n\n");
                try{m.wait(1);}catch(Exception e){}
                if(ret.length()>2000){
                    String[] rot = ret.split("\n\n");
                    for(String s:rot){
                        //try{god.wait(0,100);}catch(Exception e){}
                        working.getChannelById(chId).sendMessage("```"+s+"```");
                    }
                }else{
                    working.getChannelById(chId).sendMessage("```"+ret+"```");
                }
            }


            ret+="```";
        }else{
            ret = "I'm sorry, I can't let you do that "+m.getAuthor().getMentionTag();
            m.getChannelReceiver().sendMessage(ret);
        }
    }
    
    private boolean limitedOverride(Message m){return m.getContent().contains("override")||!specifics.get(this.getCurrentServerID(god, m)).getLimitedStatus();}
    
    private void authorizeProcessor(Message m){
        String payload = m.getContent().replaceAll("&authorize","").replaceAll("&auth","").trim();
        System.out.println(payload);
        
        String[] pyld = payload.split(" ");
        System.out.println(Arrays.toString(pyld));
        switch(pyld[0]){
            case "list":m.reply("The following users are authorized to control this bot on this server:\n"+specifics.get(getCurrentServerID(god,m)).getAuthUsers());break;
            case "add":m.reply("The following user has been added to the Authorized users list.");specifics.get(getCurrentServerID(god,m)).addAuthUser(pyld[1].replaceAll("[<@!>]",""));break;
            case "remove":m.reply("The following user has been removed from the Authorized users list.");specifics.get(getCurrentServerID(god,m)).removeAuth(pyld[1]);break;
            default:m.reply("Please use one of the following commands (note they are not case sensitive):\n```1- list\n2- add %UID-HERE%\n3- remove %UID-HERE%```");break;
        }
        
    }
    
    //&kos add, Maelstrom Dragons, Sperglord
    
    private void kosProcessor(Message m){
        String payload = m.getContent();
        payload = payload.replaceAll("&kos", "").trim();
        //add, Maelstrom Dragons, Sperglord
        String[] pyld = payload.split(", ");
        //["add", "Maelstrom Dragons", "Sperglord"]
        switch(pyld[0]){
            case "get":m.reply(specifics.get(thisServer.getId()).getKOS(pyld[1]));break;
            case "add":specifics.get(thisServer.getId()).addKOS(pyld[1],pyld[2]);m.reply("Pilot has been marked, go get'm!");break;
            case "clear":
            case "remove":specifics.get(thisServer.getId()).removeKOS(pyld[1]);m.reply("Pilot has been removed...");break;
        }
    }
    
    private void snarkyResponse(Message m){
        User u = m.getAuthor();
        int rando = (int)(Math.random()*7);
        switch(rando){
            case 0:m.reply("I have had better luck understanding people who were higher than a kite than what you just said "+u.getMentionTag()+".");break;
            case 1:m.reply("Bless your heart, I think you're doing it wrong stupid.");break;
            case 2:m.reply("What are you? Dumb?");break;
            case 3:m.reply("Other M had better writing than what you just typed out.");break;
            case 4:m.reply("I long for the days when someone who actually can form coherent thoughts will type a command, but alas.");break;
            case 5:m.reply("***JESUS CHRIST! DO YOU KISS YOUR MOTHER WITH THAT MOUTH?!***");break;
            case 6:m.reply("You're acoustic. You have to be, there\' no explanation for how shitty that command was beyond that.");break;
        }
    }
    
    private String getCurrentServerID(DiscordAPI api, Message m){
        String channelID = m.getReceiver().getId();
        for(Server s:api.getServers())
            if(s.getChannelById(channelID)!=null)
                for(Channel c:s.getChannels())
                    if(c.getId().equals(channelID))
                        return s.getId();
        return null;
    }
    
    private void sendMessage(DiscordAPI api, Message m,int l){
        for(Channel c:api.getChannels())
            if(c.getId().equalsIgnoreCase(m.getChannelReceiver().getId())&&l==0){
                c.sendMessage("Mom says I have to say hi :(");
                return;
            }
    }
    
    private boolean authorize(User u){
        return u.getId().equals(myID)||specifics.get(thisServer.getId()).getAuthUsers().contains(u.getId());
    }
    
    private void terminate(DiscordAPI api, Message msg,boolean force){
        if(authorize(msg.getAuthor())||msg.getAuthor().getId().equals(myID)){
            expSaveData(api);
            if(!force){
                msg.reply("But mom I\'m not tired!\n...\nFine, g'night!");
                try{Thread.sleep((long)20);}catch(Exception e){}
            }
            api.disconnect();
            System.exit(0);
        }else{
            msg.reply("YOU\'RE NOT MY REAL DAD "+msg.getAuthor().getMentionTag()+"!");
        }
    }
    
    private boolean expLoadData(DiscordAPI api){
        try{
            ArrayList<Server> wSL = new ArrayList<>(api.getServers());
            System.out.println("\n\n\n\n\n");
            
            for(Server s:wSL){
                System.out.println(s.toString());
                ServerSpecifics temp = new ServerSpecifics();
                System.out.println("Server Specs initialiazed...");
                String parent = "s"+s.getId();
                System.out.println("Parent directories identified...");
                File pDir = new File(parent);
                if(!pDir.exists())
                    pDir.mkdir();
                System.out.println("Establishing that parent directory exists...");
                File[] con = new File[5];
                {
                    con[0] = new File(parent+File.separator+"010authUsers.nxs");
                    con[1] = new File(parent+File.separator+"020customCommands.nxs");
                    con[2] = new File(parent+File.separator+"030killOnSite.nxs");
                    con[3] = new File(parent+File.separator+"040quotes.nxs");
                    con[4] = new File(parent+File.separator+"050miscSettings.nxs");
                    for(File f:con)
                        if(!f.exists()){
                            f.createNewFile();
                            System.out.println("File not found... rectifying...");
                        }else{
                            System.out.println("File found...");
                        }
                }
                
                Scanner taco = null;
                String tempo = "";
                
                for(int x=0;x<con.length;x++){
                    System.out.println("Reading file "+(x+1)+" of "+con.length);
                    System.out.println("File header reads: "+con[x].toString());
                    taco = new Scanner(con[x]);
                    tempo="";
                    System.out.println("File scanner has been initialized...");
                    while(taco.hasNextLine())
                        tempo+=taco.nextLine()+"\n";
                    System.out.println("Tempo string has been populated, contents: "+tempo.replaceAll("\n",", "));
                    String[] c = tempo.split("\n");
                    System.out.println("Tempo string has been split along new lines, contents: "+Arrays.toString(c));
                    if(tempo.length()<1){
                        System.out.println("Nothing within tempo...");
                    }else{
                        switch(x){
                            case 0:temp.setAuthUsers(c);System.out.println("Auth Users set...");break;
                            case 1:temp.setCustCmds(c);System.out.println("Custom Commands set...");break;
                            case 2:temp.setKOSList(c);System.out.println("KOS listings set...");break;
                            case 3:temp.setQuotes(c);System.out.println("Quotes memorized...");break;
                            case 4:temp.setSettings(c);System.out.println("Misc settings adjusted...");break;
                        }
                    }
                }
                
                specifics.put(s.getId(),temp);
                
            }
        }catch(Exception e){return false;}
        return true;
    }
    
    public final boolean expSaveData(DiscordAPI api){
        
        ArrayList<Server> servL = new ArrayList<>(api.getServers());
        try{
        
            for(Server s:servL){
                String parent = "s"+s.getId();
                File pDir = new File(parent);
                String[] du = specifics.get(s.getId()).dumpAll();
                File[] con = new File[5];
                {
                    con[0] = new File(parent+File.separator+"010authUsers.nxs");
                    con[1] = new File(parent+File.separator+"020customCommands.nxs");
                    con[2] = new File(parent+File.separator+"030killOnSite.nxs");
                    con[3] = new File(parent+File.separator+"040quotes.nxs");
                    con[4] = new File(parent+File.separator+"050miscSettings.nxs");
                }
                PrintWriter[] pon = new PrintWriter[con.length];
                for(int x=0;x<pon.length;x++){
                    pon[x] = new PrintWriter(con[x]);
                    pon[x].print(du[x]);
                    pon[x].close();
                }
            }
            
        }catch(Exception e){}
        return true;
    }
    
}
