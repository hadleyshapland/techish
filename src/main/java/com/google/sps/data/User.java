package com.google.sps.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.algorithms.MatchRanking;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class User {
  public static enum ProfileType {
    MENTEE, MENTOR;
  };

  private String id;
  private String company;
  private String major;
  private String name;
  private String email;
  private String occupation;
  private ProfileType userType;
  private String school;
  private Collection<String> specialties;
  private String userBio;
  private Map<String, Integer> savedMatchWordCount;

  public User(String name) {
    this.name = name;
    specialties = new HashSet<String>();
    userBio = "[user did not add a bio]";
    savedMatchWordCount = new HashMap<String, Integer>();
  }

  public String getName() {
      return name;
  }
  public String toString() {
    return new Gson().toJson(this);
  } 

  public ProfileType toEnum(String input) {
      if(input.equalsIgnoreCase("MENTEE")) {
          input = "MENTEE";
      }
      else if(input.equalsIgnoreCase("MENTOR")) {
          input = "MENTOR";
      }
      else {
          System.err.println("Invalid input type");
      }
      
      ProfileType result = ProfileType.valueOf(input);
      return result;
  }
 
  public boolean equals(Object user) {
    if (user instanceof User) { 
        User toCompare = (User) user;
        return this.id.equals(toCompare.id);
    }
    return false;
  }

  //method to make .contains for HashSet work
  public int hashCode() {
      return id.hashCode();
  }

  public void addSpecialty(String toAdd) {
      specialties.add(toAdd);
  }

  public Collection<String> getSpecialties() {
      return specialties;
  }

  public void setProfileType(ProfileType input) {
      if(input == ProfileType.MENTEE || input == ProfileType.MENTOR){
        this.userType = input;
      }
      else {
          System.err.println("Invalid profile type");
      }
  }

  public ProfileType getProfileType() {
      return this.userType;
  }

  public void setId(String id) {
      this.id = id;
  }

  public String getId() {
      return id;
  }
 
  public void setSchool(String school) { 
      this.school = school;
  }
 
  public String getSchool() {
      return school;
  }

   public void setMajor(String major) { 
      this.major = major;
  }
 
  public String getMajor() {
      return major;
  }

  public void setCompany(String company) {
      this.company = company;
  }
  
  public String getCompany() {    
      return company;
  }

  public void setOccupation(String occupation) {
      this.occupation = occupation;
  }
  
  public String getOccupation() {    
      return occupation;
  }

  public void setEmail(String email) {
      this.email = email;
  }

  public String getEmail() {
      return email;
  }

  public void setBio(String userBio) {
      this.userBio = userBio;

      //update map that counts every user bio word in MatchRanking
      if(userBio != null) {
          MatchRanking.addToAllUserWordCount(userBio);
      }
  }

  public String getBio() {
      return userBio;
  }

  public Map<String, Integer> getBioMap() {
    return savedMatchWordCount;
  }

  public void setBioMap(Map<String, Integer> bioMap) {
    this.savedMatchWordCount = bioMap;
  }

}
