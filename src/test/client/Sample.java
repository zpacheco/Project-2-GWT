package test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import java.util.ArrayList;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.user.client.ui.Frame;

public class Sample implements EntryPoint, ClickHandler
{
   private static class MyWorker
   {
      private final String name;
      private final String username;
      private final String department;
      private final int id;
      
      public MyWorker(String nameStr, String user, String dept, int id)
      {
         name = nameStr;
         username = user;
         department = dept;
         this.id = id;
      }
   }
   ArrayList<MyWorker> workers = new ArrayList<MyWorker>();
   JsArray<Worker> jsonData;
   VerticalPanel mainPanel = new VerticalPanel();
   Button addButton = new Button("Add worker");
   Button addSubmitButton = new Button("Add");
   Button editButton = new Button("Edit worker");
   Button editSubmitButton = new Button ("Modify");
   Button deleteButton = new Button("Delete worker");
   Button loginButton = new Button("Login");
   Button loginSubmitButton = new Button("Route");
   TextBox nameBox = new TextBox();
   TextBox userBox = new TextBox();
   PasswordTextBox passBox = new PasswordTextBox();
   TextBox deptBox = new TextBox();
   
   MyWorker selectedWorker;
   public void onModuleLoad()
   {
      RootPanel.get().add(mainPanel);
      addButton.addClickHandler(this);
      addSubmitButton.addClickHandler(this);
      editSubmitButton.addClickHandler(this);
      editButton.addClickHandler(this);
      deleteButton.addClickHandler(this);
      loginButton.addClickHandler(this);
      showLoginForm();
      
   }
   public void onClick(ClickEvent e) {
      Object source = e.getSource();
      if (source == addButton) {
         showEditForm("","","","",false);
      }
      else if (source == addSubmitButton) {
         String encData = URL.encode("name") + "=" +
            URL.encode(nameBox.getText()) + "&" +
            URL.encode("username") + "=" +
            URL.encode(userBox.getText()) + "&" +
            URL.encode("password") + "=" +
            URL.encode(passBox.getText()) + "&" +
            URL.encode("department") + "=" +
            URL.encode(deptBox.getText());
         String url = "http://localhost:3000/workers/create";
         postRequest(url,encData);
      }
      else if (source == editButton)
      {
         showEditForm(selectedWorker.name,selectedWorker.username,""
               ,selectedWorker.department,true);
      }
      else if (source == editSubmitButton) {
         int id = selectedWorker.id;
         String encData = URL.encode("name") + "=" +
               URL.encode(nameBox.getText()) + "&" +
               URL.encode("username") + "=" +
               URL.encode(userBox.getText()) + "&" +
               URL.encode("password") + "=" +
               URL.encode(passBox.getText()) + "&" +
               URL.encode("department") + "=" +
               URL.encode(deptBox.getText()) + "&" +
               URL.encode("id") + "=" +
               URL.encode("" + id);
            String url = "http://localhost:3000/workers/update";
            postRequest(url,encData);
            
      }
      else if (source == deleteButton) {
         int id = selectedWorker.id;
         String encData = URL.encode("id") + "=" +
         URL.encode("" + id);
         String url = "http://localhost:3000/workers/destroy";
         postRequest(url,encData);
      }
      else if (source == loginButton) {
         String encData = URL.encode("username") + "=" +
               URL.encode(userBox.getText()) + "&" +
               URL.encode("password") + "=" +
               URL.encode(passBox.getText());
            String url = "http://localhost:3000/workers/login";
            postLoginRequest(url,encData);
            
      }
   }
   private void postRequest(String url, String data)
   {
      final RequestBuilder rb =
         new RequestBuilder(RequestBuilder.POST,url);
      rb.setHeader("Content-type",
               "application/x-www-form-urlencoded");
      try {
         rb.sendRequest(data, new RequestCallback()
         {
            public void onError(final Request request,
               final Throwable exception)
            {
               Window.alert(exception.getMessage());
            }
            public void onResponseReceived(final Request request,
               final Response response)
            {
               String url = "http://localhost:3000/workers.json";
               getRequest(url);
            }
         });
      }
      catch (final Exception e) {
         Window.alert(e.getMessage());
      }
   }
    private void postLoginRequest(String url, String data)
      {
         final RequestBuilder rb =
            new RequestBuilder(RequestBuilder.POST,url);
         rb.setHeader("Content-type",
                  "application/x-www-form-urlencoded");
         try {
            rb.sendRequest(data, new RequestCallback()
            {
               public void onError(final Request request,
                  final Throwable exception)
               {
                  Window.alert(exception.getMessage());
               }
               public void onResponseReceived(final Request request,
                  final Response response)
               {
                  int id = Integer.parseInt(response.getText());
                  if (id == 1) {
                     String url = "http://localhost:3000/workers.json";
                     getRequest(url);
                  }
                  else if (id > 1) {
                     Frame frame = new Frame("http://localhost:3000/workshops/summary");
                     RootPanel.get().add(frame);
                     mainPanel.clear();
                     mainPanel.add(frame);
                     //Window.alert("" + id);
                  }
                  else {
                     userBox.setText("");
                     passBox.setText("");
                  }
               }
            });
         }
      catch (final Exception e) {
         Window.alert(e.getMessage());
      }
   }
   private void getRequest(String url)
   {
      final RequestBuilder rb =
         new RequestBuilder(RequestBuilder.GET,url);
      try {
         rb.sendRequest(null, new RequestCallback()
         {
            public void onError(final Request request,
               final Throwable exception)
            {
               Window.alert(exception.getMessage());
            }
            public void onResponseReceived(final Request request,
               final Response response)
            {
               String text = response.getText();
               showWorkersCellTable(text);
            }
         });
      }
      catch (final Exception e) {
         Window.alert(e.getMessage());
      }
   }
   private void showEditForm(String nameStr,
         String userStr, String passStr,
         String deptStr, boolean editing)
   {
      VerticalPanel editPanel = new VerticalPanel();
      HorizontalPanel row1 = new HorizontalPanel();
      Label nameLabel = new Label("Name: ");
      row1.add(nameLabel);
      row1.add(nameBox);
      nameBox.setText(nameStr);
      editPanel.add(row1);
      HorizontalPanel row2 = new HorizontalPanel();
      Label userLabel = new Label("Username: ");
      row2.add(userLabel);
      row2.add(userBox);
      userBox.setText(userStr);
      editPanel.add(row2);
      HorizontalPanel row3 = new HorizontalPanel();
      Label passLabel = new Label("Password: ");
      row3.add(passLabel);
      row3.add(passBox);
      passBox.setText(passStr);
      editPanel.add(row3);
      HorizontalPanel row4 = new HorizontalPanel();
      Label deptLabel = new Label("Department: ");
      row4.add(deptLabel);
      row4.add(deptBox);
      deptBox.setText(deptStr);
      editPanel.add(row4);
      if(editing) {
         editPanel.add(editSubmitButton);
      }
      else {
         editPanel.add(addSubmitButton);
      }
      mainPanel.clear();
      mainPanel.add(editPanel);
   }
   private void showLoginForm()
   {
      VerticalPanel editPanel = new VerticalPanel();
      HorizontalPanel row1 = new HorizontalPanel();
      Label userLabel = new Label("Username: ");
      row1.add(userLabel);
      row1.add(userBox);
      editPanel.add(row1);
      HorizontalPanel row2 = new HorizontalPanel();
      Label passLabel = new Label("Password: ");
      row2.add(passLabel);
      row2.add(passBox);
      editPanel.add(row2);
      editPanel.add(loginButton);
      mainPanel.clear();
      mainPanel.add(editPanel);
      
   }
   private JsArray<Worker> getJSONData(String json)
   {
      return JsonUtils.safeEval(json);
   }
   private void showWorkersCellTable(String json)
   {
      jsonData = getJSONData(json);
      workers = new ArrayList<MyWorker>();
      Worker worker = null;
      for (int i = 1; i < jsonData.length(); i++) {
         worker = jsonData.get(i);
         String name = worker.getName();
         String username = worker.getUsername();
         String department = worker.getDepartment();
         int id = worker.getId();
         MyWorker w = new MyWorker(name,username,department,id);
         workers.add(w);
      }
      TextColumn<MyWorker> nameCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
               return worker.name;
            }
         };
      TextColumn<MyWorker> usernameCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
               return worker.username;
            }
         };
      TextColumn<MyWorker> deptCol =
         new TextColumn<MyWorker>()
         {
            @Override
            public String getValue(MyWorker worker)
            {
            return worker.department;
            }
         };
      CellTable<MyWorker> table =
         new CellTable<MyWorker>();
      final SingleSelectionModel<MyWorker> selectionModel =
            new SingleSelectionModel<MyWorker>();
      table.setSelectionModel(selectionModel);
      selectionModel.addSelectionChangeHandler(
            new SelectionChangeEvent.Handler()
            {
               public void onSelectionChange(SelectionChangeEvent e)
               {
                  MyWorker choice = selectionModel.getSelectedObject();
                  if (choice != null) {
                     selectedWorker = choice;
                  }
               }
            });
      table.addColumn(nameCol,"Name");
      table.addColumn(usernameCol,"Username");
      table.addColumn(deptCol,"Department");
      table.setRowCount(workers.size(),true);
      table.setRowData(0,workers);
      HorizontalPanel buttonPanel = new HorizontalPanel();
      buttonPanel.add(addButton);
      buttonPanel.add(editButton);
      buttonPanel.add(deleteButton);
      mainPanel.clear();
      mainPanel.add(buttonPanel);
      mainPanel.add(table);
   }
}