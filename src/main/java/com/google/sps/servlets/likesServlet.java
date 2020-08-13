// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Manages number of likes for a comment from a user */
@WebServlet("/likes")
public class DataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /** If user is not logged in redirect to authentication */
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/authentication");
      return;
    }
	  
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
    String userIdLike = userService.getCurrentUser().getUserId();
    long postIdLike = request.getParameter("postId");
	
    Entity likesEntity = new Entity("Likes");
    likesEntity.setProperty("userId", userIdLike);
    likesEntity.setProperty("postId", postIdLike);
	
    bool sameEntry = false;

    /** Check for same entry and then add to datastore*/
    try{
      Query<Entity> queryLike = Query.newEntityQueryBuilder()
       		         .setKind("Likes")
		         .setFilter(PropertyFilter.eq("userId", userIdLike), PropertyFilter.eq("postId", postIdLike))
		         .build();
	
      if(queryLike!=NULL){
        sameEntry = true;
      }
      else{
	datastore.put(likesEntity);
      }
    }
    catch(Exception e){
      response.getWriter().println("Error while querying Likes entity : " + e);	
    }

    try{
      Query queryComment = new Query("Comment");
      PreparedQuery resultsComments = datastore.prepare(queryComment);
	
      /** Increase the number of likes for a comment by a user for the first time and update in datastore */
      for (Entity entity : resultsComments.asIterable()) {
        long id = entity.getKey().getId();
	long likes = (long) entity.getProperty("likes");
	String userIdComment = (String) entity.getProperty("userId");
	if(id==postIdLike && !sameEntry){
	  likes = likes + 1;  
	}
	long createdAt = (long) entity.getProperty("createdAt");
        String comment = (String) entity.getProperty("comment");
        String websiteURL = (String) entity.getProperty("websiteURL");
	  
	Entity commentEntity = new Entity("Comment", id);
        commentEntity.setProperty("comment", comment);
        commentEntity.setProperty("createdAt", createdAt);
        commentEntity.setProperty("userId", userIdComment);
        commentEntity.setProperty("likes", likes);
        commentEntity.setProperty("websiteURL", websiteURL);
	  
	datastore.put(commentEntity);
      }
    }
    catch(Exception e){
      response.getWriter().println("Error while querying Comments entity : " + e);
    }
	  
    response.sendRedirect("/comment");
  }
}
