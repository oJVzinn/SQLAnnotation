import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.enums.OrderType;
import com.github.ojvzinn.sqlannotation.model.MySQLModel;
import com.github.ojvzinn.sqlannotation.model.OrderModel;
import com.github.ojvzinn.sqlannotation.model.SQLConfigModel;
import org.junit.Test;

public class Teste {

    private final UserRepository repository = SQLAnnotation.loadRepository(UserRepository.class);

    @Test
    public void main() {
        MySQLModel mySQL = new MySQLModel("localhost", 3306, "server", "user", "admin");
        SQLConfigModel config = new SQLConfigModel(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
        SQLAnnotation.scanEntity(User.class);

        repository.findAll(new OrderModel().appendAppendOrder(OrderType.ASC, "name").appendAppendOrder(OrderType.DESC, "age")).forEach(System.out::println);
    }

}
