/*
    This is my Inara Scraper, the point is to scrape data from inara
 */
package com.reed.corporation.nexuscontrolprogram;

import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class InaraScraper {
    
    public static String whois(String cmdr){
        return whois(new String[]{cmdr,"0"});
    }
    
    public static String whois(String[] c){
        String ret = null;
        try{
            //System.out.*(Arrays.toString(c));
            String tomato = c[0];
            tomato = tomato.replaceAll("whois", "").trim();
            //System.out.*(tomato);
            //System.out.*("\n\n\n\n\n\nI'm gonna start thinking REAAAAAAAAAAALLY hard\n\n");
            URL inaraSearch = new URL("https://inara.cz/search/?location=search&searchglobal="+tomato);
            ////System.out.*("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL\n"+inaraSearch.toString()+"\nLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL\n");
            Scanner k = new Scanner(inaraSearch.openStream());
            String pass = "";
            while(k.hasNextLine())
                pass+=k.nextLine()+"\n";
            pass = pass.replaceAll(">",">\n").replaceAll("[*<=>]","").replaceAll("inverse", "\n");
            //System.out.*("\n\n\n\n"+pass);
            ret = pass;
        }catch(Exception e){
            ret = e.getMessage()+"\n";
            ret+= e.getLocalizedMessage()+"\n";
            ret+= e.toString();
        }
        return ret;
    }
    
}
