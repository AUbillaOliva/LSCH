package cl.afubillaoliva.lsch.models;

public class StorageOptionItem {

    private String title, location;
    private long free, total, used;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public double getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }
}
