package edu.project.secret_messenger;

public class User {
    private String id;
    private String pw;
    private String name;

    User(String id, String pw, String name){
        this.id = id; this.pw = pw; this.name = name;
    }

    public void setId(String id){ this.id = id;}
    public void setPw(String pw){ this.pw = pw;}
    public void setName(String name){ this.name = name;}
    public String getId() { return id;}

    public String getName() { return name;}

    public String getPw() { return pw;}
}
