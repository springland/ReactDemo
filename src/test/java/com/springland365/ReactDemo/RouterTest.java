package com.springland365.ReactDemo;


import com.springland365.ReactDemo.controller.ProductController;
import com.springland365.ReactDemo.model.Product;
import com.springland365.ReactDemo.model.ProductEvent;
import com.springland365.ReactDemo.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RouterTest {

    private WebTestClient client ;

    private List<Product> expectedList ;

    @Autowired
    private IProductRepository repository;

    @Autowired
    RouterFunction route ;

    @BeforeEach
    public void beforeEach()
    {
        this.client = WebTestClient.bindToRouterFunction(this.route)
                .configureClient()
                .baseUrl("/handler/products")
                .build();

        this.expectedList = this.repository.findAll().collectList().block();
    }

    @Test
    public void testGetAllProducts()
    {
        client.get().uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedList);
    }


    @Test
    public void testProductInvalidIdNotFound()
    {
        client.get()
                .uri("/aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testProductIdFound()
    {
        Product expectedProduct = expectedList.get(0);

        client.get().uri("/" + expectedProduct.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProduct);
    }


    @Test
    public void testProductEvents()
    {
        ProductEvent expectedEvent = new ProductEvent(0L , "Product Event");
        FluxExchangeResult<ProductEvent> result =
                client.get().uri("/events")
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(ProductEvent.class);

        StepVerifier.create(result.getResponseBody() )
                .expectNext(expectedEvent)
                .expectNextCount(2)
                .consumeNextWith(event -> assertEquals(Long.valueOf(3L) , event.getEventId()))
                .thenCancel()
                .verify();
    }

}
