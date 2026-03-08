package produt;

import com.github.ojvzinn.sqlannotation.annotations.Column;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import lombok.*;

@Entity(name = "product")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @PrimaryKey(autoIncrement = true)
    @Column
    private Long id;

    @Column(notNull = true)
    @NonNull
    private String name;

    @Column(notNull = true)
    @NonNull
    private Double price;

}
