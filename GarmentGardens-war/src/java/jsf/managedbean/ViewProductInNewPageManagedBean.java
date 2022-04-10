/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.RatingEntitySessionBeanLocal;
import entity.ProductEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.ProductNotFoundException;

/**
 *
 * @author brennanlee
 */
@Named(value = "viewProductInNewPageManagedBean")
@ViewScoped
public class ViewProductInNewPageManagedBean implements Serializable{

    @EJB(name = "RatingEntitySessionBeanLocal")
    private RatingEntitySessionBeanLocal ratingEntitySessionBeanLocal;

    /**
     * Creates a new instance of ViewProductInNewPageManagedBean
     */
    
    
    private ProductEntity productEntityFullDetailsToView;
    private String ratingScoreForProduct;
    
    
    
    public ViewProductInNewPageManagedBean() {
        productEntityFullDetailsToView = new ProductEntity();
        ratingScoreForProduct = "";
    }
    
    @PostConstruct
    public void postConstruct() {
        try {
            productEntityFullDetailsToView = (ProductEntity) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("ProductToView");
            setRatingScoreForProduct(String.format("%.2f out of 5", ratingEntitySessionBeanLocal.retrieveRatingScore(productEntityFullDetailsToView.getProductId())));
        } catch (ProductNotFoundException ex) {
            Logger.getLogger(ViewProductInNewPageManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void back(ActionEvent event) throws IOException{
        FacesContext.getCurrentInstance().getExternalContext().redirect("productManagement.xhtml");
    }
    
    
    

    public ProductEntity getProductEntityFullsDetailsToView() {
        return productEntityFullDetailsToView;
    }

    public void setProductEntityFullDetailsToView(ProductEntity productEntityFulLDetailsToView) {
        this.productEntityFullDetailsToView = productEntityFulLDetailsToView;
    }

    public String getRatingScoreForProduct() {
        return ratingScoreForProduct;
    }

    public void setRatingScoreForProduct(String ratingScoreForProduct) {
        this.ratingScoreForProduct = ratingScoreForProduct;
    }
    
}
