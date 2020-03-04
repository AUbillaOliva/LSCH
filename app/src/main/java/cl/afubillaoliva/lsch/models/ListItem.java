package cl.afubillaoliva.lsch.models;

public class ListItem {
    private String title, subtitle;
    private boolean isDisabled, expanded = false;

    public ListItem(){
        this.expanded = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isDisabled() { return isDisabled; }

    public void setDisabled(boolean disabled) { isDisabled = disabled; }

    public boolean isExpanded(){
        return expanded;
    }

    public void setExpanded(boolean expanded){
        this.expanded = expanded;
    }
}
