package application;

public class Manager extends User{

    public Manager(String username, String password, String fullname) {
        super(username, password, fullname);
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
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    @Override
    public void printAccountInfo() {
        System.out.println("Manager Username: " + username);
        System.out.println("Password: " + password);
    }
}