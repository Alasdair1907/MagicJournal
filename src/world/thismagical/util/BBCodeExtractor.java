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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BBCodeExtractor {

    public static String REGEX_IMG_ID = "\\[\\s*img\\s*id\\s*\\=\\s*([0-9]+)\\s*\\]"; // \[\s*img\s*id\s*\=\s*([0-9]+)\s*\]

    private final static Pattern regexImgId = Pattern.compile(REGEX_IMG_ID);

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

        BBCodeData bbCodeData = new BBCodeData();
        bbCodeData.imgIds = imgIds;
        bbCodeData.imgExpressions = imgExpressions;

        return bbCodeData;
    }

    public static String preprocess(String text, Session session){

        BBCodeData bbCodeData = parse(text);

        text = preprocessImageData(text, bbCodeData, session);

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
                    imageId, imageFileEntity.getPreviewFileName(), imageFileEntity.getFileName(), imageFileEntity.getTitle());

            text = text.replaceAll(Pattern.quote(bbCodeData.imgExpressions.get(i)), imageHtml);
        }

        return text;

    }


    public static class BBCodeData {
        public List<Long> imgIds;
        public List<String> imgExpressions;
    }
}
