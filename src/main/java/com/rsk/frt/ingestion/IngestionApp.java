package com.rsk.frt.ingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApp {

    public static void main(String[] args) {
        try{
            System.out.println("starting ingestion main app");
            SpringApplication.run(IngestionApp.class, args);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
