package com.springland365.ReactDemo;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;


public class FluxTest {


    @Test
    public void firstFlux()
    {
        Flux.just("A" , "B" , "C")
           .log()
           .subscribe();
    }

    @Test
    public void fluxFromIterable()
    {
        Flux.fromIterable(Arrays.asList("A" , "B" , "C"))
                .log()
                .subscribe();
    }

    @Test
    public void fluxFromRange()
    {
        Flux.range(0 , 5)
                .log()
                .subscribe();
    }

    @Test
    public void fluxFromInterval() throws InterruptedException {
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .take(2)
                .subscribe();

        Thread.sleep(5000);
    }


    @Test
    public void fluxRequest() throws Exception
    {
        Flux.range(1 , 5)
                .log()
                .subscribe(
                        null ,
                        null,
                        null,
                        s-> s.request(3)
                );
    }


    @Test
    public void fluxCustomSubscriber() throws Exception
    {
        Flux.range(1 , 10)
                .log()
                .subscribe(
                        new BaseSubscriber<Integer>()
                        {
                            int elementToProcess = 3;
                            int counter = 0 ;

                            public void hookOnSubscribe(Subscription subscription)
                            {
                                System.out.println("Subscribed");
                                request(elementToProcess);
                            }

                            public void hookOnNext(Integer value)
                            {
                                counter ++;
                                if(counter == elementToProcess)
                                {
                                    counter = 0 ;
                                    Random r = new Random();
                                    elementToProcess = r.ints(1 , 4)
                                            .findFirst().getAsInt();
                                    request(elementToProcess);
                                }
                            }
                        }

                );
    }

    @Test
    public void fluxLimitRate()
    {
        Flux.range(1 , 10)
                .log()
                .limitRate(3)
                .subscribe();
    }
}
