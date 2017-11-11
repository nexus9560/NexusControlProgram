package com.reed.corporation.nexuscontrolprogram;

import de.btobastian.javacord.entities.message.Message;

public class DiceRoll {
    
    public static String roll(Message m){
        String ret = "";
        try{
            String cmd = m.getContent().replaceAll("&roll", "").trim();
            // "10d20 + 5"
            // "5d20 - 9"
            // "2d20"
            // "d20"
            // "d20 + 5 adv"
            String[] cd = cmd.split(" ");
            // {"10d20"}
            // {"5d20"}
            // {"2d20"}
            // {"d20","+","5","adv"}
            String text = "The results "+m.getAuthor().getMentionTag()+" has requested: ";
            String[] die=null;
            if(cd[0].charAt(0)!='d')
                die = cd[0].split("d");
            if(die!=null){
                int numdie = Integer.parseInt(die[0]);
                int sides = Integer.parseInt(die[1]);
                if(numdie>250){
                    ret = ("I seriously doubt you can hold that many dice, "+m.getAuthor().getMentionTag()+" try again with fewer (x <= 250).");
                    return ret;
                }
                if(sides>100){
                    ret = ("A die that big wouldn't even be balanced, "+m.getAuthor().getMentionTag()+" try again (x <= 100).");
                    return ret;
                }
                int[] rolls = new int[numdie];
                for(int x=0;x<numdie;x++)
                    rolls[x] = roll(sides);

                for(int x=0;x<rolls.length;x++){
                    if(sides==20)
                        switch(rolls[x]){
                            case 1:text+= "__"+rolls[x]+"__ ";break;
                            case 20:text+= "***"+rolls[x]+"*** ";break;
                            default:text+= rolls[x]+" ";
                        }
                    else
                        text+=rolls[x]+" ";
                    if((x+1)!=rolls.length)
                        text+="+ ";
                    else{
                        int total = 0;
                        for(int i:rolls)
                            total+=i;
                        text+= "= "+total;
                    }
                }
            }else if(cd.length==1){
                int sides = Integer.parseInt(cd[0].replaceAll("d",""));
                if(sides>100){
                    ret = ("A die that big wouldn't even be balanced, "+m.getAuthor().getMentionTag()+" try again (x <= 100).");
                    return ret;
                }
                // {"d20"}
                text += roll(sides);
            }else{
                // {"d20","+","5","adv"}
                int sides = Integer.parseInt(cd[0].replaceAll("d",""));
                int mod = 0;
                if(cd.length>2){
                    switch(cd[1]){
                        case "+" :mod = Integer.parseInt(cd[2]);break;
                        case "-" :mod = Integer.parseInt(cd[2])*(-1);break;
                    }
                }
                if(cd[cd.length-1].contains("adv")){
                    int[] roll = new int[]{roll(sides),roll(sides)};
                    text+=" "+(roll[0]>roll[1]?"**"+roll[0]+"**":roll[0])+" | "+(roll[0]<roll[1]?"**"+roll[1]+"**":roll[1])+(mod!=0?" + "+mod:"")+" = "+(roll[0]>=roll[1]?roll[0]+mod:roll[1]+mod);
                }else if(cd[cd.length-1].contains("dis")){
                    int[] roll = new int[]{roll(sides),roll(sides)};
                    text+=" "+(roll[0]<roll[1]?"**"+roll[0]+"**":roll[0])+" | "+(roll[0]>roll[1]?"**"+roll[1]+"**":roll[1])+(mod!=0?" + "+mod:"")+" = "+(roll[0]<=roll[1]?roll[0]+mod:roll[1]+mod);
                }else {
                    int troll = roll(sides);
                    if(mod!=0)
                        text += " "+troll+" + "+mod+" = "+(troll+mod);
                    else
                        text += " "+troll;
                }
            }
            ret += (text);
        }catch(Exception e){
            ret += ("Please format your command as such:\n```&roll NdN\n&roll dN +/- x\n&roll dN +/- x adv/dis\n&roll dN adv/dis```");
        }
        return ret;
    }
    
    private static int roll(int sides){
        int ret = 0;
        ret = (int)(Math.random()*sides);
        return ret+1;
    }
    
}
/*
private void rollCommand(Message m){
        try{
            String cmd = m.getContent().replaceAll("&roll", "").trim();
            // "10d20 + 5"
            // "5d20 - 9"
            // "2d20"
            // "d20"
            // "d20 + 5 adv"
            String[] cd = cmd.split(" ");
            // {"10d20"}
            // {"5d20"}
            // {"2d20"}
            // {"d20","+","5","adv"}
            String text = "The results "+m.getAuthor().getMentionTag()+" has requested: ";
            String[] die=null;
            if(cd[0].charAt(0)!='d')
                die = cd[0].split("d");
            if(die!=null){
                int numdie = Integer.parseInt(die[0]);
                int sides = Integer.parseInt(die[1]);
                if(numdie>250){
                    m.reply("I seriously doubt you can hold that many dice, "+m.getAuthor().getMentionTag()+" try again with fewer (x <= 250).");
                    return;
                }
                if(sides>100){
                    m.reply("A die that big wouldn't even be balanced, "+m.getAuthor().getMentionTag()+" try again (x <= 100).");
                    return;
                }
                int[] rolls = new int[numdie];
                for(int x=0;x<numdie;x++)
                    rolls[x] = roll(sides);

                for(int x=0;x<rolls.length;x++){
                    if(sides==20)
                        switch(rolls[x]){
                            case 1:text+= "__"+rolls[x]+"__ ";break;
                            case 20:text+= "***"+rolls[x]+"*** ";break;
                            default:text+= rolls[x]+" ";
                        }
                    else
                        text+=rolls[x]+" ";
                    if((x+1)!=rolls.length)
                        text+="+ ";
                    else{
                        int total = 0;
                        for(int i:rolls)
                            total+=i;
                        text+= "= "+total;
                    }
                }
            }else if(cd.length==1){
                int sides = Integer.parseInt(cd[0].replaceAll("d",""));
                if(sides>100){
                    m.reply("A die that big wouldn't even be balanced, "+m.getAuthor().getMentionTag()+" try again (x <= 100).");
                    return;
                }
                // {"d20"}
                text += roll(sides);
            }else{
                // {"d20","+","5","adv"}
                int sides = Integer.parseInt(cd[0].replaceAll("d",""));
                int mod = 0;
                if(cd.length>2){
                    switch(cd[1]){
                        case "+" :mod = Integer.parseInt(cd[2]);break;
                        case "-" :mod = Integer.parseInt(cd[2])*(-1);break;
                    }
                }
                if(cd[cd.length-1].contains("adv")){
                    int[] roll = new int[]{roll(sides),roll(sides)};
                    text+=" "+(roll[0]>roll[1]?"**"+roll[0]+"**":roll[0])+" | "+(roll[0]<roll[1]?"**"+roll[1]+"**":roll[1])+(mod!=0?" + "+mod:"")+" = "+(roll[0]>=roll[1]?roll[0]+mod:roll[1]+mod);
                }else if(cd[cd.length-1].contains("dis")){
                    int[] roll = new int[]{roll(sides),roll(sides)};
                    text+=" "+(roll[0]<roll[1]?"**"+roll[0]+"**":roll[0])+" | "+(roll[0]>roll[1]?"**"+roll[1]+"**":roll[1])+(mod!=0?" + "+mod:"")+" = "+(roll[0]<=roll[1]?roll[0]+mod:roll[1]+mod);
                }else {
                    int troll = roll(sides);
                    if(mod!=0)
                        text += " "+troll+" + "+mod+" = "+(troll+mod);
                    else
                        text += " "+troll;
                }
            }
            m.reply(text);
        }catch(Exception e){
            m.reply("Please format your command as such:\n```&roll NdN\n&roll dN +/- x\n&roll dN +/- x adv/dis\n&roll dN adv/dis```");
        }
    }
    
    private int roll(int sides){
        int ret = 0;
        ret = (int)(Math.random()*sides);
        return ret+1;
    }
*/