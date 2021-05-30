package com.springland365.ReactDemo;

import com.springland365.ReactDemo.handler.ProductHandler;
import com.springland365.ReactDemo.model.Product;
import com.springland365.ReactDemo.repository.IProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ReactDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactDemoApplication.class, args);
	}


	@Bean
	CommandLineRunner  init(ReactiveMongoOperations operations , IProductRepository  productRepository)
	{
		return args-> {
			Flux<Product>	productFlux = Flux.just(
					new Product(null , "Big Latte" , 2.99),
					new Product(null , "Big Decaf" , 2.49),
					new Product(null , "Gree Tea" , 1.49)

			)
			//.flatMap(productRepository::save)
			.flatMap(p-> productRepository.save(p))		;

			productFlux.thenMany(productRepository.findAll())
						.subscribe(System.out::println);



			/*
			operations.collectionExists(Product.class)
					.flatMap(exists ->
							exists? operations.dropCollection(Product.class): Mono.just(exists))
					.thenMany( v-> operations.createCollection(Product.class))
					.thenMany(productFlux)
					.thenMany(productRepository.findAll())
					.subscribe(System.out::println);
			*/
		};

	}

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler)
	{
		return route(GET("/handler/products").and(accept(APPLICATION_JSON)), handler::getAllProducts)
				.andRoute(POST("/handler/products").and(accept(APPLICATION_JSON)) , handler::saveProduct	)
				.andRoute(DELETE("/handler/products").and(accept(APPLICATION_JSON)) , handler::deleteAll	)
				.andRoute(GET("/handler/products/events").and(accept(TEXT_EVENT_STREAM)) , handler::getProductEvents)
				.andRoute(GET("/handler/products/{id}").and(accept(APPLICATION_JSON)) , handler::getProduct)
				.andRoute(PUT("/handler/products/{id}").and(accept(APPLICATION_JSON)) , handler::updateProduct)
				.andRoute(DELETE("/handler/products/{id}").and(accept(APPLICATION_JSON)) , handler::deleteProduct)
				;
	}


	public RouterFunction<ServerResponse> nestedRoutes(ProductHandler handler)
	{
		return nest(path("/handler/products"),
				nest(
						accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
						route(GET("/") , handler::getAllProducts)
						.andRoute(method(HttpMethod.POST) , handler::saveProduct)
						.andRoute(DELETE("/") , handler::deleteAll)
						.andRoute(GET("/events") , handler::getProductEvents)
						.andNest(path("/{id}") ,
								route(method(HttpMethod.GET) , handler::getProduct))
								.andRoute(method(HttpMethod.PUT) , handler::updateProduct)
								.andRoute(method(HttpMethod.DELETE) , handler::deleteProduct)

				)
		);
	}
}
