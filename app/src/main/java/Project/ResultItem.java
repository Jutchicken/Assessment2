package Project;


import com.google.firebase.Timestamp;

public class ResultItem {
    private String userId;
    private String tournamentId;
    private String tournamentTitle;
    private int score;
    private int totalQuestions;
    private int percentage;
    private Timestamp completedAt;

    public ResultItem() {
    }

    public ResultItem(String userId, String tournamentId, String tournamentTitle, int score, int totalQuestions, int percentage, Timestamp completedAt) {
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.tournamentTitle = tournamentTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.completedAt = completedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public String getTournamentTitle() {
        return tournamentTitle;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getPercentage() {
        return percentage;
    }

    public Timestamp getCompletedAt() {
        return completedAt;
    }
}


