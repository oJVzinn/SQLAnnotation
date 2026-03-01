import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.Join;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "departament")
@Getter
@Setter
public class Department {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    private String name;

    @Column
    @Join(column = "id")
    private User manager;

}
