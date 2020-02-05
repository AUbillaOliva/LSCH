package cl.afubillaoliva.lsch.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Expressions implements Serializable {

    private String title;
    private ArrayList<String> images, category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }
}
