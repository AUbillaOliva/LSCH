package cl.afubillaoliva.lsch.models;

public class Word {

    private String title;
    private String[] description, images, sin, ant;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getSin() {
        return sin;
    }

    public void setSin(String[] sin) {
        this.sin = sin;
    }

    public String[] getAnt() {
        return ant;
    }

    public void setAnt(String[] ant) {
        this.ant = ant;
    }
}
