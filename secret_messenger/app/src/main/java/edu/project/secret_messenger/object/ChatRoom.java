package edu.project.secret_messenger.object;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {
    private String roomUid;
    private String title;
    private String lastMsg;
    private Map<String, String> users;
    private User user;

    public ChatRoom() { }
    public ChatRoom(Map<String, String> users){ this.users = users;}
    public ChatRoom(User user){this.user = user;}
    public String getUserName(User mUser){
        String result = null;
        for(String key: users.keySet()) {
            if (!key.equals(mUser.getId())) {
                result = users.get(key);
            }
        }
        return result;
    }

    public String getRoomUid() { return roomUid; }
    public void setRoomUid(String roomUid) { this.roomUid = roomUid; }

    public String getLastMsg() { return lastMsg; }
    public void setLastMsg(String lastMsg) { this.lastMsg = lastMsg; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("lastMsg", lastMsg);
        return result;
    }
}

