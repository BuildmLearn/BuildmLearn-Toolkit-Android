package org.buildmlearn.toolkit.utilities;

/**
 * @brief Model class for storing Alias data for keystore.
 */
public class Alias {

    long id;
    Keystore keystore;
    String name;
    String displayName;
    String password;
    boolean selected;
    boolean rememberPassword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Keystore getKeystore() {
        return keystore;
    }

    public void setKeystore(Keystore keystore) {
        this.keystore = keystore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean rememberPassword() {
        return rememberPassword;
    }

    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alias alias = (Alias) o;

        if (id != alias.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
