package world.thismagical.vo;

import java.time.LocalDateTime;

public interface PostVO {
    String getTitle();
    String getDescription();
    AuthorVO getAuthorVO();
    LocalDateTime getCreationDate();
    String getPostAttributionStr();
    void setPostAttributionStr(String postAttributionStr);
}
