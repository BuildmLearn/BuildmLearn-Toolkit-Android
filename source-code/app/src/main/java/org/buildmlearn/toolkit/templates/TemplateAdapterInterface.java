package org.buildmlearn.toolkit.templates;

/**
 * Created by scopeinfinity on 2/3/16.
 */
public interface TemplateAdapterInterface {
    /**
     * @param query
     * @brief Called whenever list content needed to be changed depending upon search query.
     */
    void searchFilter(String query);
}
