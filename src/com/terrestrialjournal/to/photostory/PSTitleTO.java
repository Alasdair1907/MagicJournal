package com.terrestrialjournal.to.photostory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PSTitleTO implements PSItemTO {
    public Integer order;
    public String titleText;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("itemType")
    public PSItemType itemType;

    // for javascript
    public Boolean isTitle = true;

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

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }
}
