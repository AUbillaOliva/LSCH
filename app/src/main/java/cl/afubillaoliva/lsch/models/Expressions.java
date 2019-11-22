package cl.afubillaoliva.lsch.models;

import java.io.Serializable;

public class Expressions implements Serializable {

    private String title;
    private String[] images, category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }


}
