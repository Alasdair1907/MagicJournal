package com.terrestrialjournal.to;
/*
  User: Alasdair
  Date: 7/9/2020
  Time: 10:31 PM                                                                                                    
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

import com.terrestrialjournal.entity.TagEntity;
import com.terrestrialjournal.util.Tools;
import com.terrestrialjournal.vo.*;

import java.util.stream.Collectors;

public class MetaTO {

    /* <meta name="description" content="blah"> */
    public String description;
    public String keywords; // e.g. "Free web tutorials"
    public String author;

    /* <meta property="og:title" content="blah"> */
    public String ogImage;
    public String ogTitle;
    public String ogDescription;
    public String ogUrl;

    public String twitterHandle; //@someone
    public String twitterImage;
    public String twitterDescription;
    public String twitterTitle;


    public String websiteUrl; // http://www.example.org

    public String getDescription() {
        return Tools.htmlSingleQuote(description);
    }

    public String getKeywords() {
        return Tools.htmlSingleQuote(keywords);
    }

    public String getAuthor() {
        return Tools.htmlSingleQuote(author);
    }

    public String getOgImage() {
        return ogImage;
    }

    public String getOgTitle() {
        return Tools.htmlSingleQuote(ogTitle);
    }

    public String getOgDescription() {
        return Tools.htmlSingleQuote(ogDescription);
    }

    public String getOgUrl() {
        return ogUrl;
    }

    public String getTwitterHandle() {
        return Tools.htmlSingleQuote(twitterHandle);
    }

    public String getTwitterImage() {
        return twitterImage;
    }

    public String getTwitterDescription() {
        return Tools.htmlSingleQuote(twitterDescription);
    }

    public String getTwitterTitle() {
        return Tools.htmlSingleQuote(twitterTitle);
    }

    public static MetaTO fromPostVO(PostVO postVO, SettingsTO settingsTO){
        MetaTO metaTO = new MetaTO();

        metaTO.description = postVO.getTitle();
        metaTO.author = postVO.getAuthorVO().displayName;
        if (postVO.getTagEntityList() != null){
            metaTO.keywords = postVO.getTagEntityList().stream().map(TagEntity::getTag).collect(Collectors.joining(" "));
        }

        metaTO.ogTitle = postVO.getPostAttributionStr() + ": " + postVO.getTitle();
        metaTO.ogDescription = postVO.getTinyDescription();

        metaTO.websiteUrl = Tools.normalizeURL(settingsTO.websiteURL);

        ImageVO imageVO = postVO.getMainImageVO();
        if (imageVO != null){
            metaTO.ogImage = metaTO.websiteUrl + "/getImage.jsp?filename=" + imageVO.thumbnail;
        }

        metaTO.ogUrl = metaTO.websiteUrl + "/posts.jsp?" + postVO.getPostAttributionStr().toLowerCase() + "=" + postVO.getId();

        //twitter cards
        if (settingsTO.twitterProfile != null && !settingsTO.twitterProfile.isBlank()) {
            String twitterHandle = Tools.normalizeURL(settingsTO.twitterProfile);
            String[] tokens = twitterHandle.split("\\/");
            if (tokens != null && tokens.length > 0) {
                twitterHandle = tokens[tokens.length - 1];
            }

            metaTO.twitterHandle = "@" + twitterHandle;
        }

        metaTO.twitterTitle = metaTO.ogTitle;
        String twitterDescription = postVO.getDescription();
        if (twitterDescription != null && twitterDescription.length() > 200){
            twitterDescription = twitterDescription.substring(0,201) + "...";
        }
        metaTO.twitterDescription = twitterDescription;
        metaTO.twitterImage = metaTO.ogImage;


        return metaTO;
    }

}
