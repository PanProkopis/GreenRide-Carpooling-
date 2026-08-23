package gr.hua.dit.greenride.dto;

public class AdminUserResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean blocked;

    public AdminUserResponse(
            Long id,
            String name,
            String email,
            String role,
            boolean blocked) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.blocked = blocked;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isBlocked() {
        return blocked;
    }
}