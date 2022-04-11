package ws.restful;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import ejb.session.stateless.UserEntitySessionBeanLocal;
import entity.CategoryEntity;
import entity.StaffEntity;
import entity.UserEntity;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;

@Path("Category")
public class CategoryResource {

    @Context
    private UriInfo context;

    private final SessionBeanLookup sessionBeanLookup;

//    private final StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    private final CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    private final UserEntitySessionBeanLocal userEntitySessionBeanLocal;

    public CategoryResource() {
        sessionBeanLookup = new SessionBeanLookup();

        userEntitySessionBeanLocal = sessionBeanLookup.lookupUserEntitySessionBeanLocal();
        categoryEntitySessionBeanLocal = sessionBeanLookup.lookupCategoryEntitySessionBeanLocal();
    }

    @Path("retrieveAllCategories")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCategories(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** CategoryResource.retrieveAllCategories(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");

            List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllCategories();
            for (int i = 0; i < categoryEntities.size(); i++) {
                System.out.println(categoryEntities.get(i).getSubCategories());
            }
            for (CategoryEntity categoryEntity : categoryEntities) {
                if (categoryEntity.getParentCategory() != null) {
                    categoryEntity.getParentCategory().getSubCategories().clear();
                }

                categoryEntity.getSubCategories().clear();
                categoryEntity.getProducts().clear();
            }

            GenericEntity<List<CategoryEntity>> genericEntity = new GenericEntity<List<CategoryEntity>>(categoryEntities) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("retrieveOnlyParentCategories")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOnlyParentCategories(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** CategoryResource.retrieveOnlyParentCategories(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");

            List<CategoryEntity> rootCategories = categoryEntitySessionBeanLocal.retrieveAllRootCategories();

            for (CategoryEntity categoryEntity : rootCategories) {
                categoryEntity.getSubCategories().clear();
                categoryEntity.getProducts().clear();
            }

            GenericEntity<List<CategoryEntity>> genericEntity = new GenericEntity<List<CategoryEntity>>(rootCategories) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @Path("getSubCategories/{categoryId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubCategories(@QueryParam("username") String username,
            @QueryParam("password") String password, @PathParam("categoryId") Long categoryId) {
        try {
            
                        System.out.println("testing: " + categoryId);

            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** CategoryResource.getSubCategories(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");

            List<CategoryEntity> subCategories = categoryEntitySessionBeanLocal.getSubCategories(categoryId);
            System.out.println(subCategories);
            for (CategoryEntity categoryEntity : subCategories) {
                System.out.println("the cat " + categoryEntity.getParentCategory());
                if (categoryEntity.getParentCategory() != null) {
                    categoryEntity.getParentCategory().getSubCategories().clear();
                    categoryEntity.setParentCategory(null);
                }
                categoryEntity.getSubCategories().clear();
                categoryEntity.getProducts().clear();
            }
            
            System.out.println("response " + subCategories);

            GenericEntity<List<CategoryEntity>> genericEntity = new GenericEntity<List<CategoryEntity>>(subCategories) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (CategoryNotFoundException ex) {
            return Response.status(Status.OK).build();
        }
    }

    @Path("retrieveAllLeafCategories")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllLeafCategories(@QueryParam("username") String username,
            @QueryParam("password") String password) {
        try {
            UserEntity userEntity = userEntitySessionBeanLocal.userLogin(username, password);
            System.out.println("********** CategoryResource.retrieveAllCategories(): User(SELLER) " + userEntity.getUsername() + " login remotely via web service");

            List<CategoryEntity> categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();

            for (CategoryEntity categoryEntity : categoryEntities) {
                if (categoryEntity.getParentCategory() != null) {
                    categoryEntity.getParentCategory().getSubCategories().clear();
                }

                categoryEntity.getSubCategories().clear();
                categoryEntity.getProducts().clear();
            }

            GenericEntity<List<CategoryEntity>> genericEntity = new GenericEntity<List<CategoryEntity>>(categoryEntities) {
            };

            return Response.status(Status.OK).entity(genericEntity).build();
        } catch (InvalidLoginCredentialException ex) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
}
