package com.terrestrialjournal.entity;

import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.vo.PhotostoryVO;
import com.terrestrialjournal.vo.PostVO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "photostories")
public class PhotostoryEntity implements Serializable, PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @Column(name="index_id")
    Long indexId;

    @Column(name="title")
    String title;

    @Column(name="description")
    String description;

    @Column(name="tiny_description")
    String tinyDescription;

    @Column(name="title_image_id")
    Long titleImageId;

    @Column(name="json_content")
    String jsonContent;

    @OneToOne
    @JoinColumn(name="author_id")
    AuthorEntity author;

    @Column(name="creation_date")
    LocalDateTime creationDate;

    @Column(name="gps_coordinates")
    String gpsCoordinates;

    @Column(name="published")
    Boolean published;

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

    public void setAuthor(AuthorEntity authorEntity) {
        this.author = authorEntity;
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


    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public Long getTitleImageId() {
        return titleImageId;
    }

    public void setTitleImageId(Long titleImageId) {
        this.titleImageId = titleImageId;
    }

    public LocalDateTime getLastModifiedDate() {
        if (lastModifiedDate == null){
            return creationDate;
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public PostAttribution getPostAttribution() { return PostAttribution.PHOTOSTORY; }

    public PostVO toBaseVO(){
        return new PhotostoryVO(this);
    }

    public String getPreRender() {
        return preRender;
    }

    public void setPreRender(String preRender) {
        this.preRender = preRender;
    }

}
