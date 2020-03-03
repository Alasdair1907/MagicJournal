package world.thismagical.entity;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "test_table")
public class TestEntity implements Serializable {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "test_text")
    private String text;
}
