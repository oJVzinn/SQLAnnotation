import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.MySQLEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import org.junit.Test;

public class Teste {

    @Test
    public void main() {
        MySQLEntity mySQL = new MySQLEntity("localhost", 3306, "server", "root", "");
        SQLConfigEntity config = new SQLConfigEntity(mySQL);
        SQLAnnotation.init(config);
        SQLAnnotation.scanTable(UsersTable.class);
    }

}
