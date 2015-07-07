package core;

import java.util.Date;

public class FbGraphTokenResults {
	private String id;
	private String firstName;
	private String gender;
	private String lastName;
	private String link;
	private String locale;
	private String name;
	private String timezone;
	private Date updatedTime;
	private boolean verified;
	

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGender() {
        return gender;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLink() {
        return link;
    }

    public String getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    public String getTimezone() {
        return timezone;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
