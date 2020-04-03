package world.thismagical.entity;
/*
  User: Alasdair
  Date: 12/29/2019
  Time: 7:37 PM                                                                                                    
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

import world.thismagical.util.PostAttribution;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="image_files")
public class ImageFileEntity implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_name")
    private String fileName;

    @Column(name="thumbnail_file_name")
    private String thumbnailFileName;

    @Column(name="preview_file_name")
    private String previewFileName;

    @Column(name="original_file_name")
    private String originalFileName;

    @Column(name = "image_attribution_class")
    private Short imageAttributionClass;

    @Column(name="parent_object_id")
    private Long parentObjectId;

    @Column(name="title")
    private String title;

    @Column(name="gps_coordinates")
    private String gpsCoordinates;

    @Column(name="order_num")
    private Long orderNumber;

    public ImageFileEntity(ImageFileEntity imageFileEntity){
        this.imageAttributionClass = imageFileEntity.getImageAttributionClass().getId();
        this.parentObjectId = imageFileEntity.getParentObjectId();
    }

    public ImageFileEntity(){

    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public void setThumbnailFileName(String thumbnailFileName) {
        this.thumbnailFileName = thumbnailFileName;
    }

    public String getPreviewFileName() {
        return previewFileName;
    }

    public void setPreviewFileName(String previewFileName) {
        this.previewFileName = previewFileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public PostAttribution getImageAttributionClass() {
        return PostAttribution.getPostAttribution(this.imageAttributionClass);
    }

    public void setImageAttributionClass(PostAttribution imageAttribution) {
        this.imageAttributionClass = imageAttribution.getId();
    }

    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(String gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
}
