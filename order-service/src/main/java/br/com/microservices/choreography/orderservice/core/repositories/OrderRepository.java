package br.com.microservices.choreography.orderservice.core.repositories;

import br.com.microservices.choreography.orderservice.core.document.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    /*passamos <classe, tipo do id> só com isso ele já implementa
    * finb by id, find all e todo crud*/
}
