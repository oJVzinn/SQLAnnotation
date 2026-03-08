package user;

import com.github.ojvzinn.sqlannotation.annotations.*;
import department.Department;
import lombok.*;
import role.Role;

@Entity(name = "user")
@Getter
@ToString
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class User {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column(notNull = true)
    @NonNull
    private String name;

    @Column(notNull = true)
    @NonNull
    private Integer age;

    @Column(notNull = true)
    @NonNull
    private String email;

    @Varchar(length = 1)
    @Column(notNull = true)
    @NonNull
    private String gender;

    @Column
    @Join(column = "id")
    @NonNull
    private Role roleID;

    @Column
    @Join(column = "id")
    @NonNull
    private Department departmentID;

}
