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
    
    String userId_like = userService.getCurrentUser().getUserId();
    long postId_like = request.getParameter("postId");

    Entity likesEntity = new Entity("Likes");
    likesEntity.setProperty("userId", userId_like);
    likesEntity.setProperty("postId", postId_like);
	
    bool same_entry = false;

    /** Check for same entry and then add to datastore*/
    Query<Entity> query_likes = Query.newEntityQueryBuilder().setKind("Likes").setFilter(PropertyFilter.eq("userId", userId_like), PropertyFilter.eq("postId", postId_like)).build();
	
    if(query_likes!=NULL)
      same_entry = true;
    else
      datastore.put(likesEntity);

	
    Query query_comment = new Query("Comment");
    PreparedQuery results_comments = datastore.prepare(query_comment);
	
    /** Increase the number of likes for a comment by a user for the first time and update in datastore */
    for (Entity entity : results_comments.asIterable()) {
      long id = entity.getKey().getId();
      long likes = (long) entity.getProperty("likes");
      String userId_comment = (String) entity.getProperty("userId");
      if(id==postId_like && !same_entry)
        likes = likes + 1;  
      long createdAt = (long) entity.getProperty("createdAt");
      String comment = (String) entity.getProperty("comment");
      String websiteURL = (String) entity.getProperty("websiteURL");
	  
      Entity commentEntity = new Entity("Comment", id);
      commentEntity.setProperty("comment", comment);
      commentEntity.setProperty("createdAt", createdAt);
      commentEntity.setProperty("userId", userId_comment);
      commentEntity.setProperty("likes", likes);
      commentEntity.setProperty("websiteURL", websiteURL);
	  
      datastore.put(commentEntity);
    }
    response.sendRedirect("/comment");
  }
}
