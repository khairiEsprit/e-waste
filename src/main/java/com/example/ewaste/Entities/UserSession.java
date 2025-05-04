package com.example.ewaste.Entities;



public class UserSession {
    private static UserSession instance;

    private int userId;
    private String userName;
    private String prenom;
    private UserRole role;


    public UserSession(int userId, String userName, String prenom, UserRole role) {
        this.userId = userId;
        this.userName = userName;
        this.prenom = prenom;
        this.role = role;

    }

    public static UserSession getInstace(int userId, String userName, String prenom, UserRole role) {
        if(instance == null) {
            instance = new UserSession(userId, userName, prenom, role);
        }
        return instance;
    }

    public static UserSession initializeUserSession(User u1) {
        instance = new UserSession(u1.getId(), u1.getFirst_name(), u1.getLast_name(), u1.getRole());
        ApplicationContext.getInstance().setUserSession(instance);
        return instance;
    }

    public static UserSession initializeUserSessionGoogle(GoogleUser u1) {
        instance = new UserSession(1, u1.getName(), u1.getGivenName(), UserRole.CITOYEN);
        ApplicationContext.getInstance().setUserSession(instance);
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPrenom() {
        return prenom;
    }

    public UserRole getRole() {
        return role;
    }



    public void cleanUserSession() {
        userId = 0;
        userName = "";
        prenom = "";
        role = null;

    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
