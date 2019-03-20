import java.util.Date;

public class Comment implements ProgressReport {
    private String comment;
    private double changeDate;
    private Date time;
    private User author;
    final private double reported;
    final private double finished;

    public Comment(double reported, double finished, String comment, Date commonTime, User author) {
        this.reported = reported;
        this.finished = finished;
        this.comment = comment;
        this.time = commonTime;
    }

    public void setComment(String newComment){
        changeDate = time.getTime();
        comment = newComment;
    }

    public String getComment(){
        return comment;
    }

    @Override
    public double getDuration() {
        return finished - reported;
    }

    @Override
    public double getReported() {
        return reported;
    }

    @Override
    public double getFinished() {
        return finished;
    }

    @Override
    public User getAuthor() {
        return author;
    }
}
