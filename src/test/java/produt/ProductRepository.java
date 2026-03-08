package produt;

import com.github.ojvzinn.sqlannotation.interfaces.Repository;

public interface ProductRepository extends Repository<Product> {

    Product findByPrice(Double price);

}
