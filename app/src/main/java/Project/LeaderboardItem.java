package Project;

import com.google.firebase.Timestamp;

public class LeaderboardItem {
    private String userId;
    private String playerName;
    private int score;
    private int totalQuestions;
    private Timestamp completedAt;

    public LeaderboardItem(String userId, String playerName, int score, int totalQuestions, Timestamp completedAt) {
        this.userId = userId;
        this.playerName = playerName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
}
