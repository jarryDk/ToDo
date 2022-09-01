package dk.jarry.todo.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;

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
