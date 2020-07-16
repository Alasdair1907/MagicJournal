package world.thismagical.entity;

import world.thismagical.util.PostAttribution;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tags")
public class TagEntity implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tag_name")
    private String tag;

    @Column(name="attribution_class")
    private Short attributionClass;

    @Column(name="parent_object_id")
    private Long parentObjectId;

    @Column(name="post_index_item_id")
    private Long postIndexItemId;

    @Column(name="parent_has_geo")
    private Boolean parentHasGeo;

    @Column(name="parent_is_published")
    private Boolean parentIsPublished;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public PostAttribution getAttributionClass() {
        return PostAttribution.getPostAttribution(this.attributionClass);
    }

    public void setAttributionClass(PostAttribution attributionClass){
        this.attributionClass = attributionClass.getId();
    }

    public Long getPostIndexItemId() {
        return postIndexItemId;
    }

    public void setPostIndexItemId(Long postIndexItemId) {
        this.postIndexItemId = postIndexItemId;
    }

    public Boolean getParentHasGeo() {
        return parentHasGeo;
    }

    public void setParentHasGeo(Boolean parentHasGeo) {
        this.parentHasGeo = parentHasGeo;
    }

    public Boolean getParentIsPublished() {
        return parentIsPublished;
    }

    public void setParentIsPublished(Boolean parentIsPublished) {
        this.parentIsPublished = parentIsPublished;
    }
}
