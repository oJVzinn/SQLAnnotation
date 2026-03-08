package user;

import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import com.github.ojvzinn.sqlannotation.model.LimitModel;
import org.json.JSONArray;

public interface UserRepository extends Repository<User> {

    User findByName(String name);
    JSONArray findAllByConditionalsAgeAndName(Integer age, String name);
    JSONArray findAll(LimitModel limit);
    void deleteAllByConditionalsAgeAndEmail(Integer age, String email);
    void deleteByAge(Integer age);


}
