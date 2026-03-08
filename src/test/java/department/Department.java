package department;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.*;

@Entity(name = "department")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Department {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    @NonNull
    private String name;

}
