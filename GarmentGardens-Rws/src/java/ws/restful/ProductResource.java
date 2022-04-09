package ws.restful;

import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import ejb.session.stateless.UserEntitySessionBeanLocal;
import entity.ProductEntity;
import entity.StaffEntity;
import entity.TagEntity;
import entity.UserEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.UpdateProductException;
import ws.datamodel.CreateProductReq;
import ws.datamodel.UpdateProductReq;



@Path("Product")

public class ProductResource 
{    
    @Context
    private UriInfo context;
    
    private final SessionBeanLookup sessionBeanLookup;
    
//    private final StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    private final UserEntitySessionBeanLocal userEntitySessionBeanLocal;
    private final ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    
    
    
    public ProductResource() 
    {
        sessionBeanLookup = new SessionBeanLookup();
        userEntitySessionBeanLocal = sessionBeanLookup.lookupUserEntitySessionBeanLocal();
//        staffEntitySessionBeanLocal = sessionBeanLookup.lookupStaffEntitySessionBeanLocal();
        productEntitySessionBeanLocal = sessionBeanLookup.lookupProductEntitySessionBeanLocal();
    }
    
    
    
    @Path("retrieveAllProducts")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllProducts(@QueryParam("username") String username, 
                                        @QueryParam("password") String password)
    {
        try
        {
//            StaffEntity staffEntity = staffEntitySessionBeanLocal.staffLogin(username, password);
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** ProductResource.retrieveAllProducts(): Staff " + userEntity.getUsername() + " login remotely via web service");

            List<ProductEntity> productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
            
            for(ProductEntity productEntity:productEntities)
            {
                if(productEntity.getCategory().getParentCategory() != null)
                {
                    productEntity.getCategory().getParentCategory().getSubCategories().clear();
                }
                
                productEntity.getCategory().getProducts().clear();
                
                for(TagEntity tagEntity:productEntity.getTags())
                {
                    tagEntity.getProducts().clear();
                }
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(productEntities) {
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    
    
    @Path("retrieveProduct/{productId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveProduct(@QueryParam("username") String username, 
                                        @QueryParam("password") String password,
                                        @PathParam("productId") Long productId)
    {
        try
        {
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** ProductResource.retrieveProduct(): Staff " + userEntity.getUsername() + " login remotely via web service");

            ProductEntity productEntity = productEntitySessionBeanLocal.retrieveProductByProductId(productId);
            
            if(productEntity.getCategory().getParentCategory() != null)
            {
                productEntity.getCategory().getParentCategory().getSubCategories().clear();
            }

            productEntity.getCategory().getProducts().clear();

            for(TagEntity tagEntity:productEntity.getTags())
            {
                tagEntity.getProducts().clear();
            }
            
            return Response.status(Status.OK).entity(productEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(ProductNotFoundException ex)
        {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(CreateProductReq createProductReq)
    {
        if(createProductReq != null)
        {
            try
            {
                UserEntity userEntity = userEntitySessionBeanLocal.userLogin(createProductReq.getUsername(), createProductReq.getPassword());
                System.out.println("********** ProductResource.createProduct(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");
                
                ProductEntity productEntity  = productEntitySessionBeanLocal.createNewProduct(createProductReq.getProductEntity(), createProductReq.getCategoryId(), createProductReq.getTagIds());                
                
                return Response.status(Response.Status.OK).entity(productEntity.getProductId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewProductException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
            catch(ProductSkuCodeExistException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(UpdateProductReq updateProductReq)
    {
        if(updateProductReq != null)
        {
            try
            {                
                UserEntity userEntity = userEntitySessionBeanLocal.userLogin(updateProductReq.getUsername(), updateProductReq.getPassword());
                System.out.println("********** ProductResource.updateProduct(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");
                
                productEntitySessionBeanLocal.updateProduct(updateProductReq.getProductEntity(), updateProductReq.getCategoryId(), updateProductReq.getTagIds());
                
                return Response.status(Response.Status.OK).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(UpdateProductException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
            catch(Exception ex)
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update product request").build();
        }
    }
    
    
    
    @Path("{productId}")
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@QueryParam("username") String username, 
                                        @QueryParam("password") String password,
                                        @PathParam("productId") Long productId)
    {
        try
        {
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** ProductResource.deleteProduct(): USER(Seller) " + userEntity.getUsername() + " login remotely via web service");

            productEntitySessionBeanLocal.deleteProduct(productId);
            
            return Response.status(Status.OK).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(ProductNotFoundException | DeleteProductException ex)
        {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}