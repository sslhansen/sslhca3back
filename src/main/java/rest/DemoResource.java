package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.BoredDTO;
import dtos.FoodDTO;
import dtos.CombinedDTO;
import dtos.KanyeDTO;
import dtos.QuoteDTO;
import dtos.RandomDogDTO;
import entities.User;
import java.io.IOException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.HttpUtils;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Gson gson = new Gson();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    
    @GET
    @Path("extern")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJokes() throws IOException {
        String randomDog = HttpUtils.fetchData("https://dog.ceo/api/breeds/image/random");
        RandomDogDTO randomDogDTO = gson.fromJson(randomDog, RandomDogDTO.class);

        String randomQuote = HttpUtils.fetchData("https://programming-quotes-api.herokuapp.com/quotes/random");
        QuoteDTO randomQuoteDTO = gson.fromJson(randomQuote, QuoteDTO.class);
        
        String food = HttpUtils.fetchData("https://foodish-api.herokuapp.com/api");
        FoodDTO breakingBadDTO = gson.fromJson(food, FoodDTO.class);
        
        String bored = HttpUtils.fetchData("https://www.boredapi.com/api/activity");
        BoredDTO boredDTO = gson.fromJson(bored, BoredDTO.class);
        
        String kanye = HttpUtils.fetchData("https://api.kanye.rest/");
        KanyeDTO kanyeDTO = gson.fromJson(kanye, KanyeDTO.class);
        
        CombinedDTO combined = new CombinedDTO(boredDTO, breakingBadDTO, kanyeDTO, randomQuoteDTO, randomDogDTO);
        
        String json = GSON.toJson(combined);
        return json;
    }

}
