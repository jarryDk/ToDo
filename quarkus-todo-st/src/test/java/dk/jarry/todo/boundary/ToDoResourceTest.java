package dk.jarry.todo.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import dk.jarry.todo.control.ChuckNorrisJokes;
import dk.jarry.todo.control.ToDoResourceClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ToDoResourceTest {

    @Inject
    @RestClient
    ToDoResourceClient resourceClient;

    @Test
    public void create() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", getSubject());
        createObjectBuilder.add("body", getBody());
        JsonObject todoInput = createObjectBuilder.build();

        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("create - Todo " + todoOutput);

    }

    @Test
    public void read() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", getSubject());
        createObjectBuilder.add("body", getBody());

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("read - Todo [1] " + todoOutput);

        UUID uuid = UUID.fromString(todoOutput.getString("uuid"));

        todoOutput = this.resourceClient.read(uuid);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("read - Todo [2] " + todoOutput);

    }

    @Test
    public void update() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", getSubject());
        createObjectBuilder.add("body", getBody());

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("update - Todo [1] " + todoOutput);

        UUID uuid = UUID.fromString(todoOutput.getString("uuid"));

        JsonObjectBuilder todoUpdateBuilder = Json.createObjectBuilder(todoOutput);
        todoUpdateBuilder.add("subject", "new subject");
        var todoUpdated = todoUpdateBuilder.build();

        todoOutput = this.resourceClient.update(uuid, todoUpdated);

        assertEquals(todoUpdated.getString("subject"), "new subject");
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("update - Todo [2] " + todoOutput);

    }

    @Test
    public void delete() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", getSubject());
        createObjectBuilder.add("body", getBody());

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("delete- Todo " + todoOutput);

        UUID uuid = UUID.fromString(todoOutput.getString("uuid"));

        this.resourceClient.delete(uuid);

        try {
            todoOutput = this.resourceClient.read(uuid);
        } catch (jakarta.ws.rs.WebApplicationException we) {
            assertTrue(we.getResponse().getStatus() == 404);
        }
    }

    String getSubject() {
        return "Subject - quarkus-todo-st - Timestamp : " + ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
    }

    String getBody() {
        return ChuckNorrisJokes.getInstance().getRandomJoke();
    }

}