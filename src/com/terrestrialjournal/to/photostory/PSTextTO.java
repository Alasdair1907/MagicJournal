package com.terrestrialjournal.to.photostory;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PSTextTO implements PSItemTO {
    public Integer order;

    public String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public PSItemType itemType;

    // for javascript
    public Boolean isText = true;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
