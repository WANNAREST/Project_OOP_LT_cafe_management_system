package application;

public class Customer extends User{
    private String phoneNumber;
    private String email;
    private boolean firstLogin = true;
    private String address;

    public Customer(String username, String password, String fullname, String phoneNumber, String email, String address) {
        super(username, password, fullname);
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstLogin = true;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getFullName() {
        return fullname;
    }
    public void setFullName(String fullName) {
        this.fullname = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isFirstLogin() {
        return firstLogin;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public boolean changePassword (String oldPassword, String newPassword) {
        if(newPassword == null || newPassword.isEmpty()){
            return false;
        }
        this.password = newPassword;
        this.firstLogin = false;
        return true;
    }
    @Override
    public void printAccountInfo(){
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Full Name: " + fullname);
        System.out.println("Email: " + (email != null ? email : "No email is ok."));
        System.out.println("Password: " + password);
    }
}