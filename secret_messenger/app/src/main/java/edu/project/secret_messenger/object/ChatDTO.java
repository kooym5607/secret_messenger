package edu.project.secret_messenger.object;

public class ChatDTO {
    private User user;
    private String message;

    public ChatDTO(){}
    public ChatDTO(User user, String message){ this.user=user; this.message=message; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
