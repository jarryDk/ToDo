package dk.jarry.todo.entity;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

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
	@SequenceGenerator( //
			name = "todosSequence", //
			sequenceName = "todos_id_seq", //
			allocationSize = 1, //
			initialValue = 10)
	@GeneratedValue( //
			strategy = GenerationType.SEQUENCE, //
			generator = "todosSequence")
	@Schema(readOnly = true)
	public Long id;

	public String subject;
	
	public String body;

	public Date createdDate;
	public Date updatedDate;

	public Date startDate;
	public Date endDate;

	public Integer priority;
	public Integer importens;

	public String owner;
	
	public String createBy;
	public String updatedBy;
	
}
