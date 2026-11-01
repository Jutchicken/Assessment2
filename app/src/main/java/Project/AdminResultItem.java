package Project;
import com.google.firebase.Timestamp;
public class AdminResultItem {
    private final String userId;
    private String playerName;
    private final int score, totalQuestions, percentage;
    private final Timestamp completedAt;
    public AdminResultItem(String userId, String playerName, int score, int totalQuestions, int percentage, Timestamp completedAt) {
        this.userId=userId; this.playerName=playerName; this.score=score; this.totalQuestions=totalQuestions;
        this.percentage=percentage; this.completedAt=completedAt;
    }
    public String getUserId(){return userId;}
    public String getPlayerName(){return playerName;}
    public void setPlayerName(String name){playerName=name;}
    public int getScore(){return score;}
    public int getTotalQuestions(){return totalQuestions;}
    public int getPercentage(){return percentage;}
    public Timestamp getCompletedAt(){return completedAt;}
}
