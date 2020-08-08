package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for get(all comments), post */
@WebServlet("/comment")
public class DataServlet extends HttpServlet {

  @Override
  /** Get all comments from datastore and sort it in descending order of time posted */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("createdAt", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {

      long id = entity.getKey().getId();
      long likes = (long) entity.getProperty("likes");
      long createdAt = (long) entity.getProperty("createdAt");
      String userId = (String) entity.getProperty("userId");
      String comment = (String) entity.getProperty("comment");
      String websiteURL = (String) entity.getProperty("websiteURL");
      
      Comment comment = new Comment(id, likes, createdAt, userId, comment, websiteURL);
      comments.add(comment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  /** Post a new comment to datastore */
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/authentication");
      return;
    }
    String comment = request.getParameter("comment");
    String websiteURL = request.getParameter("websiteURL");
    long createdAt = System.currentTimeMillis();
    String userId = userService.getCurrentUser().getUserId();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("createdAt", createdAt);
    commentEntity.setProperty("userId", userId);
    commentEntity.setProperty("likes", 0);
    commentEntity.setProperty("websiteURL", websiteURL);

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    datastoreService.put(commentEntity);

    response.sendRedirect("/comment");
  }
}
