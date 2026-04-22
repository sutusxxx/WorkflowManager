package WorkflowManager.issue;

import java.util.Optional;

public enum IssueLinkType {
    BLOCKS,
    BLOCKED_BY,
    DUPLICATES,
    DUPLICATED_BY,
    RELATES_TO;

    public Optional<IssueLinkType> inverse() {
        return switch (this) {
            case BLOCKS        -> Optional.of(BLOCKED_BY);
            case BLOCKED_BY    -> Optional.of(BLOCKS);
            case DUPLICATES    -> Optional.of(DUPLICATED_BY);
            case DUPLICATED_BY -> Optional.of(DUPLICATES);
            case RELATES_TO    -> Optional.empty(); // symmetric, no inverse needed
        };
    }

    public boolean isSymmetric() {
        return this == RELATES_TO;
    }
}
