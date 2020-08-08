package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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

/** Servlet responsible for get comments sorted by likes*/
@WebServlet("/comment_likes")
public class DataServlet extends HttpServlet {

  @Override
  /** Get all comments from datastore and sort it*/
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("likes", SortDirection.DESCENDING);

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

}
