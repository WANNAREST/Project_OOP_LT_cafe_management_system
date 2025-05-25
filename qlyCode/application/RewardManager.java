package application;
import java.util.*;
public class RewardManager {
    private Map<Integer, String> reward = new TreeMap<>();
    public void setReward(int pointsRequired, String rewardDetails){
        reward.put(pointsRequired, rewardDetails);
    }
    public String getRewardForPoints(int points){
        String lastest = null;
        for (Map.Entry<Integer, String> entry : reward.entrySet()) {
            if(points >= entry.getKey()){
                lastest = entry.getValue();
            }
        }
        return lastest;
    }
    public void showAllReward(){
        for (Map.Entry<Integer, String> entry : reward.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
