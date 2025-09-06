import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import org.json.JSONArray;

public interface UserRepository extends Repository<User> {

    User findByName(String name);
    JSONArray findAllByConditionalsAgeAndName(Integer age, String name);

}
