package com.sutusxxx.graphql.configuration;

import com.sutusxxx.graphql.annotation.CurrentUserArgumentResolver;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class GraphQlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.GraphQLShort)
                .scalar(ExtendedScalars.GraphQLLong);
    }

    @Bean
    public Executor batchExecutor() {
        return new ThreadPoolTaskExecutor() {{
            setCorePoolSize(8);
            setMaxPoolSize(16);
            setThreadNamePrefix("graphql-batch-");
            initialize();
        }};
    }

    @Bean
    public AnnotatedControllerConfigurer annotatedControllerConfigurer(
            CurrentUserArgumentResolver resolver) {

        AnnotatedControllerConfigurer configurer = new AnnotatedControllerConfigurer();
        configurer.addCustomArgumentResolver(resolver);
        return configurer;
    }
}
