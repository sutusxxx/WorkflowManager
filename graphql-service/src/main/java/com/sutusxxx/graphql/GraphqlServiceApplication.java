package com.sutusxxx.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {
		"com/sutusxxx/graphql",
		"com/sutusxxx/user",
})
@ComponentScan(basePackages = {
		"com/sutusxxx/graphql",
		"com/sutusxxx/commons",
		"com/sutusxxx/user"
})
public class GraphqlServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlServiceApplication.class, args);
	}

}
