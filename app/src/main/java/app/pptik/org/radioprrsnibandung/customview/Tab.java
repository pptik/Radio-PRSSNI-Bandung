package app.pptik.org.radioprrsnibandung.customview;

/**
 * Created by Hafid on 5/17/2017.
 */

public class Tab {
    private int icon;
    private String name;

    public Tab(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
