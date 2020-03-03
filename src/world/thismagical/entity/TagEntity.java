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
        return PostAttribution.getImageAttribution(this.attributionClass);
    }

    public void setAttributionClass(PostAttribution attributionClass){
        this.attributionClass = attributionClass.getId();
    }
}
