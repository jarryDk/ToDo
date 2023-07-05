package dk.jarry.todo.boundary;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import dk.jarry.todo.entity.ToDo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

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
    public void create(ToDo toDo) {
        broadcast(toDo, WsActions.CREATE);
    }

    @Incoming("todoRead")
    public void read(ToDo toDo) {
        broadcast(toDo, WsActions.READ);
    }

    @Incoming("todoUpdate")
    public void update(ToDo toDo) {
        broadcast(toDo, WsActions.UPDATE);
    }

    @Incoming("todoDelete")
    public void delete(ToDo toDo) {
        broadcast(toDo, WsActions.DELETE);
    }

    void broadcast(ToDo toDo, WsActions action) {

        Jsonb jsonb = JsonbBuilder.create();
        String jsonString = jsonb.toJson(toDo);

        JsonObject toDoAsJson = Json.createReader(new StringReader(jsonString)).readObject();

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        objectBuilder.add("toDo", toDoAsJson);
        objectBuilder.add("action", action.name());

        broadcast(objectBuilder.build().toString());
    }

    void broadcast(String message) {

        sessions.forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

}