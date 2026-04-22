package WorkflowManager.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "permissions")
public class Permission {
    @Id
    String id;

    @Indexed(unique = true)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    public static Permission valueOf(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }
}
