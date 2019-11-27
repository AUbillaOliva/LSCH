package cl.afubillaoliva.lsch.models;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Word implements Serializable {

    private String title;
    private ArrayList<String> description, sin, ant, images, category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getSin() {
        return sin;
    }

    public void setSin(ArrayList<String> sin) {
        this.sin = sin;
    }

    public ArrayList<String> getAnt() {
        return ant;
    }

    public void setAnt(ArrayList<String> ant) {
        this.ant = ant;
    }

    public ArrayList<String> getDescription() {
        return description;

    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
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
