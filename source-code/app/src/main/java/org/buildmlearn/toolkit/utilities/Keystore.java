package org.buildmlearn.toolkit.utilities;

import java.util.ArrayList;
import java.util.List;


/**
 * @brief Model class for storing Keystore details
 */
public class Keystore {

    private final List<Alias> aliases = new ArrayList<>();
    private long id;
    private String path;
    private String password;
    private boolean rememberPassword;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addAlias(Alias alias) {
        aliases.add(alias);
    }

    public List<Alias> getAliases() {
        return aliases;
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

        Keystore keystore = (Keystore) o;

        return id == keystore.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
