package application;

public class Customer extends User{
    private String username;
    private String phoneNumber;
    private String fullName;
    private String email;
    private String password;
    private boolean firstLogin = true;
    private String address;
    private int rewardPoint = 0;

    public Customer(String username, String phoneNumber, String fullName, String email, String password) {
        super(username, password);
        this.fullName = fullName;
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
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    public void addRewardPoint(int amount){
        this.rewardPoint += amount;
    }
    public int getRewardPoint(){
        return this.rewardPoint;
    }
    @Override
    public void printAccountInfo(){
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + (email != null ? email : "No email is ok."));
        System.out.println("Password: " + password);
    }
}
