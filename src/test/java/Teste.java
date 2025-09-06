import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.entity.MySQLEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import org.json.JSONArray;
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

        ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.AND);
        conditional.appendConditional("age", 12).appendConditional("gender", "M");
        JSONArray users = repository.findAllByConditionals(conditional);
        System.out.println(users);

        users = repository.findAll();
        System.out.println(users);

        users = repository.findAllByConditionalsAgeAndName(25, "Yan");
        System.out.println(users);
    }

}
