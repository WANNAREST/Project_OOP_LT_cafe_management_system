package application;
import java.util.*;
public class UserManager {
    private List<User> userLogined;
    public UserManager(){
        this.userLogined = new ArrayList<>();
    }
    public void addUser(User user){
        for(User u : userLogined){
            if(u.getUsername().equals(user.getUsername())){
                System.out.println("User already exists");
                return;
            }
        }
        userLogined.add(user);
        System.out.println("User added");
    }
    public void showAllCustomer(){
        boolean found = false;
        for(User u : userLogined){
            if(u instanceof Customer){
                if(!found){
                    found = true;
                    System.out.println("Customer List:\n");
                }
                u.printAccountInfo();
                System.out.println();
            }
        }
        if(!found){
            System.out.println("Have no Customer.");
        }
    }
    public void showAllEmployee(){
        boolean found = false;
        for(User u : userLogined){
            if(u instanceof Employee){
                if(!found){
                    found = true;
                    System.out.println("Employee List:\n");
                }
                u.printAccountInfo();
                System.out.println();
            }
        }
        if(!found){
            System.out.println("Have no Employee.");
        }
    }

    public void removeUser(User user){
        for (User u : userLogined) {
            if (u.getUsername().equals(user.getUsername())) {
                userLogined.remove(u);
                System.out.println("User has been removed.");
                return;
            }
        }
        System.out.println("User does not exist");
        return;
    }
    public User findUserByUsername(String username) {
        for (User u : userLogined) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

}
