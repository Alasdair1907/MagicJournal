package world.thismagical.util;
/*
  User: Alasdair
  Date: 3/27/2020
  Time: 9:23 PM                                                                                                    
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                   
*/

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBCodeExtractor {

    public static String REGEX_IMG_ID = "\\[\\s*img\\s*id\\s*\\=\\s*([0-9]+)\\s*\\]"; // \[\s*img\s*id\s*\=\s*([0-9]+)\s*\]

    private final static Pattern regexImgId = Pattern.compile(REGEX_IMG_ID);

    public static BBCodeData parse(String s){

        // [img id="1234"]
        List<Long> imgIds = null;
        Matcher m = regexImgId.matcher(s);
        if (m.find()){
            imgIds = new ArrayList<>();

            for (int i = 1; i <= m.groupCount(); i++){
                Long id = Long.parseLong(m.group(i));
                imgIds.add(id);
            }
        }

        BBCodeData bbCodeData = new BBCodeData();
        bbCodeData.imgIds = imgIds;

        return bbCodeData;
    }

    public static class BBCodeData {
        public List<Long> imgIds;
    }
}
