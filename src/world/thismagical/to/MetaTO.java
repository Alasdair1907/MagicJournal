package world.thismagical.to;
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

import world.thismagical.entity.TagEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;
import world.thismagical.vo.*;

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

    public static MetaTO fromPostVO(PostVO postVO, String websiteURL){
        MetaTO metaTO = new MetaTO();

        metaTO.description = postVO.getTitle();
        metaTO.author = postVO.getAuthorVO().displayName;
        if (postVO.getTagEntityList() != null){
            metaTO.keywords = postVO.getTagEntityList().stream().map(TagEntity::getTag).collect(Collectors.joining(" "));
        }

        metaTO.ogTitle = postVO.getPostAttributionStr() + ": " + postVO.getTitle();
        metaTO.ogDescription = postVO.getTinyDescription();

        metaTO.websiteUrl = Tools.normalizeURL(websiteURL);

        ImageVO imageVO = postVO.getMainImageVO();
        if (imageVO != null){
            metaTO.ogImage = metaTO.websiteUrl + "/getImage.jsp?filename=" + imageVO.thumbnail;
        }

        metaTO.ogUrl = metaTO.websiteUrl + "/posts.jsp?" + postVO.getPostAttributionStr().toLowerCase() + "=" + postVO.getId();


        return metaTO;
    }

}
