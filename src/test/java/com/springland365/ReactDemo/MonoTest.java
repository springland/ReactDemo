package com.springland365.ReactDemo;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest{

    @Test
    public void firstMono()
    {
        Mono.just("A")
            .log()
            .subscribe();
    }

    @Test
    public void monoWithConsumer()
    {
        Mono.just("A")
                .log()
                .subscribe( t -> System.out.println(t));
    }

    @Test
    public void monoWithDoOn()
    {
        Mono.just("A")
                .log()
                .doOnSubscribe( subs-> System.out.println("Subscribed: "+ subs))
                .doOnRequest(request -> System.out.println("Request: " + request))
                .doOnSuccess(complete -> System.out.println("Complete: " + complete))
                .subscribe(System.out::println);
    }

    @Test
    public void emptyMono()
    {
        Mono.empty()
                .log()
                .subscribe(System.out::println);
    }

    @Test
    public void emptyCompleteConsumerMono()
    {
       Mono.empty()
            .log()
            .subscribe(System.out::println,
                    null,
                    ()-> System.out.println("Done"));
    }


    @Test
    public void errorRunTimeExceptionMono()
    {
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    @Test
    public void errorExceptionMono()
    {
        Mono.error(new Exception())
                .log()
                .subscribe();
    }

    @Test
    public void errorConsumerException()
    {
        Mono.error(new Exception())
                .log()
                .subscribe(System.out::println,
                        e-> System.err.println("Error " + e));
    }

    @Test
    public void doOnError()
    {
        Mono.error(new Exception())
                .doOnError(( e-> System.out.println("Error :" + e)))
                .log()
                .subscribe();
    }

    @Test
    public void errorOnErrorResumeMono()
    {
        Mono.error(new Exception())
                .onErrorResume( e-> {
                    System.out.println("Caught " + e);
                    e.printStackTrace();
                    return Mono.just("B");
                })
                .log()
                .subscribe();
    }

    @Test
    public void errorOnErrorReturnMono()
    {
        Mono.error(new Exception())
                .onErrorReturn("B")
                .log()
                .subscribe();
    }

}
