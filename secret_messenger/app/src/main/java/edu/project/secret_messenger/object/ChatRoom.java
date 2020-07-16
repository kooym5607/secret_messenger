package edu.project.secret_messenger.object;

public class ChatRoom {
    private ChatDTO chatDTO;
    private String roomUid;

    ChatRoom() { }
    ChatRoom(ChatDTO chatDTO, String roomUid) { this.chatDTO = chatDTO;this.roomUid = roomUid; }
    public String getRoomUid() { return roomUid; }
    public void setRoomUid(String roomUid) { this.roomUid = roomUid; }
    public ChatDTO getChatDTO() { return chatDTO; }
    public void setChatDTO(ChatDTO chatDTO) { this.chatDTO = chatDTO; }
}

