/*
*/
package com.reed.corporation.nexuscontrolprogram;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.MessageDeleteListener;
import de.btobastian.javacord.listener.message.MessageEditListener;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import de.btobastian.javacord.listener.server.ServerLeaveListener;
import de.btobastian.javacord.listener.server.ServerMemberAddListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;


/**
 * Bot id 339467323251359744
 * @author Disingenuous Donuts
 */
public class CoreProcessor {
    
    private static CmdProcessor working = null;
    
    private static DiscordAPI god = null;
    
    private static JFrame backend = null;
    
    private GeneralListener mo = new GeneralListener();
    
    public CoreProcessor(String token){
        launchGUIBackend();
        if(god==null)
            god = Javacord.getApi(token, true);
        god.setAutoReconnect(true);
        god.connect(new FutureCallback<DiscordAPI>()
        {
            public void onSuccess(DiscordAPI apo){
                apo.registerListener(mo);
                working = new CmdProcessor(apo,apo.getServerById("156289196451954688"));
            }
            public void onFailure(Throwable t){
                ProcessorCaller.main(null);
                try{this.wait(500);}catch(InterruptedException e){}
                System.exit(0);
            }
        });
    }
    
    private void reactionAddF(Message m){
    }
    
    private void launchGUIBackend(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e) {
           // handle exception
        }
        backend = new JFrame();
        backend.setSize(640, 480);
        backend.setResizable(false);
        backend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        backend.setLocationRelativeTo(null);
        backend.setTitle("Nexus Control Program");
        JButton sAQ = new JButton("Save");
        sAQ.setSize(sAQ.getPreferredSize());
        sAQ.addActionListener(new SaveAndQuitListener());
        backend.add(sAQ);
        
        backend.setVisible(true);
    }
    
    private boolean bieberDetector(Message m){
        String s = m.getContent();
        return s.contains("Justin bieber")||s.contains("justin bieber")||s.contains("justin Bieber");
    }
    
    class GeneralListener implements MessageCreateListener, MessageEditListener, MessageDeleteListener, ServerJoinListener, ServerLeaveListener, ServerMemberAddListener{
        public void onMessageCreate(DiscordAPI apc, Message mess){
                        try{
                        if(mess.getAuthor().getName().equalsIgnoreCase("McJamz")&&mess.getContent().contains("MAKE ME")){
                            mess.reply(mess.getAuthor().getMentionTag()+" no.");
                        }else if(!mess.getAuthor().isBot())
                            System.out.println(mess.getContent().charAt(0)==38);
                            if(mess.getContent().charAt(0)==38||mess.getContent().substring(0,2).contains("&")){
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
                            }/*else if(mess.getContent().contains("F")&&mess.getContent().length()==1){
                                reactionAddF(mess);
                            }*/
                        }catch(Exception e){
                            e.printStackTrace();
                            String sentMessage = "Something happened here ya go     : "+e.getMessage();
                            sentMessage+=      "\nIt happened on this server        : "+mess.getChannelReceiver().getServer().getName();
                            sentMessage+=      "\nIn this thread                    : "+mess.getChannelReceiver().getName();
                            sentMessage+=      "\nBecause of this User              : "+mess.getAuthor().getName();
                            sentMessage+=      "\nWas trying to execute this cmd    : "+mess.getContent();
                            sentMessage+=      "\nWith Message ID                   : "+mess.getId();
                            god.getServerById("156289196451954688").getChannelById("278226194103664640").sendMessage("```"+sentMessage+"```");
                        }
                    }

        @Override
        public void onMessageEdit(DiscordAPI dapi, Message msg, String string) {
        }

        @Override
        public void onMessageDelete(DiscordAPI dapi, Message msg) {
        }

        @Override
        public void onServerJoin(DiscordAPI dapi, Server server) {
            if(working==null)
                working = new CmdProcessor(dapi,server);
        }
        
        public void onServerLeave(DiscordAPI dapi, Server server){
            
        }

        @Override
        public void onServerMemberAdd(DiscordAPI dapi, User user, Server server) {
            if(working==null)
                working = new CmdProcessor(dapi,server);
            working.sendServerWelcomeMessage(server,user);
        }
    
}

    
    class SaveAndQuitListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            if(working!=null&&god!=null){
                working.expSaveData(god);
                god.disconnect();
                System.exit(0);
            }
        }
        
    }
    
}

