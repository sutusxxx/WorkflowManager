package com.sutusxxx.user;

import com.sutusxxx.user.model.UserSummaryDTO;
import com.sutusxxx.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    public User syncUser(Map<String, Object> claims) {
        String keycloakId = (String) claims.get("sub");

        return userRepository.findByKeycloakId(keycloakId)
                .map(this::updateLastLogin)
                .orElseGet(() -> createUser(claims));
    }

    public <T> Map<T, UserSummaryDTO> batchLoadUsers(List<T> objects, Function<T, String> idExtractor) {
        Set<String> userIds = objects.stream()
                .map(idExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<T, UserSummaryDTO> result = new HashMap<>();

        if (userIds.isEmpty()) return result;

        Map<String, UserSummaryDTO> usersById = userRepository.findAllById(userIds)
                .stream()
                .map(userConverter::convertToSummaryDTO)
                .collect(Collectors.toMap(UserSummaryDTO::getId, Function.identity()));

        objects.forEach(object -> result.put(object, usersById.get(idExtractor.apply(object))));
        return result;
    }

    private User createUser(Map<String, Object> claims) {
        User user = new User();
        user.setKeycloakId((String) claims.get("sub"));
        user.setUsername((String) claims.get("preferred_username"));
        user.setEmail((String) claims.get("email"));
        user.setRegistrationDate(OffsetDateTime.now());
        user.setLastLoggedIn(OffsetDateTime.now());
        return userRepository.save(user);
    }

    private User updateLastLogin(User user) {
        user.setLastLoggedIn(OffsetDateTime.now());
        return userRepository.save(user);
    }
}
