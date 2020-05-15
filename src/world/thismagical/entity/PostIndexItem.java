package world.thismagical.entity;
/*
  User: Alasdair
  Date: 5/13/2020
  Time: 10:41 PM                                                                                                    
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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import world.thismagical.util.PostAttribution;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="posts_index")
public class PostIndexItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @Column(name="post_attribution")
    Short postAttribution;

    @Column(name="post_id")
    Long postId;

    @Column(name="author_login")
    String authorLogin;

    @Column(name="creation_date")
    LocalDateTime creationDate;

    @Column(name="is_published")
    Boolean isPublished;

    /*
    @OneToMany(fetch = FetchType.EAGER)
    @BatchSize(size = 100)
    @JoinColumns({
            @JoinColumn(updatable = false, insertable = false, referencedColumnName = "post_attribution", name = "attribution_class"),
            @JoinColumn(updatable = false, insertable = false, referencedColumnName = "post_id", name = "parent_object_id")
    })
    List<TagEntity> tagEntityList;*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PostAttribution getPostAttribution() {
        return PostAttribution.getPostAttribution(postAttribution);
    }

    public void setPostAttribution(PostAttribution postAttribution) {
        this.postAttribution = postAttribution.getId();
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    /*
    public List<TagEntity> getTagEntityList() {
        return tagEntityList;
    }

    public void setTagEntityList(List<TagEntity> tagEntityList) {
        this.tagEntityList = tagEntityList;
    }*/
}
