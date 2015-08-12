package org.buildmlearn.toolkit.model;

/**
 * @brief Model class for holding the details of keystore file.
 *
 * Created by abhishek on 11/06/15 at 2:19 PM.
 */
public class KeyStoreDetails {

    private String assetsPath;
    private String password;
    private String alias;
    private String aliasPassword;

    public KeyStoreDetails(String assetsPath, String password, String alias, String aliasPassword) {
        this.assetsPath = assetsPath;
        this.password = password;
        this.alias = alias;
        this.aliasPassword = aliasPassword;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public String getPassword() {
        return password;
    }

    public String getAlias() {
        return alias;
    }

    public String getAliasPassword() {
        return aliasPassword;
    }
}
