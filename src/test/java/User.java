import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import com.github.ojvzinn.sqlannotation.annotations.Varchar;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "USERS")
@Getter
@ToString
@Setter
public class User {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private String email;

    @Varchar(length = 1)
    @Column(notNull = false)
    private String gender;

}
