package com.google.sps;

import static org.mockito.Mockito.when;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.sps.servlets.UserLoginServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class UserLoginTest {

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private UserLoginServlet userServlet;

  private LocalServiceTestHelper helperLoggedIn =
    new LocalServiceTestHelper(new LocalUserServiceTestConfig())
        .setEnvIsAdmin(true).setEnvIsLoggedIn(true);
  
  private LocalServiceTestHelper helperLoggedOut =
    new LocalServiceTestHelper(new LocalUserServiceTestConfig())
        .setEnvIsAdmin(true).setEnvIsLoggedIn(false);
  
  private JsonObject getLoginServletResponse() throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    userServlet = new UserLoginServlet();
    userServlet.doGet(request, response);

    String response = stringWriter.getBuffer().toString().trim();
    JsonElement responseJsonElement = new JsonParser().parse(response);
    JsonObject responseJsonObject = responseJsonElement.getAsJsonObject();
    
    return responseJsonObject;
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  

  @Test
  public void userLoggedInReturningLogOut() throws ServletException, IOException  {

    helperLoggedIn.setUp();
    
    JsonObject responseJsonObject = getLoginServletResponse();
    String logInUrl = responseJsonObject.get("LogInUrl").getAsString();
    String logOutUrl = responseJsonObject.get("LogOutUrl").getAsString();
   
    Assert.assertFalse(logOutUrl.isEmpty());
    Assert.assertTrue(logOutUrl.contains("logout"));
    Assert.assertTrue(logInUrl.isEmpty());
    helperLoggedIn.tearDown();
  }

   @Test
  public void userLoggedOutReturningLogIn() throws ServletException, IOException  {
    helperLoggedOut.setUp();

    JsonObject responseJsonObject = getLoginServletResponse();
    String logInUrl = responseJsonObject.get("LogInUrl").getAsString();
    String logOutUrl = responseJsonObject.get("LogOutUrl").getAsString();

    Assert.assertFalse(logInUrl.isEmpty()) ;
    Assert.assertTrue(logInUrl.contains("login"));
    Assert.assertTrue(logOutUrl.isEmpty());
    helperLoggedOut.tearDown();
  }
}