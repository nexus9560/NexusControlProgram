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
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.MessageDeleteListener;
import de.btobastian.javacord.listener.message.MessageEditListener;
import de.btobastian.javacord.listener.server.ServerJoinListener;
import de.btobastian.javacord.listener.server.ServerLeaveListener;
import de.btobastian.javacord.listener.server.ServerMemberAddListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    
    private static JTextArea readout;
    
    private static JComboBox ser;
    
    private static HashMap<String,String> sIDs = new HashMap<>();
    
    private static JComboBox cha;
    
    private static HashMap<String,String> cIDs = new HashMap<>();
    
    private static JTextField cmdLine;
    
    private static String tkn = null;
    
    public CoreProcessor(String token){
        if(tkn==null)
            tkn = token;
        launchGUIBackend();
        callBack();
    }
    
    private void callBack(){
        if(god==null)
            god = Javacord.getApi(tkn, true);
        god.setAutoReconnect(true);
        god.connect(new FutureCallback<DiscordAPI>()
        {
            public void onSuccess(DiscordAPI apo){
                apo.registerListener(mo);
                working = new CmdProcessor(apo);
                for(Server s:god.getServers()){
                    ser.addItem(s.getName());
                    sIDs.put(s.getName(), s.getId());
                }
            }
            public void onFailure(Throwable t){
                ProcessorCaller.main(null);
                try{this.wait(500);}catch(InterruptedException e){}
                System.exit(0);
            }
        });
    }
    
    private void reactionAddF(Message m){
        m.addUnicodeReaction("🇫");
    }
    
    public final static void updateBox(String s){
        String message = "> "+s+"\n";
        readout.append(message);
        readout.setCaretPosition(readout.getText().length());
    }
    
    private void launchGUIBackend(){
        
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch (Exception e){}
        
        backend = new JFrame();
        backend.setSize(640, 480);
        backend.setResizable(false);
        backend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        backend.setLocationRelativeTo(null);
        backend.setTitle("Nexus Control Program");
        
        
        JButton sAQ = new JButton("Save and Exit");
        sAQ.setSize(sAQ.getPreferredSize());
        sAQ.setLocation(backend.getWidth()-sAQ.getWidth()-20,10);
        sAQ.setToolTipText("Saves and exits program, most noteably useful on internet disconnect.");
        sAQ.addActionListener(new SaveAndQuitListener());
        
        ser = new JComboBox();
        ser.setSize((backend.getWidth()-140)/2,sAQ.getHeight());
        ser.setLocation(10,backend.getHeight()-75);
        ser.addActionListener(new ServerSelector());
        ser.setToolTipText("Server Selector");
        
        cha = new JComboBox();
        cha.setSize(ser.getSize());
        cha.setLocation(ser.getX()+ser.getWidth()+10,ser.getY());
        cha.setToolTipText("Channel Selector");
        
        JButton recon = new JButton("Reconnect");
        recon.setSize(recon.getPreferredSize());
        recon.setLocation(backend.getWidth()-recon.getWidth()-20,40);
        recon.setToolTipText("Attempts to reconnect");
        recon.addActionListener(new ReconnectListener());
        
        JPanel panel = new JPanel();
        panel.setSize(panel.getPreferredSize());
        panel.setLayout(null);
        
        readout = new JTextArea();
        readout.setFocusable(false);
        readout.setEditable(false);
        readout.setAutoscrolls(true);
        JScrollPane rdt = new JScrollPane(readout);
        rdt.setLocation(10, 10);
        rdt.setSize(backend.getWidth()-140,backend.getHeight()-120);
        rdt.setAutoscrolls(true);
        
        cmdLine = new JTextField();
        cmdLine.setSize(rdt.getWidth(),sAQ.getHeight());
        cmdLine.setLocation(10, rdt.getHeight()+15);
        cmdLine.setFocusCycleRoot(true);
        cmdLine.addKeyListener(new CmdLineListener());
        
        JButton sndCmd = new JButton("Send");
        sndCmd.setSize(sndCmd.getPreferredSize());
        sndCmd.setLocation(cmdLine.getWidth()+cmdLine.getX()+10,cmdLine.getY());
        sndCmd.addActionListener(new SendAction());
        
        
        panel.add(ser);
        panel.add(cha);
        panel.add(cmdLine);
        panel.add(sndCmd);
        panel.add(rdt);
        panel.add(sAQ);
        panel.add(recon);
        
        backend.add(panel);
        backend.setVisible(true);
    }
    
    private boolean bieberDetector(Message m){
        String s = m.getContent();
        return s.contains("Justin bieber")||s.contains("justin bieber")||s.contains("justin Bieber");
    }
    
    class GeneralListener implements MessageCreateListener, MessageEditListener, MessageDeleteListener, ServerJoinListener, ServerLeaveListener, ServerMemberAddListener{
        public void onMessageCreate(DiscordAPI apc, Message mess){
            try{
                if(!mess.getAuthor().isBot()&&(mess.getContent().length()>0&&mess.getContent().charAt(0)=='&')||(mess.getContent().length()>2&&mess.getContent().substring(0,2).contains("&"))){
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
                }else if(mess.getContent().equalsIgnoreCase("F")&&mess.getContent().length()==1){
                    reactionAddF(mess);
                }else{
                    
                }
            }catch(Exception e){
                e.printStackTrace();
                String sentMessage = "Something happened here ya go     : "+e.getMessage();
                sentMessage+=      "\nIt happened on this server        : "+mess.getChannelReceiver().getServer().getName();
                sentMessage+=      "\nIn this thread                    : "+mess.getChannelReceiver().getName();
                sentMessage+=      "\nBecause of this User              : "+mess.getAuthor().getName();
                sentMessage+=      "\nWas trying to execute this cmd    : "+mess.getContent();
                sentMessage+=      "\nWith Message ID                   : "+mess.getId();
                god.getServerById("156289196451954688").getChannelById("278226194103664640").sendMessage("```"+sentMessage+"```");
                updateBox(e.getMessage());
                updateBox(sentMessage);
            }
        }

        @Override
        public void onMessageEdit(DiscordAPI dapi, Message msg, String string) {
            if(working!=null&&!msg.getAuthor().isBot()){
                working.sendBotLogMessage(msg, string);
            }
        }

        @Override
        public void onMessageDelete(DiscordAPI dapi, Message msg) {
            if(working!=null&&!msg.getAuthor().isBot()){
                working.sendBotLogMessage(msg, null);
            }
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
    
    class ServerSelector implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!sIDs.isEmpty()){
                cIDs.clear();
                cha.removeAllItems();
                String selser = (String)ser.getSelectedItem();
                for(Channel c:god.getServerById(sIDs.get(selser)).getChannels()){
                    cha.addItem(c.getName());
                    cIDs.put(c.getName(), c.getId());
                }
            }
        }
    }
    
    class ReconnectListener implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            callBack();
        }
        
    }
    
    class SendAction implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!sIDs.isEmpty()&&!cIDs.isEmpty()){
                String sent = cmdLine.getText();
                String serId = sIDs.get((String)ser.getSelectedItem());
                String chanId = cIDs.get((String)cha.getSelectedItem());
                god.getServerById(serId).getChannelById(chanId).sendMessage(sent);
                updateBox(sent);
                cmdLine.setText("");
            }
        }
    }
    
    class CmdLineListener implements KeyListener{

        public void keyTyped(KeyEvent e) {
            //updateBox(e.getKeyChar()+" || "+e.getKeyCode());
            if(!sIDs.isEmpty()&&!cIDs.isEmpty()&&e.getKeyCode()==10&&cmdLine.getText().length()>0){
                String sent = cmdLine.getText();
                String serId = sIDs.get((String)ser.getSelectedItem());
                String chanId = cIDs.get((String)cha.getSelectedItem());
                god.getServerById(serId).getChannelById(chanId).sendMessage(sent);
                updateBox(sent);
                cmdLine.setText("");
            }
        }

        public void keyPressed(KeyEvent e) {
            //updateBox(e.getKeyChar()+" || "+e.getKeyCode());
            if(!sIDs.isEmpty()&&!cIDs.isEmpty()&&e.getKeyCode()==10&&cmdLine.getText().length()>0){
                String sent = cmdLine.getText();
                String serId = sIDs.get((String)ser.getSelectedItem());
                String chanId = cIDs.get((String)cha.getSelectedItem());
                god.getServerById(serId).getChannelById(chanId).sendMessage(sent);
                updateBox(sent);
                cmdLine.setText("");
            }
        }

        public void keyReleased(KeyEvent e) {
            //updateBox(e.getKeyChar()+" || "+e.getKeyCode());
            if(!sIDs.isEmpty()&&!cIDs.isEmpty()&&e.getKeyCode()==10&&cmdLine.getText().length()>0){
                String sent = cmdLine.getText();
                String serId = sIDs.get((String)ser.getSelectedItem());
                String chanId = cIDs.get((String)cha.getSelectedItem());
                god.getServerById(serId).getChannelById(chanId).sendMessage(sent);
                updateBox(sent);
                cmdLine.setText("");
            }
        }
        
    }
    
}

