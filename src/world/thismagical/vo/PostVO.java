package world.thismagical.vo;

import world.thismagical.entity.TagEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PostVO {
    String getTitle();
    String getDescription();
    AuthorVO getAuthorVO();
    LocalDateTime getCreationDate();
    String getPostAttributionStr();
    void setPostAttributionStr(String postAttributionStr);
    List<TagEntity> getTagEntityList();
    Short getPostAttribution();
    String getTinyDescription();
    ImageVO getMainImageVO();
    Long getId();
    LocalDateTime getLastModified();
}
