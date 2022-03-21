/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import entity.CategoryEntity;
import java.io.IOException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import util.exception.CreateNewCategoryException;
import util.exception.InputDataValidationException;
/**
 *
 * @author rilwa
 */
@Named(value = "categoryManagementManagedBean")
@ViewScoped
public class categoryManagementManagedBean implements Serializable {

    @EJB(name = "CategoryEntitySessionBeanLocal")
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    
    private List<CategoryEntity> categoryEntities;
    private List<CategoryEntity> filteredCategoryEntities;
    
    private CategoryEntity newCategoryEntity;
    private Long categoryIdNew;
    
    private CategoryEntity selectedCategoryEntityToUpdate;
    private Long categoryIdUpdate;
    
    @Inject
    private ViewCategoryManagedBean viewCategoryManagedBean;

    /**
     * Creates a new instance of categoryManagementManagedBean
     */
    public categoryManagementManagedBean() {
        this.newCategoryEntity = new CategoryEntity();
    }
    
    @PostConstruct
    public void postConstruct() {
        categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
    }
    
    public void viewCategoryDetails(ActionEvent event) throws IOException {
        Long categoryIdToView = (Long) event.getComponent().getAttributes().get("categoryId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("categoryIdToView", categoryIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewCategoryDetails.xhtml");
    }
    
    public void createNewCategory(ActionEvent event) {
        
        if (getCategoryIdNew() == 0) {
            setCategoryIdNew(null);
        }
        
        try {
            CategoryEntity ce = categoryEntitySessionBeanLocal.createNewCategoryEntity(getNewCategoryEntity(), getCategoryIdNew());
            getCategoryEntities().add(ce);
            
            if (filteredCategoryEntities != null) {
                filteredCategoryEntities.add(ce);
            }
            
            setNewCategoryEntity(new CategoryEntity());
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New Category created successfully (Category ID: " + ce.getCategoryId() + ")", null));
        } catch (InputDataValidationException | CreateNewCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new Category: " + ex.getMessage(), null));
        }
    }

    public void doUpdateCategory(ActionEvent event) {
        selectedCategoryEntityToUpdate = (CategoryEntity) event.getComponent().getAttributes().get("categoryEntityToUpdate");

        categoryIdUpdate = selectedCategoryEntityToUpdate.getCategoryId();

    }

    public void updateCategory(ActionEvent event) {
        if (categoryIdUpdate == 0) {
            categoryIdUpdate = null;
        }

        try {            
            categoryEntitySessionBeanLocal.updateCategory(newCategoryEntity, categoryIdNew);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Category updated successfully", null));
        }
        
        catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    public void deleteCategory(ActionEvent event) {
        try {
            CategoryEntity categoryEntityToDelete = (CategoryEntity) event.getComponent().getAttributes().get("categoryEntityToDelete");
            categoryEntitySessionBeanLocal.deleteCategory(categoryEntityToDelete.getCategoryId());

            categoryEntities.remove(categoryEntityToDelete);

            if (filteredCategoryEntities != null) {
                filteredCategoryEntities.remove(categoryEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Category deleted successfully", null));
        }  
        
        catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

        
        
    /**
     * @return the categoryEntities
     */
    public List<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    /**
     * @param categoryEntities the categoryEntities to set
     */
    public void setCategoryEntities(List<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
    }

    /**
     * @return the filteredCategoryEntities
     */
    public List<CategoryEntity> getFilteredCategoryEntities() {
        return filteredCategoryEntities;
    }

    /**
     * @param filteredCategoryEntities the filteredCategoryEntities to set
     */
    public void setFilteredCategoryEntities(List<CategoryEntity> filteredCategoryEntities) {
        this.filteredCategoryEntities = filteredCategoryEntities;
    }

    /**
     * @return the newCategoryEntity
     */
    public CategoryEntity getNewCategoryEntity() {
        return newCategoryEntity;
    }

    /**
     * @param newCategoryEntity the newCategoryEntity to set
     */
    public void setNewCategoryEntity(CategoryEntity newCategoryEntity) {
        this.newCategoryEntity = newCategoryEntity;
    }

    /**
     * @return the categoryIdNew
     */
    public Long getCategoryIdNew() {
        return categoryIdNew;
    }

    /**
     * @param categoryIdNew the categoryIdNew to set
     */
    public void setCategoryIdNew(Long categoryIdNew) {
        this.categoryIdNew = categoryIdNew;
    }

    /**
     * @return the viewCategoryManagedBean
     */
    public ViewCategoryManagedBean getViewCategoryManagedBean() {
        return viewCategoryManagedBean;
    }

    /**
     * @param viewCategoryManagedBean the viewCategoryManagedBean to set
     */
    public void setViewCategoryManagedBean(ViewCategoryManagedBean viewCategoryManagedBean) {
        this.viewCategoryManagedBean = viewCategoryManagedBean;
    }
    

}
    
