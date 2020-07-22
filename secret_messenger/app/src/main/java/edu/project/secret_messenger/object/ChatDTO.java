package edu.project.secret_messenger.object;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatDTO {
    private String userID;
    private String message;
    private String userName;
    private Date msg_Time;
    private boolean is_Enc = false;

    public ChatDTO(){}
    public ChatDTO(String userID, String message, String userName,Date msg_Time){ this.userID=userID; this.message=message; this.userName=userName;this.msg_Time=msg_Time; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    public String getUserName(){ return userName; }
    public Date getMsg_Time() { return msg_Time; }
    public void setMsg_Time(Date msg_Time) { this.msg_Time = msg_Time; }
    public boolean isIs_Enc() { return is_Enc; }
    public void setIs_Enc(boolean is_Enc) { this.is_Enc = is_Enc; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("message", message);
        result.put("userName", userName);
        result.put("msg_Time",msg_Time);
        return result;
    }



}
