package uk.ac.wlv.blogapp;

import java.util.UUID;
import java.util.Date;

public class Blog {
    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private boolean mComplete;


    public Blog() {
        this(UUID.randomUUID());
    }

    public Blog(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isComplete() {
        return mComplete;
    }

    public void setComplete(boolean complete) {
        mComplete = complete;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
