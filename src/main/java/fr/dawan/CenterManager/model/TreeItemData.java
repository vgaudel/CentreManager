package fr.dawan.CenterManager.model;

import java.util.function.BiConsumer;

import fr.dawan.CenterManager.dao.GenericDao;

public class TreeItemData<T extends Displayable> {
    private final boolean isCategory;
    private final boolean isRoot;
    private final T data;
    private String label;
    private GenericDao<T> dao;
    private BiConsumer<T, String> labelSetter;


    // A UTILISER UNIQUEMENT POUR ROOT
    public TreeItemData(String label, Boolean isRoot) {
        this.label = label;
        this.isCategory = true;
        this.isRoot = isRoot;
        this.data = null;
        this.dao = null;
    }

    // Parent
    public TreeItemData(String label, GenericDao<T> dao) {
        this.label = label;
        this.isCategory = true;
        this.isRoot = false;
        this.data = null;
        this.dao = dao;
    }

    // Enfant
    public TreeItemData(String label, T data) {
        this.label = label;
        this.isCategory = false;
        this.isRoot = false;
        this.data = data;
        this.dao = null;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public T getData() {
        return data;
    }

    public GenericDao<T> getDao() {
        return dao;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setLabelSetter(BiConsumer<T, String> labelSetter) {
        this.labelSetter = labelSetter;
    }

    public BiConsumer<T, String> getLabelSetter() {
        return labelSetter;
    }

    public void setDaoFromParent(GenericDao<?> parentDao) {
        if (this.dao == null && !isCategory && !isRoot) {
            this.dao = (GenericDao<T>) parentDao;
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
