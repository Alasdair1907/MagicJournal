package world.thismagical.vo;
/*
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

import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.TagEntity;
import world.thismagical.util.Tools;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleVO {
    public Long id;
    public String title;
    public String description;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public String gpsCoordinates;
    public Boolean published;

    public String articleText;
    public ImageVO titleImageVO;

    public List<TagEntity> tagEntityList;

    public ArticleVO(ArticleEntity articleEntity){
        this.id = articleEntity.getId();
        this.title = articleEntity.getTitle();
        this.description = articleEntity.getDescription();
        this.authorVO = new AuthorVO(articleEntity.getAuthor());
        this.creationDate = articleEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(articleEntity.getCreationDate());
        this.gpsCoordinates = articleEntity.getGpsCoordinates();
        this.published = articleEntity.getPublished();
        this.articleText = articleEntity.getArticleText();
    }

}
