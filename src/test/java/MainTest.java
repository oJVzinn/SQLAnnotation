import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.MySQLModel;
import com.github.ojvzinn.sqlannotation.model.SQLConfigModel;
import department.Department;
import department.DepartmentRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import produt.Product;
import produt.ProductRepository;
import role.Role;
import role.RoleRepository;
import user.User;
import user.UserRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainTest {

    private final UserRepository userRepository = SQLAnnotation.loadRepository(UserRepository.class);
    private final RoleRepository roleRepository = SQLAnnotation.loadRepository(RoleRepository.class);
    private final DepartmentRepository departmentRepository = SQLAnnotation.loadRepository(DepartmentRepository.class);
    private final ProductRepository productRepository = SQLAnnotation.loadRepository(ProductRepository.class);

    @Test
    @Order(1)
    public void initializeSQLAnnotation() {
        MySQLModel mySQL = new MySQLModel("localhost", 3306, "teste", "root", "admin");
        SQLConfigModel config = new SQLConfigModel(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
    }

    @Test
    @Order(2)
    public void loadTables() {
        SQLAnnotation.scanEntity(User.class);
        SQLAnnotation.scanEntity(Role.class);
        SQLAnnotation.scanEntity(Department.class);
        SQLAnnotation.scanEntity(Product.class);
    }

    @Test
    @Order(3)
    public void clearTables() {
        SQLAnnotation.truncate(User.class);
        SQLAnnotation.truncate(Role.class);
        SQLAnnotation.truncate(Department.class);
        SQLAnnotation.truncate(Product.class);
    }

    @Test
    @Order(4)
    public void populateDepartment() {
        departmentRepository.save(new Department("Diretoria"));
        departmentRepository.save(new Department("RH"));
        departmentRepository.save(new Department("Vendas"));
    }

    @Test
    @Order(5)
    public void populateRole() {
        roleRepository.save(new Role("Diretor", 1));
        roleRepository.save(new Role("RH", 2));
        roleRepository.save(new Role("Gerente", 3));
        roleRepository.save(new Role("Vendedor", 4));
    }

    @Test
    @Order(6)
    public void populateUser() {
        userRepository.save(
                new User("Rodrigo", 37, "rodrigo@gmail.com", "M", roleRepository.findByKey(1), departmentRepository.findByKey(1)));
        userRepository.save(
                new User("Sabrino", 27, "sabrino@gmail.com", "M", roleRepository.findByKey(2), departmentRepository.findByKey(2)));
        userRepository.save(
                new User("Renata", 30, "renata@gmail.com", "F", roleRepository.findByKey(3), departmentRepository.findByKey(3)));
        userRepository.save(
                new User("Caio", 22, "caio@gmail.com", "M", roleRepository.findByKey(4), departmentRepository.findByKey(3)));
        userRepository.save(
                new User("Sabria", 20, "sabrina@gmail.com", "F", roleRepository.findByKey(4), departmentRepository.findByKey(3)));
    }

    @Test
    @Order(7)
    public void populateProduct() {
        productRepository.save(new Product("Ralador de Queijo", 30.00));
    }

    @Test
    @Order(8)
    public void testJoin() {
        User user = userRepository.findByKey(2);
        System.out.println("O usuário " + user.getName() + " possui o cargo " + user.getRoleID().getName() + " e está no departamento " + user.getDepartmentID().getName());
    }

    @Test
    @Order(9)
    public void testSelectNormal() {
        System.out.println("Pelo preço de 30.00, temos o produto: " + productRepository.findByPrice(30.00).getName());
    }

}
