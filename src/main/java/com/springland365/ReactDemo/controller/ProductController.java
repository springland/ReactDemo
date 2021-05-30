package com.springland365.ReactDemo.controller;

import com.springland365.ReactDemo.model.Product;
import com.springland365.ReactDemo.model.ProductEvent;
import com.springland365.ReactDemo.repository.IProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/products")
public class ProductController {

    private IProductRepository  repository ;

    public ProductController(IProductRepository repo)
    {
        this.repository = repo;
    }


    @GetMapping
    public Flux<Product> getAllProducts()
    {
        return this.repository.findAll();
    }

    /*
    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable("id") String id)
    {
        return this.repository.findById(id);
    }
    */

    @GetMapping("{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable("id") String id)
    {
        return this.repository.findById(id)
                .map( product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> saveProduct(@RequestBody Product product)
    {
        return repository.save(product);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Product>> udpateProduct(@PathVariable(value = "id") String id ,
                                                       @RequestBody Product product)
    {
        return repository.findById(id)
                .flatMap(
                        existingProduct -> {
                            existingProduct.setPrice(product.getPrice());
                            existingProduct.setName(product.getName());
                            return repository.save(existingProduct);
                        }
                ).map( udpateProduct -> ResponseEntity.ok(udpateProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable(value = "id") String id )
    {
        return repository.findById(id)
                .flatMap( t -> this.repository.delete(t)
                                .then(Mono.just(ResponseEntity.ok().<Void>build())))
                .defaultIfEmpty( ResponseEntity.notFound().build());
    }

    @DeleteMapping()
    public Mono<Void>  deleteAll()
    {
        return this.repository.deleteAll() ;
    }


    @GetMapping(value="/events" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductEvent> getProductEvents()
    {
        return Flux.interval(Duration.ofSeconds(1))
                .map( val -> new ProductEvent(val, "Product Event"));
    }
}
