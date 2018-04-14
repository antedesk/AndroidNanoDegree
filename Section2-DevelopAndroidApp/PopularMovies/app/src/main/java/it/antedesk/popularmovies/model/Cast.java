package it.antedesk.popularmovies.model;

/**
 * Created by Antedesk on 14/04/2018.
 */

public class Cast {

    private String name;
    private String character;
    private String profilePicPath;

    public Cast(String name, String character, String profilePicPath) {
        this.name = name;
        this.character = character;
        this.profilePicPath = profilePicPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }
}
