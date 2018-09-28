package com.talents.examples.ws.jugtours;

import java.util.logging.LogManager;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
public class App {



  public static void main(String[] args) {

    SpringApplication.run(App.class,
        "--spring.application.name=jugtours",
        "--server.port=9999");

    //JUL support
    LogManager.getLogManager().reset();
    SLF4JBridgeHandler.install();

    try {
      String 
    }
  }

}
