package application;

public class Manager extends User{

    public Manager(String username, String password) {
        super(username, password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public void printAccountInfo() {
        System.out.println("Manager Username: " + username);
        System.out.println("Password: " + password);
    }
}
