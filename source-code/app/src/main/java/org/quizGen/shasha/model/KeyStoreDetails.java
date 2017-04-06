package org.quizGen.shasha.model;

/**
 * @brief Model class for holding the details of keystore file.
 * <p/>
 */
public class KeyStoreDetails {

    private final String assetsPath;
    private final String password;
    private final String alias;
    private final String aliasPassword;

    public KeyStoreDetails(String password, String alias, String aliasPassword) {
        this.assetsPath = "TestKeyStore.jks";
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
