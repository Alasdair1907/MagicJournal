package com.terrestrialjournal.entity;
/*
  User: Alasdair
  Date: 1/2/2020
  Time: 5:24 PM                                                                                                    
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

import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.vo.PhotoVO;
import com.terrestrialjournal.vo.PostVO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="photos")
public class PhotoEntity implements Serializable, PostEntity {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="index_id")
    Long indexId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="tiny_description")
    String tinyDescription;

    @JoinColumn(name="author_id")
    @OneToOne
    private AuthorEntity author;

    @Column(name="creation_date")
    private LocalDateTime creationDate;

    @Column(name="gps_coordinates")
    private String gpsCoordinates;

    @Column(name="published")
    private Boolean published;

    @Column(name="lastmod")
    LocalDateTime lastModifiedDate;

    @Column(name="prerender")
    String preRender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIndexId() {
        return indexId;
    }

    public void setIndexId(Long indexId) {
        this.indexId = indexId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;

}
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTinyDescription() {
        return tinyDescription;
    }

    public void setTinyDescription(String tinyDescription) {
        this.tinyDescription = tinyDescription;
    }

    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public LocalDateTime getLastModifiedDate() {
        if (lastModifiedDate == null){
            return creationDate;
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public PostAttribution getPostAttribution() { return PostAttribution.PHOTO; }

    public PostVO toBaseVO(){
        return new PhotoVO(this);
    }

    public String getPreRender() {
        return preRender;
    }

    public void setPreRender(String preRender) {
        this.preRender = preRender;
    }
}
