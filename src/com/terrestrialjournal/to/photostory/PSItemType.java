package com.terrestrialjournal.to.photostory;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PSItemType {
    @JsonProperty("PS_ITEM_TITLE")
    PS_ITEM_TITLE,
    @JsonProperty("PS_ITEM_IMAGE")
    PS_ITEM_IMAGE,
    @JsonProperty("PS_ITEM_TEXT")
    PS_ITEM_TEXT;

}
