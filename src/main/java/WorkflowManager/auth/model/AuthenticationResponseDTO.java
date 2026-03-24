package WorkflowManager.auth.model;

public class AuthenticationResponseDTO {
    private String accessToken;

    public AuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
