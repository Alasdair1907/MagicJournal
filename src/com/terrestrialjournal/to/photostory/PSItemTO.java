package com.terrestrialjournal.to.photostory;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "itemType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PSTitleTO.class, name = "PS_ITEM_TITLE"),
        @JsonSubTypes.Type(value = PSImageTO.class, name = "PS_ITEM_IMAGE"),
        @JsonSubTypes.Type(value = PSTextTO.class, name = "PS_ITEM_TEXT")
})
public interface PSItemTO {
    Integer getOrder();
    void setOrder(Integer order);

    PSItemType getItemType();
    void setItemType(PSItemType itemType);
}
