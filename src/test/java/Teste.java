import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.MySQLEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Teste {

    @Test
    public void main() {
        MySQLEntity mySQL = new MySQLEntity("localhost", 3306, "server", "root", "");
        SQLConfigEntity config = new SQLConfigEntity(mySQL);
        config.setLog(true);
        SQLAnnotation.init(config);
        SQLAnnotation.scanTable(User.class);

        User user = SQLAnnotation.findByKey(User.class, 1);

        if (user == null) System.out.println("RETORNOU NULO");
        if (user != null) System.out.println(user);
    }

}
