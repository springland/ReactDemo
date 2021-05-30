package com.springland365.ReactDemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class OperatorTest {

    @Test
    public void testmap()
    {
        Flux.range(1 , 10)
               // .log()
                .map( t-> 2*t)
                .subscribe(System.out::println);
    }

    @Test
    public void testFlatMap()
    {
        Flux.range( 1, 5)
                .flatMap( t -> Flux.range(10*t , 2))
                .subscribe(System.out::println);
    }

    @Test
    public void flatMapMany()
    {
        Mono.just(3)
            .flatMapMany(i -> Flux.range(i , 3))
                .subscribe(System.out::println);
    }


    @Test
    public void concat() throws Exception
    {
        Flux<Integer> oneToFive = Flux.range( 1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> sixToTen = Flux.range( 6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.concat(oneToFive , sixToTen)
                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    public void merge() throws Exception
    {
        Flux<Integer> oneToFive = Flux.range( 1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> sixToTen = Flux.range( 6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.merge(oneToFive , sixToTen)
                .subscribe(System.out::println);

        Thread.sleep(4000);

    }

    @Test
    public void zip() throws Exception
    {
        Flux<Integer> oneToFive = Flux.range( 1, 5)
                .delayElements(Duration.ofMillis(200));

        Flux<Integer> sixToTen = Flux.range( 6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.zip(oneToFive , sixToTen ,
                (item1 , item2) -> item1 + "  " + item2)
                .subscribe(System.out::println);

        Thread.sleep(4000);

    }
}
