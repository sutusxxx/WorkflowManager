package WorkflowManager.user;

import WorkflowManager.user.model.UserDetailsDTO;
import WorkflowManager.user.model.UserSummaryDTO;
import WorkflowManager.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final UserConverter userConverter;

    public UserService(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDetailsDTO loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
        return userConverter.convertToDTO(user);
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
}
