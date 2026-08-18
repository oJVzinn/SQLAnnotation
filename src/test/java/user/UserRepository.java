package user;

import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import com.github.ojvzinn.sqlannotation.model.LimitModel;

import java.util.List;

public interface UserRepository extends Repository<User> {

    User findByName(String name);
    List<User> findAllByConditionalsAgeAndName(Integer age, String name);
    List<User> findAll(LimitModel limit);
    void deleteAllByConditionalsAgeAndEmail(Integer age, String email);
    void deleteByAge(Integer age);

}
