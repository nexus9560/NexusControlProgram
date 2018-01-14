/*
meta || property=`og:title` || content=`Varia Suit maintenance by P-H | Metroid` || / || 
meta || property=`og:site_name` || content=`Know Your Meme` || / || 
meta || property=`og:url` || content=`http://knowyourmeme.com/photos/1312003-metroid` || / || 
a || href=`http://i0.kym-cdn.com/photos/images/original/001/312/003/314.jpg` || class=`magnify` || target=`_blank` || title=`Varia Suit maintenance by P-H`img || alt=`Varia Suit maintenance by P-H` || class=`centered_photo` || data-src=`http://i0.kym-cdn.com/photos/images/newsfeed/001/312/003/314.jpg` || height=`955` || src=`http://a.kym-cdn.com/assets/blank-b3f96f160b75b1b49b426754ba188fe8.gif` || width=`692` || //a || 
meta || name=`description` || content=`Post with 6076 votes and 189559 views. Tagged with art, creative, raichiyo; Shared by Twinsdude. Raichiyo33 Artwork` || / || 
link || rel=`image_src` || href=`https://i.imgur.com/TM1SYq9.jpg`/ || 
*/
package com.reed.corporation.nexuscontrolprogram;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PageScraper {
    public static String[][] scrapeContent(String uri){
        if(uri.contains("knowyourmeme"))
            return KYM(uri);
        else if(uri.contains("imgur"))
            return Imgur(uri);
        else return AllElse(uri);
    }
    
    private static String[][] KYM(String uri){
        URL res;
        try{
            res= new URL(uri);
            Scanner netScan = new Scanner(res.openStream());
            ArrayList<String> metas = new ArrayList<>();
            while(netScan.hasNextLine()){
                String t = netScan.nextLine().replaceAll("[*<>]", "").replaceAll("[*\"\']", "`").trim();
                if(t.contains("a href=`http://i0.kym-")||(t.contains("meta property")&&(t.contains("title")||t.contains("site_name")||t.contains("og:image")||t.contains("og:url"))))
                    metas.add(t.replaceAll("[*<>]", "").trim());
            }
            String[][] modicum = new String[metas.size()][0];
            for(int x=0;x<modicum.length;x++)
                modicum[x] = spaceOutside(metas.get(x)).split("Ω");
            return modicum;
        }catch(Exception e){
            return null;
        }
    }
    
    private static String[][] Imgur(String uri){
        URL res;
        try{
            res= new URL(uri);
            Scanner netScan = new Scanner(res.openStream());
            ArrayList<String> metas = new ArrayList<>();
            while(netScan.hasNextLine()){
                String t = netScan.nextLine().replaceAll("[*<>]", "").replaceAll("[*\"\']", "`").trim();
                t = t.replaceAll("            ", " ").replaceAll("`/ ","`");
                if(t.contains("e=`description"))
                    metas.add(t.trim());
                else if(t.contains("link rel=`image_src`"))
                    metas.add(t.trim());
                else if(t.contains("gifUrl:"))
                    metas.add(t.trim());
                else if(t.contains("og:title")&&t.contains("content="))
                    metas.add(t.trim());
            }
            String[][] modicum = new String[metas.size()][0];
            for(int x=0;x<modicum.length;x++)
                modicum[x] = spaceOutside(metas.get(x)).split("Ω");
            return modicum;
        }catch(Exception e){
            return null;
        }
    }
    
    private static String[][] AllElse(String uri){
        return new String[][]{{uri}};
    }
    
    private static String spaceOutside(String s){
        char[] charSet = s.replaceAll("[\"*\']","`").toCharArray();
        boolean inQuotes = false;
        for(int x=0;x<charSet.length;x++){
            //System.out.println(inQuotes&&(charSet[x]=='\''||charSet[x]=='\"'));
            if(charSet[x]=='`')
                inQuotes = !inQuotes;
            if(!inQuotes)
                if(charSet[x]==' ')
                    charSet[x]='Ω';
        }
        return new String(charSet);
    }
}
