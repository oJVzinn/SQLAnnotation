import com.github.ojvzinn.sqlannotation.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "users")
@Getter
@ToString
@Setter
public class User {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column(notNull = true)
    private String name;

    @Column(notNull = true)
    private Integer age;

    @Column(notNull = true)
    private String email;

    @Varchar(length = 1)
    @Column(notNull = true)
    private String gender;

    @Column
    @Join(column = "id")
    private Role roleID;

    @Column
    @Join(column = "id")
    private Department departmentID;

}
