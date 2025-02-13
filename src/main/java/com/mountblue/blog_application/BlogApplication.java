package com.mountblue.blog_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}
}

//newest one
//http://localhost:8080/
// ?sort=desc&
// page=0&
// author=Namritha+Thapar
// &publishedAt=&search=&
// tagName=oops&tagName=java&tagName=python&tagName=nevergambleyourheart

//oldest one
//http://localhost:8080/
// ?sort=asc&
// page=0&
// author=Namritha+Thapar
// &publishedAt=&search=&
// tagName=oops&tagName=java&tagName=python&tagName=nevergambleyourheart

