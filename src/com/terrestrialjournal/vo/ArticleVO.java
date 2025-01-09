package com.terrestrialjournal.vo;
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

import com.terrestrialjournal.entity.ArticleEntity;
import com.terrestrialjournal.entity.TagEntity;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.Tools;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleVO implements PostVO {
    public Long id;
    public Long indexId;
    public String title;
    public String description;
    public String seDescription;
    public String tinyDescription;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public LocalDateTime lastModified;
    public String gpsCoordinates;
    public Boolean published;

    public String postAttributionStr;
    public Short postAttribution;
    public Boolean isArticle;

    public String articleText;
    public ImageVO titleImageVO;

    public List<TagEntity> tagEntityList;

    public String preRender;

    public ArticleVO(ArticleEntity articleEntity){
        this.id = articleEntity.getId();
        this.indexId = articleEntity.getIndexId();
        this.title = articleEntity.getTitle();
        this.description = articleEntity.getDescription();
        this.tinyDescription = articleEntity.getTinyDescription();
        this.authorVO = new AuthorVO(articleEntity.getAuthor());
        this.creationDate = articleEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(articleEntity.getCreationDate());
        this.lastModified = articleEntity.getLastModifiedDate();
        this.gpsCoordinates = articleEntity.getGpsCoordinates();
        this.published = articleEntity.getPublished();
        this.articleText = articleEntity.getArticleText();
        this.postAttributionStr = PostAttribution.ARTICLE.getReadable();
        this.postAttribution = PostAttribution.ARTICLE.getId();
        this.isArticle = true;
        this.preRender = articleEntity.getPreRender();
        this.seDescription = articleEntity.getSEDescription();
    }

    @Override
    public Long getId() { return id; }

    @Override
    public Long getIndexId() { return indexId; }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AuthorVO getAuthorVO() {
        return authorVO;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public String getPostAttributionStr() {
        return postAttributionStr;
    }

    @Override
    public void setPostAttributionStr(String postAttributionStr) {
        this.postAttributionStr = postAttributionStr;
    }

    @Override
    public List<TagEntity> getTagEntityList(){
        return this.tagEntityList;
    }

    @Override
    public Short getPostAttribution() {
        return postAttribution;
    }

    @Override
    public String getTinyDescription() {
        return tinyDescription;
    }

    @Override
    public ImageVO getMainImageVO() {
        return titleImageVO;
    }

    @Override
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public String getPreRender() {
        return preRender;
    }

    @Override
    public void setPreRender(String preRender) {
        this.preRender = preRender;
    }

    public String getSEDescription() {
        return seDescription;
    }

    public void setSEDescription(String seDescription) {
        this.seDescription = seDescription;
    }
}
