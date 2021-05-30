package com.springland365.ReactDemo.handler;

import com.springland365.ReactDemo.model.Product;
import com.springland365.ReactDemo.model.ProductEvent;
import com.springland365.ReactDemo.repository.IProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductHandler {
    private IProductRepository repository;

    public ProductHandler(IProductRepository repo)
    {
        this.repository = repo ;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request)
    {
        Flux<Product>   products = this.repository.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(products , Product.class);
    }

    public Mono<ServerResponse> getProduct(ServerRequest request)
    {
        String id = request.pathVariable("id");
        Mono<Product> product = this.repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return product.flatMap(
                p ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromObject(p))
        ).switchIfEmpty(notFound)    ;
    }

    public Mono<ServerResponse>  saveProduct(ServerRequest request)
    {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(
                product -> ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(repository.save(product) , Product.class)
        );
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request)
    {
        String id = request.pathVariable("id");
        Mono<Product> existingPProductMono = this.repository.findById(id);
        Mono<Product> productMono = request.bodyToMono(Product.class);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();


        return productMono.zipWith(existingPProductMono ,
                (product , existingProduct) -> new Product(existingProduct.getId() , product.getName() , product.getPrice()))
                .flatMap(
                        product ->
                            ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(repository.save(product) , Product.class)

                ).switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request)
    {
        String id = request.pathVariable("id");
        Mono<Product> productMono = this.repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono.flatMap(
                p -> ServerResponse.ok()
                        .build(
                                repository.delete(p)
                        )

        ).switchIfEmpty(notFound);

    }

    public Mono<ServerResponse> deleteAll(ServerRequest request)
    {
        return ServerResponse.ok().build(repository.deleteAll());
    }


    public Mono<ServerResponse> getProductEvents(ServerRequest request)
    {
        Flux<ProductEvent>  eventFlux = Flux.interval(Duration.ofSeconds(1))
                                            .map(
                                                    val -> new ProductEvent(val , "Product Event")
                                            );

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventFlux , Product.class);


    }

}
