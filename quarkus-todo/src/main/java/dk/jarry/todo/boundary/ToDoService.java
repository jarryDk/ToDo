package dk.jarry.todo.boundary;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
// import java.security.Principal;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import dk.jarry.todo.entity.ToDo;

@Traced
@RequestScoped
public class ToDoService {

	@Inject
	EntityManager entityManager;

	@Inject
	@Channel("todoCreate")
	Emitter<ToDo> toDoCreateChannel;

	@Inject
	@Channel("todoRead")
	Emitter<ToDo> todoReadChannel;

	@Inject
	@Channel("todoUpdate")
	Emitter<ToDo> todoUpdateChannel;

	@Inject
	@Channel("todoDelete")
	Emitter<ToDo> todoDeleteChannel;

	public ToDoService() {
	}

	@Transactional
	public ToDo create(ToDo toDo) {
		if (toDo.uuid != null) {
			throw new WebApplicationException( //
					"ToDo not valid.", //
					Response.Status.BAD_REQUEST);
		}
		toDo.createdDate = ZonedDateTime.now();

		entityManager.persist(toDo);
		entityManager.flush();
		entityManager.refresh(toDo);

		this.toDoCreateChannel.send(toDo);

		return toDo;
	}

	@Transactional
	public ToDo read(UUID uuid) {
		ToDo toDo = entityManager.find(ToDo.class, uuid);
		if (toDo != null) {
			this.todoReadChannel.send(toDo);
			return toDo;
		} else {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	@Transactional
	public ToDo update(UUID uuid, ToDo toDo) {
		ToDo findToDo = entityManager.find(ToDo.class, uuid);
		if (findToDo != null) {
			toDo.updatedDate = ZonedDateTime.now();
			this.todoUpdateChannel.send(toDo);
			return entityManager.merge(toDo);
		} else {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	@Transactional
	public void delete(UUID uuid) {
		ToDo findToDo = entityManager.find(ToDo.class, uuid);
		if (findToDo != null) {
			this.todoDeleteChannel.send(findToDo);
			entityManager.remove(findToDo);
		} else {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	@Transactional
	public List<ToDo> list(Long from, Long limit) {
		return entityManager
				.createNamedQuery("ToDos.findAll", ToDo.class)
				.getResultList();
	}

	@Provider
	public static class ErrorMapper implements ExceptionMapper<Exception> {

		@Override
		public Response toResponse(Exception exception) {
			int code = 500;
			if (exception instanceof WebApplicationException) {
				code = ((WebApplicationException) exception).getResponse().getStatus();
			}
			return Response.status(code).entity(Json.createObjectBuilder() //
					.add("error", (exception.getMessage() != null ? exception.getMessage() : "")) //
					// .add("stackTrace", stackTrace(exception)) //
					.add("code", code).build()).build();
		}

		String stackTrace(Exception exception) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			exception.printStackTrace(printWriter);
			return writer.toString();
		}

	}
}
