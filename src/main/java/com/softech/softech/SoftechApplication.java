package com.softech.softech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoftechApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftechApplication.class, args);
		System.out.println("La aplicación Softech está iniciando correctamente");
	}
}
