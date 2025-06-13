package sample.cafeshopmanagement.models;

public class User {
    private String username;
    private String password;
    private String securityQuestion;
    private String securityAnswer;

    public User(String username, String password, String question, String answer) {
        this.username = username;
        this.password = password;
        this.securityQuestion = question;
        this.securityAnswer = answer;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
