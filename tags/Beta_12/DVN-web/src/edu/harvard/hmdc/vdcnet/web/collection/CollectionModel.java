/*
 * CollectionModel.java
 *
 * Created on November 20, 2006, 2:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.collection;

/**
 *
 * @author roberttreacy
 */
public class CollectionModel {
    
    /** Creates a new instance of CollectionModel */
    public CollectionModel() {
    }
    
    private boolean selected;
    private String name;
    private Long id;
    private int level;
    private boolean queryType;
    private boolean link;
    public boolean isSelected() { return selected; }
    public String getName() { return name;}
    public int getLevel() { return level; }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isQueryType() {
        return queryType;
    }

    public void setQueryType(boolean queryType) {
        this.queryType = queryType;
    }

    private long parentId;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }
}
