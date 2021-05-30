package com.springland365.ReactDemo;

import com.springland365.ReactDemo.model.Product;
import com.springland365.ReactDemo.model.ProductEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientAPI {

    private WebClient   webClient ;

    public WebClientAPI()
    {
        webClient = WebClient.create("http://localhost:8080/products");
        //webClient = WebClient.builder()
         //       .baseUrl("http://localhost:8080/products")
         //       .build();
    }

    private Mono<ResponseEntity<Product>>       postNewProeduct()
    {
        return webClient.post()
                .uri("/")
                .body(Mono.just(new Product(null , "Black Tea"  , 1.99)) , Product.class)
                .exchange()
                .flatMap(response -> response.toEntity(Product.class))
                .doOnSuccess( o -> System.out.println("************POST " + o));
    }

    private Flux<Product>   getAllProducts()
    {
        return webClient.get()
                .uri("/")
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext( o -> System.out.println("*************GET :" + o));
    }

    private Mono<Product> updateProduct(String id , String name , double price)
    {
        return webClient.put()
                .uri("/{id}" , id)
                .body(Mono.just(new Product(null , name , price) ), Product.class)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnSuccess( o -> System.out.println("******** UPDATE :" +o));

    }

    private Mono<Void> deleteProduct(String id)
    {
        return webClient.delete()
                .uri("/{id}" , id)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(o -> System.out.println("**************** DELETE :" + o));
    }


    private Flux<ProductEvent> getAllEvents()
    {
        return webClient.get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(ProductEvent.class);


    }


    public static void main(String args[]) throws Exception
    {
        WebClientAPI api = new WebClientAPI();


        api.postNewProeduct()
                .thenMany(api.getAllProducts())
                .take(1)
                .flatMap( t-> api.updateProduct(t.getId() , "White Tea" , 0.99))
                .flatMap(t-> api.deleteProduct(t.getId()))
                .thenMany(api.getAllProducts())
                .thenMany(api.getAllEvents())
                .subscribe(System.out::println);



        //api.getAllProducts()
        //        .log()
        //        .subscribe(System.out::println);

        Thread.sleep(5000);
    }
}
