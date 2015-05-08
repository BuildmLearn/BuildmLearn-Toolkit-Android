package org.buildmlearn.toolkit.model;

/**
 * Created by Abhishek on 22/04/15.
 */
public interface NavigationMenuItem {

    int SECTION = 1;
    int PROJECT_MENU = 2;
    int AUX_MENU = 3;

    public int menuType();
}
