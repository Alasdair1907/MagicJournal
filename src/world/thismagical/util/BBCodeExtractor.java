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

import org.hibernate.Session;
import world.thismagical.dao.FileDao;
import world.thismagical.entity.ImageFileEntity;
import world.thismagical.entity.OtherFileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBCodeExtractor {

    public static String REGEX_IMG_ID = "\\[\\s*img\\s*id\\s*\\=\\s*([0-9]+)\\s*\\]"; // \[\s*img\s*id\s*\=\s*([0-9]+)\s*\]
    public static String REGEX_FILE_ID = "\\[\\s*file\\s*id\\s*\\=\\s*([0-9]+)\\s*\\]";

    private final static Pattern regexImgId = Pattern.compile(REGEX_IMG_ID);
    private final static Pattern regexFileId = Pattern.compile(REGEX_FILE_ID);

    public static BBCodeData parse(String s){

        // [img id=1234]
        Matcher m = regexImgId.matcher(s);

        List<Long> imgIds = new ArrayList<>();
        List<String> imgExpressions = new ArrayList<>();

        while (m.find()){
            Long id = Long.parseLong(m.group(1));
            String expression = m.group(0);

            imgIds.add(id);
            imgExpressions.add(expression);
        }

        // [file id=1234]
        List<Long> fileIds = new ArrayList<>();
        List<String> fileExpressions = new ArrayList<>();

        m = regexFileId.matcher(s);

        while (m.find()){
            Long id = Long.parseLong(m.group(1));
            String expression = m.group(0);

            fileIds.add(id);
            fileExpressions.add(expression);
        }

        BBCodeData bbCodeData = new BBCodeData();
        bbCodeData.imgIds = imgIds;
        bbCodeData.imgExpressions = imgExpressions;
        bbCodeData.fileIds = fileIds;
        bbCodeData.fileExpressions = fileExpressions;

        return bbCodeData;
    }

    public static String preprocess(String text, Session session){

        /*
         * just handle the stuff that can't be easily and efficiently be handled on the frontend - resolve
         * image and file ids into the necessary data
         */

        text = escapeHtmlElements(text);

        BBCodeData bbCodeData = parse(text);

        text = preprocessImageData(text, bbCodeData, session);
        text = preprocessFileData(text, bbCodeData, session);

        return text;

    }

    private static String preprocessImageData(String text, BBCodeData bbCodeData, Session session){
        if (bbCodeData.imgIds == null || bbCodeData.imgIds.isEmpty()){
            return text;
        }

        List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(bbCodeData.imgIds, session);
        Map<Long, ImageFileEntity> imageFileEntityMap = new HashMap<>();

        if (imageFileEntityList != null) {
            imageFileEntityList.forEach(it -> imageFileEntityMap.put(it.getId(), it));
        }

        for (int i = 0; i < bbCodeData.imgExpressions.size(); i++){
            Long imageId = bbCodeData.imgIds.get(i);
            ImageFileEntity imageFileEntity = imageFileEntityMap.get(imageId);

            String imageHtml = String.format("<div class='article-image-container' data-type='image' data-id='%d' data-preview='%s' data-image='%s' data-title='%s'></div>",
                    imageId, imageFileEntity.getPreviewFileName(), imageFileEntity.getFileName(), escapeSingleQuotes(imageFileEntity.getTitle()));

            text = text.replaceAll(Pattern.quote(bbCodeData.imgExpressions.get(i)), imageHtml);
        }

        return text;

    }

    private static String preprocessFileData(String text, BBCodeData bbCodeData, Session session){
        if (bbCodeData.fileIds == null || bbCodeData.fileIds.isEmpty()){
            return text;
        }

        List<OtherFileEntity> otherFileEntityList = FileDao.getOtherFilesByIds(bbCodeData.fileIds, session);
        Map<Long, OtherFileEntity> otherFileEntityMap = new HashMap<>();

        if (otherFileEntityList != null) {
            otherFileEntityList.forEach(it -> otherFileEntityMap.put(it.getId(), it));
        }

        for (int i = 0; i < bbCodeData.fileExpressions.size(); i++){
            Long fileId = bbCodeData.fileIds.get(i);
            OtherFileEntity otherFileEntity = otherFileEntityMap.get(fileId);

            String fileHtml = String.format("<div class='file-container' data-type='file' data-id='%d' data-displayname='%s' data-description='%s'></div>",
                    fileId, otherFileEntity.getDisplayName(), escapeSingleQuotes(otherFileEntity.getDescription()));

            text = text.replaceAll(Pattern.quote(bbCodeData.fileExpressions.get(i)), fileHtml);
        }

        return text;
    }

    public static String escapeSingleQuotes(String input){
        if (input == null){
            return "";
        }

        return input.replaceAll("'", "&apos;");
    }

    public static String escapeHtmlElements(String text){

        if (text == null){
            return "";
        }

        /*
        we dont want to escape BBCode here!
         */

        Map<String, String> htmlToEscape = new HashMap<>();

        htmlToEscape.put("&","&amp;");
        htmlToEscape.put("<", "&lt;");
        htmlToEscape.put(">", "&gt;");

        for (String orig : htmlToEscape.keySet()){
            String replacement = htmlToEscape.get(orig);
            text = text.replaceAll(Pattern.quote(orig), replacement);
        }

        return text;
    }

    public static class BBCodeData {
        public List<Long> imgIds;
        public List<String> imgExpressions;

        public List<Long> fileIds;
        public List<String> fileExpressions;
    }
}
