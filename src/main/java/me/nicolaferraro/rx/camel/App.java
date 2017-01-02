package me.nicolaferraro.rx.camel;

import io.reactivex.Observable;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        Main main = new Main();
        main.addRouteBuilder(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("timer:tick?period=2000")
                .setBody().constant("Hello")
                .to("reactive-streams:messages");

            }
        });

        main.addMainListener(new MainListenerSupport() {
            @Override
            public void configure(CamelContext context) {

                Publisher<String> messages = CamelReactiveStreams.get(context).getPublisher("messages", String.class);
                Observable.fromPublisher(messages)
                        .map(s -> s + " world!")
                        .doOnNext(s -> LOG.info("Received message {}", s))
                        .subscribe();

            }
        });

        main.run();
    }

}
