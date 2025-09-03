import com.github.ojvzinn.sqlannotation.interfaces.Repository;

public interface UserRepository extends Repository<User> {

    User findByName(String name);

}
