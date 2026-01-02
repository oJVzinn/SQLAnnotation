import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "departament")
@Getter
@Setter
public class Department {

    @PrimaryKey(autoIncrement = true)
    @Column
    private Long id;

    @Column
    private String name;

}
