package lv.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.CookieManager;
import java.net.CookiePolicy;

@SpringBootApplication
@EnableScheduling
public class EventManagerApplication {

    public static void main(String[] args) {

        SpringApplication.run(EventManagerApplication.class, args);

        CookieManager cm=new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }
}
