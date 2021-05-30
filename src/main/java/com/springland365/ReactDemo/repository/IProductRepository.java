package com.springland365.ReactDemo.repository;

import com.springland365.ReactDemo.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IProductRepository extends ReactiveMongoRepository<Product, String> {

    Flux<Product>   findByName(String name);
}
