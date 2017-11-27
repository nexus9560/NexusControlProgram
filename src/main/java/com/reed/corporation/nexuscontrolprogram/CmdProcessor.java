/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reed.corporation.nexuscontrolprogram;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAttachment;
import de.btobastian.javacord.entities.message.embed.Embed;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.entities.permissions.Role;
import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author ke5vr
 */
public class CmdProcessor{
    
    
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
    
    CmdProcessor(DiscordAPI apples){
        if(god==null)
            god = apples;
        if(!aprldd){
            expLoadData(apples);
            aprldd = true;
        }
    }
    
    CmdProcessor(DiscordAPI apples, Server s){
        if(god==null)
            god = apples;
        if(!aprldd){
            expLoadData(apples);
            thisServer = s;
            serverID = s.getId();
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
        The "main" method of the command processor, pass in the api, the message, and watch the world burn.
    */
    
    public final void process(DiscordAPI api, Message msg){
        if(msg.getAuthor().isBot())
            return;
        ////System.out.*(god.toString());
        
        //Temporary fix for muting people, as I haven't quite figured out how to have timers and such....
        Iterator<String> i = specifics.keySet().iterator();
        while(i.hasNext())
            specifics.get(i.next()).autoUnMute();
        
        
        //Checks to see if the user is ignored, if the ignored user tries to execute a command, it responds as such. (May be changed to not respond not sure)
        String sID = msg.getChannelReceiver().getServer().getId();
        if(specifics.get(sID).isIgnored(msg.getAuthor().getId())){
            msg.reply("I'm sorry, "+msg.getAuthor().getMentionTag()+", I've been told not to listen to you.");
            return;
        }
        //checks to see if user is muted (If applicable)
        if(specifics.get(sID).getMuteRoleId()!=null&&msg.getAuthor().getRoles(msg.getChannelReceiver().getServer()).contains(msg.getChannelReceiver().getServer().getRoleById(specifics.get(sID).getMuteRoleId())))
            return;
        
        //I'm the only one who can tell the bot to kill itself, since I haven't quite got it to auto start and auto terminate, this is what I use to terminate the bot
        if(msg.getContent().contains("&kys")&&msg.getAuthor().getId().equals("103730295533993984"))
            terminate(api,msg,true);
        //This is more or less going to be depreciated at some point...
        if(tisServ==null){
            tisServ = specifics.get(api.getServerById(this.getCurrentServerID(api, msg)).getId());
        }
        
        //This is where the fun begins, a hardwired switch, but for anything that requires heavy lifting, I'd prefer to have it hardwired.
        String cmd = msg.getContent();
        if(cmd.contains(" "))
            cmd = cmd.substring(0, msg.getContent().indexOf(" "));
        cmd = cmd.replaceAll("&","").toLowerCase();
        
        switch(cmd){
            case "auth"         :
            case "authorize"    :authorizeProcessor(msg);break;
            case "cmd"          :custCommands(msg);break;
            case "diag"         :
            case "diagnostics"  :diagnostics(msg);break;
            case "guestPass"    :guestPass(msg);break;
            case "ignore"       :ignoreProcessor(msg);break;
            case "kos"          :kosProcessor(msg);break;
            case "lmds"         :
            case "listmsgdtls"  :listMessageDetails(api, msg);break;
            case "lrids"        :
            case "listroleids"  :listRoleDetails(msg);break;
            case "mute"         :muteAction(msg);break;
            case "quote"        :quoteProcessor(msg);break;
            case "reload"       :expLoadData(api);msg.reply("Reloading saved data...");break;
            case "roll"         :msg.reply(DiceRoll.roll(msg));break;
            case "save"         :expSaveData(api);msg.reply("Saving data to file...");break;
            case "sudo"         :sudoActions(api, msg);break;
            case "systime"      :msg.reply(System.currentTimeMillis()+"");break;
            case "unmute"       :unmuteAction(msg);break;
            case "help"         :helpAction(msg);break;
            default             :custCmdProc(msg);
        }
        
    }
    
    //Processes ignore commands, hardwired to never ignore me under any circumstances
    //Users marked as "ignored" are ignored regardless of their approved statuses
    
    public void ignoreProcessor(Message m){
        if(authorize(m)){
            String sID = m.getChannelReceiver().getServer().getId();
            String q = m.getContent().replaceAll("&ignore", "").trim();
            String[] pyld = q.split(" ");
            for(String io: pyld){
                if(io.contains(myID)){
                    m.reply("Not happening.");
                    return;
                }
            }
            //{"add", "&USER-ID&"}
            //{"add", "&USER-ID&", "&USER-UD&"}
            if(pyld[0].equalsIgnoreCase("add")){
                if(pyld.length>2){
                    for(int x=1;x<pyld.length;x++){
                        specifics.get(sID).addIgnoredUser(pyld[x].replaceAll("[<!@#&>*]",""));
                    }
                    m.reply("Users have been ignored.");
                }else{
                    specifics.get(sID).addIgnoredUser(pyld[1].replaceAll("[<!@#&>*]",""));
                    m.reply("User has been ignored.");
                }
            }else if(pyld[0].equalsIgnoreCase("remove")){
                if(pyld.length>2){
                    for(int x=1;x<pyld.length;x++){
                        specifics.get(sID).removeIgnored(pyld[x].replaceAll("[<!@#&>*]",""));
                    }
                    m.reply("Users have been un... ignored...? yea...");
                }else{
                    specifics.get(sID).removeIgnored(pyld[1].replaceAll("[<!@#&>*]",""));
                    m.reply("User has been un... ignored...? yea...");
                }
            }
        }else{
            m.reply("I'm sorry, "+m.getAuthor().getMentionTag()+", I can't do that.");
        }
    }
    
    public EmbedBuilder buildEmbed(Message m){
        EmbedBuilder ret = new EmbedBuilder();
        User author = m.getAuthor();
        String content = m.getContent();
        String[] splitter = content.split(" ");
        return ret;
    }
    
    //Sends a log message in the designated bot log channel
    
    public void sendBotLogMessage(Message m,String q){
        String sID = m.getChannelReceiver().getServer().getId();
        Channel blc = god.getServerById(sID).getChannelById(specifics.get(sID).getBotLog());
        if(m.getChannelReceiver().equals(blc))
            return;
        else if(blc!=null&&q!=null){
            String iq = m.getChannelReceiver().getName();
            blc.sendMessage("Original message in \""+iq+"\":\n```"+q+"```");
            blc.sendMessage("Edited message:\n```"+m.getContent()+"```");
        }else if(blc!=null){
            blc.sendMessage("Deleted message:\n```"+m.getContent()+"```");
            blc.sendMessage("In \""+m.getChannelReceiver().getName()+"\"");
        }
    }
    
    //Sends a welcome message to a user on join to the server if welcome settings have been established
    
    public void sendServerWelcomeMessage(Server s, User u){
        if(specifics.get(s.getId()).getWelcoming()){
            Channel c = s.getChannelById(specifics.get(s.getId()).getWelcChanID());
            String send = specifics.get(s.getId()).getWelcomeMessage().replaceAll("&USER&",u.getMentionTag());
            if(specifics.get(s.getId()).getGuessPassId()!=null)
                send+="\nThis server has a guest pass!\nPlease type &guestPass to get it.";
            c.sendMessage(send);
        }
    }
    
    //Unmutes a user, but only if the person unmuting is authorized
    
    private void unmuteAction(Message m){
        if(authorize(m)){
            Server s = m.getChannelReceiver().getServer();
            String[] c = m.getContent().split(" ");
            for(int x=1;x<c.length;x++)
                c[x] = c[x].replaceAll("[<@!>]","");
            String i = s.getId();
            Role mute = s.getRoleById(specifics.get(i).getMuteRoleId());
            for(int x=1;x<c.length;x++){
                mute.removeUser(s.getMemberById(c[x]));
                specifics.get(i).unmutePerson(c[x]);
            }
        }else{
            m.reply("I'm sorry, "+m.getAuthor().getMentionTag()+", I can't let you do that.");
        }
    }
    
    //Mutes a user, but only if the person muting is authorized, muting also only works if there is a designated role
    
    private void muteAction(Message m){
        //&mute %USER-ID
        if(authorize(m)){
            Server s = m.getChannelReceiver().getServer();
            String[] c = m.getContent().split(" ");
            for(int x=1;x<c.length;x++)
                c[x] = c[x].replaceAll("[<@!>]","");
            String i = s.getId();
            Role mute = s.getRoleById(specifics.get(i).getMuteRoleId());
            for(int x=1;x<c.length;x++){
                mute.addUser(s.getMemberById(c[x]));
                specifics.get(i).mutePerson(c[x]);
            }
            if(specifics.get(i).getMuteChannelId()!=null)
                for(int x=1;x<c.length;x++)
                    s.getChannelById(specifics.get(i).getMuteChannelId()).sendMessage("Welcome to Purge-atory "+s.getMemberById(c[x]).getMentionTag()+", please enjoy your stay.");
        }else{
            m.reply("I'm sorry, "+m.getAuthor().getMentionTag()+", I can't let you do that.");
        }
    }
    
    //Assigns a user a "guest pass", provided the user has no role, and the server has a guest pass
    
    private void guestPass(Message m){
        Server tws = m.getChannelReceiver().getServer();
        if(m.getAuthor().getRoles(tws).isEmpty()&&tws.getRoleById(specifics.get(tws.getId()).getGuessPassId())!=null){
            Role guest = tws.getRoleById(specifics.get(tws.getId()).getGuessPassId());
            guest.addUser(m.getAuthor());
            m.getAuthor().sendMessage("You now have a guest pass for "+tws.getName());
            m.delete();
        }else{
            if(specifics.get(tws.getId()).getGuessPassId()==null)
                m.reply("I'm sorry, but this server does not have a guest pass, my condolences...");
            else if(!m.getAuthor().getRoles(tws).isEmpty())
                m.reply("Hey! You already have roles!");
        }
    }
    
    //Adjusting server settings
    
    private void sudoActions(DiscordAPI a, Message m){
        if(authorize(m)){
            Server serv = m.getChannelReceiver().getServer();
            String sID = serv.getId();
            //&sudo set guestID %GUEST-ID%
            String mC = m.getContent().replaceAll("&sudo","").trim();
            //set guestID %GUEST-ID%
            String[] cmP = mC.split(" ");
            //["set", "guestID", "%GUEST-ID%"]
            //System.out.*(Arrays.toString(cmP));
            if(cmP[0].equalsIgnoreCase("set")){
                try{
                    switch(cmP[1]){
                        case "guestID"          :specifics.get(sID).setGuestRoleId(cmP[2]);break;
                        case "muteID"           :specifics.get(sID).setMuteRoleId(cmP[2]);break;
                        case "muteChannel"      :specifics.get(sID).setMuteChannel(cmP[2]);break;
                        case "welcome"          :specifics.get(sID).setWelcoming(Boolean.parseBoolean(cmP[2]));break;
                        case "welcomeMessage"   :specifics.get(sID).setWelcomeMessage(cmP);break;
                        case "welcomeChannel"   :specifics.get(sID).setWelcChanID(cmP[2]);break;
                        case "botLog"           :specifics.get(sID).setBotLog(cmP[2]);break;
                        case "limited"          :specifics.get(sID).setLimitedStatus(Boolean.parseBoolean(cmP[2]));break;
                        default                 :
                            String reply = "";
                            reply+="The following are the settings you can change:\n```";
                            reply+="guestID         -> Appropriate role for guests\n";
                            reply+="muteID          -> role ID for muting people\n";
                            reply+="muteChannel     -> channel for disposing of muted people\n";
                            reply+="welcome         -> the toggle for sending a welcome message true/false\n";
                            reply+="welcomeMessage  -> The welcome message for welcoming new users, only sends if welcome is set to true\n";
                            reply+="welcomeChannel  -> The channel that welcome message is sent in\n";
                            reply+="botLog          -> The channel for dumping logging actions\n";
                            reply+="limited         -> Determines if the bot has limited diagnostics output, set to true by default\n";
                            reply+="```";
                            m.reply(reply);
                            return;
                    }
                }catch(Exception e){
                    
                }
            }else if(cmP[0].equalsIgnoreCase("get")){
                switch(cmP[1]){
                    case "guestID"          :m.reply("Guest role ID for this server is  : "+specifics.get(sID).getGuessPassId());break;
                    case "muteID"           :m.reply("Mute role ID for this server is   : "+specifics.get(sID).getMuteRoleId());break;
                    case "muteChannel"      :m.reply("Mute Channel ID for this server is: "+specifics.get(sID).getMuteChannelId());break;
                    case "controllerSize"   :m.reply("The data manager has : "+specifics.keySet().size()+" servers on it");
                    case "connected"        :m.reply("This bot is actively connected to: "+god.getServers().size() +" servers");break;
                    case "welcomeStatus"    :m.reply("The welcome message is: "+(specifics.get(sID).getWelcoming()?"on":"off"));break;
                    case "welcomeMessage"   :m.reply("The Welcome Message is: "+specifics.get(sID).getWelcomeMessage());break;
                    case "welcomeChannel"   :m.reply("The Welcome channel is: "+specifics.get(sID).getWelcChanID());break;
                    case "limited"          :m.reply("The limited status of this server is: "+(specifics.get(sID).getLimitedStatus()?"Limited":"Not Limited"));break;
                    default                 :
                            String reply = "";
                            reply+="The following are the settings you can change:\n```";
                            reply+="guestID         -> Appropriate role for guests\n";
                            reply+="muteID          -> role ID for muting people\n";
                            reply+="muteChannel     -> channel for disposing of muted people\n";
                            reply+="welcome         -> the toggle for sending a welcome message true/false\n";
                            reply+="welcomeMessage  -> The welcome message for welcoming new users, only sends if welcome is set to true\n";
                            reply+="welcomeChannel  -> The channel that welcome message is sent in\n";
                            reply+="botLog          -> The channel for dumping logging actions\n";
                            reply+="limited         -> Determines if the bot has limited diagnostics output, set to true by default\n";
                            reply+="```";
                            m.reply(reply);
                            return;
                }
            }
            if(cmP[0].equalsIgnoreCase("set"))
                m.reply("It has been done.");
        }else{
            m.reply("I'm sorry, "+m.getAuthor().getMentionTag()+", I can't let you do that.");
        }
    }
    
    //DMs message details, namely information on the content
    
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
    
    //Lists the IDs for roles. In the future I'll have it more on a role by role basis, but for right now, I'd rather pull the roles in batch
    
    private void listRoleDetails(Message m){
        String sent = "";
        ArrayList<Role> roles = new ArrayList<>(god.getServerById(this.getCurrentServerID(god, m)).getRoles());
        for(Role r:roles){
            sent+=r.toString()+"\n";
        }
        if(true||sent.length()<2000)
            m.getAuthor().sendMessage("```"+sent+"```");
        else{
        /*
            String[] ti = sent.split("\n");
            ArrayList<String> reto = new ArrayList<>();
            int lo = 0;
            int to = 1;
            reto.add(ti[0]);
            while(reto.get(lo).length()<1950){
                
            }
        */
        }
    }
    
    //Would read the attached help file, however I don't have that copied over to the server computer
    
    private void helpAction(Message m){
        try{
            Scanner scans = new Scanner(new File("help.nxs"));
            String message = "";
            while(scans.hasNextLine())
                message+=scans.nextLine()+"\n";
            m.getAuthor().sendMessage(message);
            message = "";
            scans.close();
        }catch(Exception e){System.out.println("Lol you forgot to make the helpfile again moron.");}
    }
    
    //Moved to DiceRoll.java
    
    //Makes custom commands, will eventually allow for embeds, but I am still yet to figure those out...
    
    private void custCommands(Message m){
        //&cmd add,, lookAtThem,, yadda yadda
        String payload = m.getContent().replaceAll("&cmd","").trim();
        String sID = m.getChannelReceiver().getServer().getId();
        //add| lookAtThem| yadda yadda
        String[] pyld = payload.split(",, ");
        //["add", "lookAtThem", "yadda yadda"]
        /*
        switch(pyld[0]){
            case "add":specifics.get(m.getChannelReceiver().getServer().getId()).addCustCmd(pyld[1],pyld[2]);m.reply("Command has been added!");break;
            case "remove":specifics.get(m.getChannelReceiver().getServer().getId()).deleteCmd(pyld[1]);m.reply("Command has been removed.");break;
            case "list":m.reply("The custom commands for this server are...\n```"+specifics.get(thisServer.getId()).getCustCmds().keySet()+"```");break;
        }
        */
        if(pyld[0].equals("add")){
            if(specifics.get(sID).getCustCmds().containsKey(pyld[1])){
                m.reply("Sorry that command already exists...");
            }else{
                specifics.get(sID).addCustCmd(pyld[1], pyld[2]);
                m.reply("Command has been added!");
            }
        }else if(pyld[0].equals("remove")){
            if(!specifics.get(sID).getCustCmds().containsKey(pyld[1])){
                m.reply("Sorry that command doesn't exist...");
            }else{
                specifics.get(sID).deleteCmd(pyld[1]);
                m.reply("Command has been removed");
            }
        }else if(pyld[0].equals("list")){
            m.reply(specifics.get(sID).getCustCmds().keySet().toString());
        }else{
            
        }
    }
    
    //Responds to the custom commands, again, will have embeds at some point
    
    private void custCmdProc(Message m){
        String send = "";
        
        send = specifics.get(m.getChannelReceiver().getServer().getId()).chkCmd(m.getContent().replaceAll("&", "").toLowerCase());
        if(send!=null)
            if(send.contains("http://")||send.contains("https://")){
                String[] builds = send.split(" ");
                String url = "";
                for(String q:builds)
                    if(q.contains("http://")||q.contains("https://")){
                        url=q;
                        break;
                    }
                String notURL="";
                for(String i:builds){
                    if(!i.contains("http://")&&!i.contains("https://"))
                        notURL+=i+" ";
                    else
                        notURL+="&LINKED-IMAGE& ";
                }
                EmbedBuilder b = new EmbedBuilder();
                b.setColor(Color.BLUE);
                b.setTitle(url);
                b.setUrl(url);
                b.setAuthor(m.getAuthor().getNickname(m.getChannelReceiver().getServer()));
                b.setThumbnail("https://i.imgur.com/7Oa4zTq.png");
                b.setImage(url);
                m.getChannelReceiver().sendMessage(notURL+"", b);
            }else
                m.reply(send.replaceAll("&USER&",m.getAuthor().getMentionTag()));
        else{
            snarkyResponse(m);
        }
    }
    //slave to quote processor to make sure someone doesn't make a bad quote request
    
    private int greaterThanZero(String s){
        int ret = new Integer(s)-1;
        if(ret<0)
            return 0;
        else
            return ret;
    }
    
    //Manages quotes, can add, remove, get Nth quote, and get most recent quote
    
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
    
    //Provides diagnostic output on the status of the bot, makes sure the data manager has been instantiated, and lists the various settings for the bot
    
    private void diagnostics(Message m){
        String ret="";
        if(authorize(m)){
            String chId = m.getChannelReceiver().getId();
            String tID = m.getChannelReceiver().getServer().getId();
            Server working = m.getChannelReceiver().getServer();
            boolean limSta = specifics.get(tID).getLimitedStatus();
            
            ret+="This Server is     : "+working.getName()+"\n";
            ret+="This Servers ID is : "+working.getId()+"\n";
            ret+="HashMap db status  : "+(specifics.keySet().isEmpty()?"Not Populated...":"Populated!")+"\n";
            if(limitedOverride(m)){
                ret+="HashMap db pop stat: "+specifics.keySet()+"\n";
                ret+="Server Lists       : "+god.getServers()+"\n";
            }
            ret+="Server Specific mgr: "+(specifics!=null?specifics.get(tID).toString()+"\n":"Data controller not loaded...\n");
            ret+="First run setup int: "+aprldd+"\n";
            ret+="Limited Mode       : "+specifics.get(tID).getLimitedStatus();
            //m.reply("```"+ret+"```");
            god.getServerById(tID).getChannelById(chId).sendMessage("```"+ret+"```");
            ret="";
            if(specifics!=null&&(!limSta||limitedOverride(m))){
                Iterator<String> i = specifics.keySet().iterator();
                while(i.hasNext()){
                    String s = i.next();
                    if(s.equals(tID))
                        continue;
                    ret+="Specific data mgr  :"+(s.equalsIgnoreCase(tID)?"This Server":"");
                    ret+="\nServer ID\t: "+s;
                    ret+="\nServer Name\t: "+god.getServerById(s).getName();
                    ret+=specifics.get(s)+"\n\n";
                }
                
                //System.out.*(ret.length()+"\n\n\n");
                //try{m.wait(1);}catch(Exception e){}
//                if(ret.length()>2000){
//                    String[] rot = ret.split("\n\n");
//                    for(String s:rot){
//                        //try{god.wait(0,100);}catch(Exception e){}
//                        //try{Thread.sleep(0,3000);}catch(Exception e){}
//                        //System.out.println("\n\n"+s+"\n\n");
//                        working.getChannelById(chId).sendMessage("```"+s+"```");
//                    }
//                }else{
//                    working.getChannelById(chId).sendMessage("```"+ret+"```");
//                }
                for(String sqr:ret.split("\n\n")){
                    String send = "```"+sqr+"```";
                    if(send.length()>10){
                        m.reply(send);
                        try{god.wait(0, 200000000);}catch(Exception e){}
                    }
                }
            }
        }else{
            ret = "I'm sorry, I can't let you do that "+m.getAuthor().getMentionTag();
            m.getChannelReceiver().sendMessage(ret);
        }
    }
    
    //slave to Diagnostics, checks to see if the limited status has been overriden
    
    private boolean limitedOverride(Message m){
        return m.getContent().contains("override")||m.getContent().contains("ovr")||!specifics.get(this.getCurrentServerID(god, m)).getLimitedStatus();
    }
    
    //Manages the Authorization and Deauthorization of users and roles, can do multiple at a time.
    
    private void authorizeProcessor(Message m){
        String payload = m.getContent().replaceAll("&authorize","").replaceAll("&auth","").trim();
        String sID = m.getChannelReceiver().getServer().getId();
        //System.out.*(payload);
        
        String[] pyld = payload.split(" ");
        System.out.println(Arrays.toString(pyld));
        String taco = "";
        if(pyld.length>=2)
            for(int x=2;x<pyld.length;x++)
                pyld[x] = pyld[x].replaceAll("[<@#&!>*]", "");
        
        for(String q:pyld)
            taco+=q+"\n";
        System.out.println(taco);
        if(!authorize(m)){
            m.reply("I'm sorry "+m.getAuthor().getMentionTag()+", I can't let you do that.");
            return;
        }else{
            // command would look like {"add", "user", "<@1234567890>"}
            if(pyld[0].equalsIgnoreCase("add")){
                if(pyld[1].equalsIgnoreCase("user")){
                    if(pyld.length==3){
                        specifics.get(sID).addAuthUser(pyld[2]);
                        m.reply("User confirmed!");
                    }else{
                        for(int x=2;x<pyld.length;x++){
                            specifics.get(sID).addAuthUser(pyld[x]);
                        }
                    }
                }else if(pyld[1].equalsIgnoreCase("role")){
                    if(pyld.length==3){
                        specifics.get(sID).addAuthRole(pyld[2]);
                        m.reply("Role confirmed!");
                    }else{
                        for(int x=2;x<pyld.length;x++){
                            specifics.get(sID).addAuthRole(pyld[x]);
                        }
                    }
                }else{
                }
            }else if(pyld[0].equalsIgnoreCase("remove")){
                if(pyld[1].equalsIgnoreCase("user")){
                    if(pyld.length==3){
                        specifics.get(sID).removeAuth(pyld[2]);
                        m.reply("User removed!");
                    }else{
                        for(int x=2;x<pyld.length;x++){
                            specifics.get(sID).removeAuth(pyld[x]);
                        }
                    }
                }else if(pyld[1].equalsIgnoreCase("role")){
                    if(pyld.length==3){
                        specifics.get(sID).removeAuthRole(pyld[2]);
                        m.reply("Role removed!");
                    }else{
                        for(int x=2;x<pyld.length;x++){
                            specifics.get(sID).removeAuthRole(pyld[x]);
                        }
                    }
                }
            }else if(pyld[0].equalsIgnoreCase("list")){
                if(pyld[1].equalsIgnoreCase("user")){
                    m.reply("users listed!");
                }else if(pyld[1].equalsIgnoreCase("role")){
                    m.reply("roles listed!");
                }
            }
            
        }
        /*
        switch(pyld[0]){
            case "list":m.reply("The following users are authorized to control this bot on this server:\n"+specifics.get(getCurrentServerID(god,m)).getAuthUsers());break;
            case "add":m.reply("The following user has been added to the Authorized users list.");specifics.get(getCurrentServerID(god,m)).addAuthUser(pyld[1].replaceAll("[<@!>]",""));break;
            case "remove":m.reply("The following user has been removed from the Authorized users list.");specifics.get(getCurrentServerID(god,m)).removeAuth(pyld[1]);break;
            default:m.reply("Please use one of the following commands (note they are not case sensitive):\n```1- list\n2- add %UID-HERE%\n3- remove %UID-HERE%```");break;
        }
        if(pyld[0].equalsIgnoreCase("add")){
            if(pyld[1].equalsIgnoreCase("role")){
                if(pyld.length>3){
                    for(int x=2;x<pyld.length;x++){
                        specifics.get(sID).addAuthRole(pyld[x].replaceAll("[<>@!:*]", ""));
                    }
                }else{
                    specifics.get(sID).addAuthRole(pyld[2].replaceAll("[<>@!:*]", ""));
                }
                m.reply("It has been done.");
            }else if(pyld[1].equalsIgnoreCase("user")){
                if(pyld.length>3)
                    for(int x=2;x<pyld.length;x++){
                        specifics.get(sID).addAuthUser(pyld[x]);
                    }
                else{
                    specifics.get(sID).addAuthUser(pyld[2]);
                    System.out.println(true);
                }
            }else{
                m.reply("Please format your add command like so:\n"
                        + "```"
                        + "&auth add user &USER-1& &USER-2& &USER-N&\n"
                        + "or\n"
                        + "&auth add role &ROLE-1& &ROLE-2& &ROLE-N&\n"
                        + "Please note that in order for the role one to work you need the specific role IDs, which can be attained with '&lrids'"
                        + "```");
            }
        }else if(pyld[0].equalsIgnoreCase("remove")){
            if(pyld[1].equalsIgnoreCase("role")){
                if(pyld.length>3){
                    int count = 0;
                    for(int x=2;x<pyld.length;x++){
                        if(specifics.get(sID).getAuthRoles().contains(pyld[x])){
                            specifics.get(sID).removeAuthRole(pyld[x]);
                            count++;
                        }
                    }
                    m.reply("Of the "+(pyld.length-2)+" roles you requested removed, there were "+count+" that were found and removed.");
                }else{
                    if(specifics.get(sID).getAuthRoles().contains(pyld[2])){
                        specifics.get(sID).removeAuthRole(pyld[2]);
                        m.reply("Role has been found and removed.");
                    }else{
                        m.reply("Sorry, that role isn't on the list.");
                    }
                }
            }else if(pyld[1].equalsIgnoreCase("user")){
                if(pyld.length>3){
                    int count = 0;
                    for(int x=2;x<pyld.length;x++){
                        if(specifics.get(sID).getAuthUsers().contains(pyld[x])){
                            specifics.get(sID).removeAuth(pyld[x]);
                            count++;
                        }
                    }
                    m.reply("Of the "+(pyld.length-2)+" Users you requested removed, there were "+count+" that were found and removed.");
                }else{
                    if(specifics.get(sID).getAuthUsers().contains(pyld[2])){
                        specifics.get(sID).removeAuth(pyld[2]);
                        m.reply("User has been found and removed.");
                    }else{
                        m.reply("Sorry, that User isn't on the list.");
                    }
                }
            }else{
                m.reply("Please format your add command like so:\n"
                        + "```"
                        + "&auth remove user &USER-1& &USER-2& &USER-N&\n"
                        + "or\n"
                        + "&auth remove role &ROLE-1& &ROLE-2& &ROLE-N&\n"
                        + "Please note that in order for the role one to work you need the specific role IDs, which can be attained with '&lrids'"
                        + "```");
            }
        }else if(pyld[0].equalsIgnoreCase("list")){
            if(pyld[1].equalsIgnoreCase("role")){
                m.reply(specifics.get(sID).getAuthRoles().toString());
            }else if(pyld[1].equalsIgnoreCase("user")){
                m.reply(specifics.get(sID).getAuthUsers().toString());
            }else{
                
            }
        }else{
            m.reply("Please format your add command like so:\n"
                        + "```"
                        + "&auth remove user &USER-1& &USER-2& &USER-N&\n"
                        + "or\n"
                        + "&auth remove role &ROLE-1& &ROLE-2& &ROLE-N&\n"
                        + "Please note that in order for the role one to work you need the specific role IDs, which can be attained with '&lrids'"
                        + "```");
        }

        */
        
    }
    
    //A work in progress "Kill On Sight" system, keep track of those pesky players who play the game in ways you don't like.
    
    private void kosProcessor(Message m){
        String payload = m.getContent();
        payload = payload.replaceAll("&kos", "").trim();
        String[] pyld = payload.split(", ");
        switch(pyld[0]){
            case "get":m.reply(specifics.get(thisServer.getId()).getKOS(pyld[1]));break;
            case "add":specifics.get(thisServer.getId()).addKOS(pyld[1],pyld[2]);m.reply("Pilot has been marked, go get'm!");break;
            case "clear":
            case "remove":specifics.get(thisServer.getId()).removeKOS(pyld[1]);m.reply("Pilot has been removed...");break;
        }
    }
    
    //User for insulting the user should they make a bad command, gradually getting phased out with actual formatting responses
    
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
    
    //Grabs the server ID based on a message
    
    private String getCurrentServerID(DiscordAPI api, Message m){
        return m.getChannelReceiver().getServer().getId();
    }
    
    //Says 'hi' in the designated channel... needs a rework
    
    private void sendMessage(DiscordAPI api, Message m,int l){
        for(Channel c:api.getChannels())
            if(c.getId().equalsIgnoreCase(m.getChannelReceiver().getId())&&l==0){
                c.sendMessage("Mom says I have to say hi :(");
                return;
            }
    }
    
    //determines if a user is either directly authorized or has a role that permits use of higher level commands
    
    private boolean authorize(Message m){
        if(m.getAuthor().getId().equals("103730295533993984"))
            return true;
        if(specifics.get(m.getChannelReceiver().getServer().getId()).getAuthUsers().contains(m.getAuthor().getId()))
            return true;
        else if(specifics.get(m.getChannelReceiver().getServer().getId()).compareRoles(m))
            return true;
        return false;
    }
    
    //Shutdown protocol for the bot, if the shutdown is not forced it'll respond with the message then shut off, otherwise it'll object
    
    private void terminate(DiscordAPI api, Message msg,boolean force){
        if(authorize(msg)||msg.getAuthor().getId().equals(myID)){
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
    
    //How I load the data from files, for each server there is a folder, in each folder there are (7) files, each file saves a certain thing
    
    private boolean expLoadData(DiscordAPI api){
        try{
            ArrayList<Server> wSL = new ArrayList<>(api.getServers());
            //System.out.*("\n\n\n\n\n");
            
            for(Server s:wSL){
                System.out.println(s.toString());
                ServerSpecifics temp = new ServerSpecifics();
                System.out.println("Server Specs initialiazed...");
                String parent = "S"+s.getId();
                System.out.println("Parent directories identified...");
                File pDir = new File(parent);
                if(!pDir.exists())
                    pDir.mkdir();
                System.out.println("Establishing that parent directory exists...");
                File[] con = new File[7];
                {
                    con[0] = new File(parent+File.separator+"010authUsers.nxs");
                    con[1] = new File(parent+File.separator+"013ignoredUsers.nxs");
                    con[2] = new File(parent+File.separator+"015authRoles.nxs");
                    con[3] = new File(parent+File.separator+"020customCommands.nxs");
                    con[4] = new File(parent+File.separator+"030killOnSite.nxs");
                    con[5] = new File(parent+File.separator+"040quotes.nxs");
                    con[6] = new File(parent+File.separator+"050miscSettings.nxs");
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
                            case 1:temp.setIgnoredUsers(c);System.out.println("Ignored Users set...");break;
                            case 2:temp.setAuthRoles(c);System.out.println("Auth Roles set...");break;
                            case 3:temp.setCustCmds(c);System.out.println("Custom Commands set...");break;
                            case 4:temp.setKOSList(c);System.out.println("KOS listings set...");break;
                            case 5:temp.setQuotes(c);System.out.println("Quotes memorized...");break;
                            case 6:temp.setSettings(c);System.out.println("Misc settings adjusted...");break;
                        }
                    }
                }
                
                specifics.put(s.getId(),temp);
                
            }
        }catch(Exception e){return false;}
        return true;
    }
    
    //How I save the data, again, for each server there is a folder, and for each folder there are a handful of files for the various variables
    
    public final boolean expSaveData(DiscordAPI api){
        
        ArrayList<Server> servL = new ArrayList<>(api.getServers());
        try{
        
            for(Server s:servL){
                String parent = "S"+s.getId();
                File pDir = new File(parent);
                String[] du = specifics.get(s.getId()).dumpAll();
                File[] con = new File[7];
                {
                    con[0] = new File(parent+File.separator+"010authUsers.nxs");
                    con[1] = new File(parent+File.separator+"013ignoredUsers.nxs");
                    con[2] = new File(parent+File.separator+"015authRoles.nxs");
                    con[3] = new File(parent+File.separator+"020customCommands.nxs");
                    con[4] = new File(parent+File.separator+"030killOnSite.nxs");
                    con[5] = new File(parent+File.separator+"040quotes.nxs");
                    con[6] = new File(parent+File.separator+"050miscSettings.nxs");
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
