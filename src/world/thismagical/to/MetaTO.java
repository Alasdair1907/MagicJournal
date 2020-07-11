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
import world.thismagical.vo.ArticleVO;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.PhotoVO;
import world.thismagical.vo.PostVO;

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

    public static MetaTO fromPostVO(PostVO postVO, String websiteURL){
        MetaTO metaTO = new MetaTO();

        metaTO.description = postVO.getTitle();
        metaTO.author = postVO.getAuthorVO().displayName;
        if (postVO.getTagEntityList() != null){
            metaTO.keywords = postVO.getTagEntityList().stream().map(TagEntity::getTag).collect(Collectors.joining(" "));
        }

        metaTO.ogTitle = postVO.getTitle();
        metaTO.ogDescription = postVO.getTinyDescription();

        if (postVO.getPostAttribution().equals(PostAttribution.ARTICLE.getId())){
            ArticleVO articleVO = (ArticleVO) postVO;
            metaTO.ogImage = Tools.normalizeURL(websiteURL) + "/" + "getImage.jsp?filename=" + articleVO.titleImageVO.thumbnail;
        } else if (postVO.getPostAttribution().equals(PostAttribution.PHOTO.getId())){
            PhotoVO photoVO = (PhotoVO) postVO;
            metaTO.ogImage = Tools.normalizeURL(websiteURL) + "/" + "getImage.jsp?filename=" + photoVO.imageVO.thumbnail;
        } else if (postVO.getPostAttribution().equals(PostAttribution.GALLERY.getId())){
            GalleryVO galleryVO = (GalleryVO) postVO;
            metaTO.ogImage = Tools.normalizeURL(websiteURL) + "/" + "getImage.jsp?filename=" + galleryVO.galleryRepresentation.get(0).thumbnail;
        }

        return metaTO;
    }

}
