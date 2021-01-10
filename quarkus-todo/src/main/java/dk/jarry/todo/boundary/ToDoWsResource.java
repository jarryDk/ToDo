package dk.jarry.todo.boundary;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import dk.jarry.todo.entity.ToDo;


@ServerEndpoint("/ws/todos")         
@ApplicationScoped
public class ToDoWsResource {
	
	enum WsActions {
		CREATE,
		READ,
		UPDATE,
		DELETE;
		
	}

	List<Session> sessions = new ArrayList<Session>();
	
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message) {
    }
    
   
    @Incoming("todoCreate")
    public void create(ToDo toDo){
        broadcast(toDo, WsActions.CREATE);   
    }
    
    @Incoming("todoRead")
    public void read(ToDo toDo){
        broadcast(toDo, WsActions.READ);   
    }
    
    @Incoming("todoUpdate")
    public void update(ToDo toDo){
        broadcast(toDo, WsActions.UPDATE);   
    }
    
    @Incoming("todoDelete")
    public void delete(ToDo toDo){
        broadcast(toDo, WsActions.DELETE);   
    }

    public void broadcast(ToDo toDo, WsActions action) {

        Jsonb jsonb = JsonbBuilder.create();
        String jsonString = jsonb.toJson(toDo);

        JsonObject toDoAsJson = Json.createReader(new StringReader(jsonString)).readObject();

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();

        createObjectBuilder.add("toDo", toDoAsJson);
        createObjectBuilder.add("action", action.name());
        
        broadcast(createObjectBuilder.build().toString());
    }


    public void broadcast(String message) {
        sessions.forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }
    
}