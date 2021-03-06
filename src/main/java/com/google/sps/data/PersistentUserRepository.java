package com.google.sps.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.sps.data.User;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class implements the user repository using the datastore 
 */
public class PersistentUserRepository implements UserRepository {
  
  private final DatastoreService datastore;
  private static PersistentUserRepository instance = null;
  private static final Gson gson = new Gson();

  public PersistentUserRepository() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    this.addFakeMentors();
  }

  public void addFakeMentors() {
    User mentorA = new User("Kevin Dowling");
    mentorA.addSpecialty("Machine Learning");
    mentorA.addSpecialty("Systems");
    mentorA.setEmail("kevin@gmail.com");
    mentorA.setOccupation("Software Engineer");
    mentorA.setCompany("Google");
    mentorA.setBio("This is a fake profile for testing. Please do not contact!");
    mentorA.setId("12");

    User mentorB = new User("Mabel Mccabe");
    mentorB.addSpecialty("Electrical Engineering");
    mentorB.addSpecialty("Graphics");
    mentorB.setEmail("mabel@gmail.com");
    mentorB.setOccupation("Product Manager");
    mentorB.setCompany("Facebook");
    mentorB.setBio("This is a fake profile for testing. Please do not contact!");
    mentorB.setId("23");

    User mentorC = new User("Julie Johnson");
    mentorC.addSpecialty("Machine Learning");
    mentorC.addSpecialty("Security");
    mentorC.setEmail("julie@gmail.com");
    mentorC.setOccupation("Software Engineer");
    mentorC.setCompany("Microsoft");
    mentorC.setBio("This is a fake profile for testing. Please do not contact!");
    mentorC.setId("34");

    User mentorD = new User("John Smith");
    mentorD.addSpecialty("Artificial Intelligence");
    mentorD.addSpecialty("DoS");
    mentorD.addSpecialty("Database");
    mentorD.setEmail("john@gmail.com");
    mentorD.setOccupation("Software Engineer");
    mentorD.setCompany("Facebook");
    mentorD.setBio("This is a fake profile for testing. Please do not contact!");
    mentorD.setId("56");

    User mentorE = new User("Bret Burton");
    mentorE.addSpecialty("Machine Learning");
    mentorE.addSpecialty("Network");
    mentorE.addSpecialty("Graphics");
    mentorE.setEmail("bret@gmail.com");
    mentorE.setOccupation("Manager");
    mentorE.setCompany("Apple");
    mentorE.setBio("This is a fake profile for testing. Please do not contact!");
    mentorE.setId("78");


    addUser(mentorA);
    addUser(mentorB);
    addUser(mentorC);
    addUser(mentorD);
    addUser(mentorE);
  }

  public static PersistentUserRepository getInstance() {
    if(instance == null) {
      instance = new PersistentUserRepository();
    }
    return instance;
  }
   
  public User getUser(com.google.appengine.api.users.User googleUser) {
    String userEmail = googleUser.getEmail();

    Collection<User> allUsers = fetchUserProfiles();
    for(User user : allUsers) {
        if (user.getEmail() == userEmail) {
            return user;
        }
    }
    User newUser = new User(googleUser.getNickname());
    newUser.setEmail(userEmail);
    newUser.setId(googleUser.getUserId());

    return newUser;
  }
    
  public void addUserToDatabase(User user) {

    String name = user.getName();
    String id = user.getId();
    String email = user.getEmail();

    Entity userEntity = new Entity("User", id);
    userEntity.setProperty("name", name);
    userEntity.setProperty("id", id);  
    userEntity.setProperty("email", email); 

    String school = user.getSchool();
    String major = user.getMajor();
    String company = user.getCompany();
    Collection<String> specialties = user.getSpecialties();
    String careerTitle = user.getOccupation();
    String userBio = user.getBio();
    String wordCountJson = gson.toJson(user.getBioMap());

    if(school != null) {
        userEntity.setProperty("school", school);  
    }
    if(major != null) {
        userEntity.setProperty("major", major);
    }

    if(company != null) {
        userEntity.setProperty("company", company);
    }

    if(careerTitle != null) {
        userEntity.setProperty("occupation", careerTitle);
    }

    if(!specialties.isEmpty()) {
        userEntity.setProperty("specialties", specialties); 
    }

    if(userBio != null) {
        userEntity.setProperty("userBio", userBio);
    }

    if(wordCountJson != null) {
        userEntity.setProperty("bioMapJson", wordCountJson);
    }
    
    datastore.put(userEntity);
  }

  public Collection<User> fetchUserEntities(PreparedQuery results) {
    Collection<User> userEntities = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
        String name = (String) entity.getProperty("name");
        String id = (String) entity.getProperty("id");
        String email = (String) entity.getProperty("email");
        String school = (String) entity.getProperty("school");
        String major = (String) entity.getProperty("major");
        String company = (String) entity.getProperty("company");
        String occupation = (String) entity.getProperty("occupation");
        Collection<String> specialties = (Collection<String>) entity.getProperty("specialties");
        String userBio = (String) entity.getProperty("userBio");
        String bioMapJson = (String) entity.getProperty("bioMapJson");

        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        Map<String, Integer> bioMap = gson.fromJson(bioMapJson, type);

        User userObject = new User(name);
        if(id != null) {
            userObject.setId(id);
        }
        if(email != null) {
            userObject.setEmail(email);
        }
        if(school != null) {
            userObject.setSchool(school);
        }
        if(major != null) {
            userObject.setMajor(major);
        }

        if(specialties != null) {
            for (String specialty : specialties) {
                userObject.addSpecialty(specialty);
            }
        }
        if(company != null) {
            userObject.setCompany(company);
        }
        if(occupation != null) {
            userObject.setOccupation(occupation);
        }

        if(userBio != null) {
            userObject.setBio(userBio);
        }

        if(bioMap != null) {
            userObject.setBioMap(bioMap);
        }

        userEntities.add(userObject);
    }
    return userEntities;
  }

  //returns the bio map for given user
  public Map<String, Integer> getMapForUser(User user) {
    PreparedQuery storedUser = getQueryFilterForId(user.getId());
    Entity storedUserEntity = storedUser.asSingleEntity();
    String jsonMap =  (String)storedUserEntity.getProperty("bioMapJson");

    Type type = new TypeToken<Map<String, Integer>>(){}.getType();
    return gson.fromJson(jsonMap, type);
  }


  // function to fetch the user from the database
  public Collection<User> fetchUserProfiles() {
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);
    Collection<User> userProfiles = fetchUserEntities(results);
    return userProfiles;
  }

  // function that sets a query filter for the results in the datastore
  public PreparedQuery getQueryFilterForName(String name) {
    Filter userNameFilter = new FilterPredicate("name", FilterOperator.EQUAL, name);
    Query query = new Query("User").setFilter(userNameFilter);
    PreparedQuery results = datastore.prepare(query);
    return results;
  }
   // function that sets a query filter for the results in the datastore
  public PreparedQuery getQueryFilterForId(String id) {
    Filter userNameFilter = new FilterPredicate("id", FilterOperator.EQUAL, id);
    Query query = new Query("User").setFilter(userNameFilter);
    PreparedQuery results = datastore.prepare(query);
    return results;
  }
  
  // function that fetches a single user, collection of users, filter on that user for name
  public Collection<User> fetchUsersWithName(String userName) {
    PreparedQuery results = getQueryFilterForName(userName);
    Collection<User> userProfiles = fetchUserEntities(results);
    return userProfiles;
  }
  
  // function that fetches a single user, collection of users, filter on that user for id
  public User fetchUserWithId(String userId) {
    PreparedQuery results = getQueryFilterForId(userId);
    Collection<User> userProfiles = fetchUserEntities(results);
    if(userProfiles.size() == 1){
        return userProfiles.iterator().next();
    }
    return null;
  }

  // function that adds user to the database if it does not exist already
  public void addUser(User user) {
    String userId = user.getId();
    User userProfile = fetchUserWithId(userId);
    /*
     * if the userProfiles is larger than 1, that means the 
     * if it doesnt exist then it gets added
    */
    if(userProfile == null) {
        addUserToDatabase(user);
    }
  }

  public void removeUserProfile(User user) throws Exception {
    String inputUserId = user.getId();
    PreparedQuery results = getQueryFilterForId(inputUserId);
    int size = results.countEntities();

    /*
     *  if size is greater than 0 then there are entities available,
     *  if size is 0 then users do not equal, so an exception is thrown
    */
    if(size > 0){
        for (Entity entity : results.asIterable()) {
            String currentId = (String) entity.getProperty("id");
            if(inputUserId.equals(currentId)) {
                Key current = entity.getKey();
                datastore.delete(current);
            }

        }
    } else {
        throw new Exception("Can't remove " + user.getName() + " not found in datastore.");
    }
      
  }
  public void removeUser(User user) throws Exception {
        removeUserProfile(user);
  }

  public Collection<User> getAllUsers() {
    Collection<User> results = fetchUserProfiles();
    return results;
  }

  public String toString() {
    StringBuilder toReturn = new StringBuilder();
    Collection <User> allUsers = getAllUsers();
    for(User user : allUsers) {
        toReturn.append(user.toString());
        toReturn.append(" ");
    }
    return toReturn.toString();
  }

}
