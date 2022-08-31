package dk.jarry.todo.boundary;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dk.jarry.todo.entity.ToDo;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Path("todos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Todo Resource", description = "All Todo Operations")
public class ToDoResource {

	@Inject
	ToDoService toDoService;

	private final MeterRegistry registry;

	ToDoResource(MeterRegistry registry) {
		this.registry = registry;
	}

	@POST
	@RolesAllowed("user")
	@Operation(description = "Create a new todo")
	public ToDo create(ToDo toDo) {
		registry.counter("createPerformed", "type", "natural")
			.increment();
		Timer timer = registry.timer("createTimer");
		return timer.record(
				() -> toDoService.create(toDo));
	}

	@GET
	@Path("{uuid}")
	@PermitAll
	@Operation(description = "Get a specific todo by id")
	public ToDo read(@PathParam("uuid") UUID uuid) {
		return toDoService.read(uuid);
	}

	@PUT
	@Path("{uuid}")
	@RolesAllowed("user")
	@Operation(description = "Update an exiting todo")
	public ToDo update(@PathParam("uuid") UUID uuid, ToDo toDo) {
		return toDoService.update(uuid, toDo);
	}

	@DELETE
	@Path("{uuid}")
	@PermitAll
	@Operation(description = "Delete a specific todo")
	public void delete(@PathParam("uuid") UUID uuid) {
		toDoService.delete(uuid);
	}

	@GET
	@PermitAll
	@Operation(description = "Get all the todos")
	public List<ToDo> list( //
			@QueryParam("from") Long from, //
			@QueryParam("limit") Long limit) {
		return toDoService.list(from, limit);
	}

}
