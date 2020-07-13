package com.google.sps.algorithms;

import com.google.sps.data.MatchRepository;
import com.google.sps.data.MatchRequest;
import com.google.sps.data.NonPersistentUserRepository;
import com.google.sps.data.User;
import java.util.ArrayList;
import java.util.Collection;


public final class MatchQuery {

  /**
  * This method takes a MatchRequest and a Collection of users that are already saved and returns 
  * all Users in the User Repository that match the criteria in MatchRequest AND are not already
  * saved in the userSavedMatches collection.
  */
  public Collection<User> query(MatchRequest request, Collection<User> userSavedMatches) {
    //Access User Repository
    NonPersistentUserRepository mockRepo = new NonPersistentUserRepository();
    mockRepo.addFakeMentors();

    Collection<User> mockMentors = mockRepo.getAllUsers();
    Collection<User> mentorMatches = new ArrayList<User>();

    String matchCriteria = request.getCriteria();

    //return empty collection if there is no criteria
    if(matchCriteria == "") {
      return mentorMatches;
    }

    for(User potentialMentor : mockMentors) {
        Collection<String> mentorSpecialties = potentialMentor.getSpecialties();

        //see if new mentor is not already saved AND contains correct criteria
        if(!(userSavedMatches.contains(potentialMentor)) && (mentorSpecialties.contains(matchCriteria))) {
            mentorMatches.add(potentialMentor);
        }
    }
    
    return mentorMatches;
  }
}
