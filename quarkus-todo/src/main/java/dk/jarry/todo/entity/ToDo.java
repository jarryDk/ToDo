package dk.jarry.todo.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;

/**
 * @author Michael Bornholdt Nielsen
 */
@Entity
@Table(name = "todos")
@NamedQuery( //
		name = "ToDos.findAll", //
		query = "SELECT f FROM ToDo f ORDER BY f.subject", //
		hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Cacheable
public class ToDo {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "uuid", updatable = false, nullable = false)
	@Schema(readOnly = true)
	public UUID uuid;

	public String subject;

	public String body;

	public ZonedDateTime createdDate;
	public ZonedDateTime updatedDate;

	public ZonedDateTime startDate;
	public ZonedDateTime endDate;

	public Integer priority;
	public Integer importens;

	public String owner;

	public String createBy;
	public String updatedBy;

}
