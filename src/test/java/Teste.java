import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.MySQLEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import org.junit.Test;

public class Teste {

    @Test
    public void main() {
        MySQLEntity mySQL = new MySQLEntity("localhost", 3306, "server", "root", "");
        SQLConfigEntity config = new SQLConfigEntity(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
        SQLAnnotation.scanTable(User.class);

        User user = SQLAnnotation.findByKey(User.class, 1);
        System.out.println(user.getName());
    }

}
