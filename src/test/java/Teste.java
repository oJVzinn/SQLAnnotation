import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.MySQLEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import org.junit.Test;

public class Teste {

    private final UserRepository repository = SQLAnnotation.loadRepository(UserRepository.class);

    @Test
    public void main() {
        MySQLEntity mySQL = new MySQLEntity("localhost", 3306, "server", "root", "");
        SQLConfigEntity config = new SQLConfigEntity(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
        SQLAnnotation.scanEntity(User.class);

        User user = repository.findByKey(1L);
        if (user == null) {
            System.out.println("RETORNOU NULL");
            return;
        }

        System.out.println(user.toString());
        user.setEmail("joaovictor17082006@gmail.com");
        repository.save(user);

        user = repository.findByKey(1L);
        System.out.println(user.toString());

        repository.save(user);
    }

}
