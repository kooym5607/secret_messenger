package edu.project.secret_messenger.object;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatDTO {
    private String userID;
    private String message;
    private String userName;

    public ChatDTO(){}
    public ChatDTO(String userID, String message, String userName){ this.userID=userID; this.message=message; this.userName=userName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getUserName(){ return userName; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("message", message);
        result.put("userName", userName);
        return result;
    }
}
