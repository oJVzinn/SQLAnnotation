import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "role")
@Getter
@Setter
public class Role {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer priority;
}
