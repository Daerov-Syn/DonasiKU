package com.aplikasiprojeksmt4.models;

public class Notification {
    private String id;
    private String title;
    private String description;
    private String time;
    private int iconResId;
    private int iconBgColor;
    private int iconTint;
    private String programId; // Added for navigation

    public Notification(String id, String title, String description, String time, int iconResId, int iconBgColor, int iconTint) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.iconResId = iconResId;
        this.iconBgColor = iconBgColor;
        this.iconTint = iconTint;
    }

    public Notification(String id, String title, String description, String time, int iconResId, int iconBgColor, int iconTint, String programId) {
        this(id, title, description, time, iconResId, iconBgColor, iconTint);
        this.programId = programId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTime() { return time; }
    public int getIconResId() { return iconResId; }
    public int getIconBgColor() { return iconBgColor; }
    public int getIconTint() { return iconTint; }
    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
}
