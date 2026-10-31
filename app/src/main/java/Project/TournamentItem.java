package Project;

public class TournamentItem {

    private String id;
    private String title;
    private int category;
    private String difficulty;
    private String type;
    private int amount;
    private String status;

    public TournamentItem() {
    }

    public TournamentItem(
            String id,
            String title,
            int category,
            String difficulty,
            String type,
            int amount,
            String status
    ) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.difficulty = difficulty;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}