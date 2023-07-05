package dk.jarry.todo.boundary;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dk.jarry.todo.entity.ToDo;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

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
	@RolesAllowed("todo:write")
	@Operation( //
			description = "Create a new todo", //
			operationId = "create")
	public ToDo create(ToDo toDo) {
		registry.counter("createPerformed", "type", "natural")
				.increment();
		Timer timer = registry.timer("createTimer");
		return timer.record(
				() -> toDoService.create(toDo));
	}

	@GET
	@Path("{uuid}")
	@RolesAllowed("todo:read")
	@Operation( //
			description = "Get a specific todo by id", //
			operationId = "readByUuid")
	public ToDo read(@PathParam("uuid") UUID uuid) {
		return toDoService.read(uuid);
	}

	@PUT
	@Path("{uuid}")
	@RolesAllowed("todo:write")
	@Operation( //
			description = "Update an exiting todo", //
			operationId = "updateByUuid")
	public ToDo update(@PathParam("uuid") UUID uuid, ToDo toDo) {
		return toDoService.update(uuid, toDo);
	}

	@DELETE
	@Path("{uuid}")
	@RolesAllowed("todo:write")
	@Operation( //
			description = "Delete a specific todo", //
			operationId = "deleteByUuid")
	public void delete(@PathParam("uuid") UUID uuid) {
		toDoService.delete(uuid);
	}

	@GET
	@RolesAllowed("todo:read")
	@Operation( //
			description = "Get all the todos", //
			operationId = "list")
	public List<ToDo> list( //
			@QueryParam("from") Long from, //
			@QueryParam("limit") Long limit) {
		return toDoService.list(from, limit);
	}

}
