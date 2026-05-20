package com.sutusxxx.graphql.configuration;

import com.sutusxxx.commons.SecurityUtils;
import com.sutusxxx.user.User;
import com.sutusxxx.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public AuditorAware<String> auditorProvider(SecurityUtils securityUtils,
                                                UserRepository userRepository) {
        return () -> {
            try {
                String keycloakId = securityUtils.getCurrentUserKeycloakId();

                return userRepository.findByKeycloakId(keycloakId)
                        .map(User::getId);

            } catch (Exception e) {
                return Optional.empty(); // not authenticated
            }
        };
    }
}
