package role;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.*;

@Entity(name = "role")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Role {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    @NonNull
    private String name;

    @Column
    @NonNull
    private Integer priority;

}
