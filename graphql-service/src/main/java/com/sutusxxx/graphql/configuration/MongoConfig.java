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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = List.of(
                new DateToOffsetDateTimeConverter(),
                new OffsetDateTimeToDateConverter()
        );
        return new MongoCustomConversions(converters);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public AuditorAware<String> auditorProvider(SecurityUtils securityUtils,
                                                UserRepository userRepository) {
        return () -> {
            try {
                String username = securityUtils.getCurrentUsername();

                return userRepository.findByUsername(username)
                        .map(User::getId);

            } catch (Exception e) {
                return Optional.of("_system"); // default not authenticated user
            }
        };
    }

    @ReadingConverter
    static class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date source) {
            return source.toInstant().atOffset(ZoneOffset.UTC);
        }
    }

    @WritingConverter
    static class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            return Date.from(source.toInstant());
        }
    }
}
