package com.terrestrialjournal.to.photostory;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PSImageTO implements PSItemTO {
    public Integer order;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public PSItemType itemType;

    public String caption;

    public String thumbnailFile;

    public String previewFile;
    public String mainFile;

    public Long imageID;

    // for javascript
    public Boolean isImage = true;

    @Override
    public Integer getOrder() {
        return order;
    }


    @Override
    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public PSItemType getItemType() {
        return itemType;
    }

    @Override
    public void setItemType(PSItemType itemType) {
        this.itemType = itemType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(String thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    public Long getImageID() {
        return imageID;
    }

    public void setImageID(Long imageID) {
        this.imageID = imageID;
    }

    public String getPreviewFile() {
        return previewFile;
    }

    public void setPreviewFile(String previewFile) {
        this.previewFile = previewFile;
    }

    public String getMainFile() {
        return mainFile;
    }

    public void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }
}
