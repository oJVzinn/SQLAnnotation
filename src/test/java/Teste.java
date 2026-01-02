import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.enums.OrderType;
import com.github.ojvzinn.sqlannotation.model.MySQLModel;
import com.github.ojvzinn.sqlannotation.model.OrderModel;
import com.github.ojvzinn.sqlannotation.model.SQLConfigModel;
import org.junit.Test;

public class Teste {

    private final UserRepository repository = SQLAnnotation.loadRepository(UserRepository.class);
    private final RoleRepository roleRepository = SQLAnnotation.loadRepository(RoleRepository.class);
    private final DepartmentRepository departmentRepository = SQLAnnotation.loadRepository(DepartmentRepository.class);

    @Test
    public void main() {
        MySQLModel mySQL = new MySQLModel("localhost", 3306, "server", "user", "admin");
        SQLConfigModel config = new SQLConfigModel(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
        SQLAnnotation.scanEntity(User.class);
        SQLAnnotation.scanEntity(Role.class);
        SQLAnnotation.scanEntity(Department.class);

        User user = repository.findByKey(1);
        System.out.println("O usuário " + user.getName() + " possui cargo: " + user.getRoleID().getName() + " está no departamento: " + user.getDepartmentID().getName());


    }

}
