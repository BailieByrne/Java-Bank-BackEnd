package Users;






import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import jakarta.validation.constraints.*;
import lombok.Getter;




@Table("users")
public record User(
		@Id
		@Column("CustomerId")
		Integer id,
		@Query
		@Column("RegisteredKeeper")
		@NotEmpty
		String registeredKeeper,
		@Column("Roles")
		String Roles,
		@Column("Passw")
		@NotEmpty
		String password
		) {

	public User withHiddenPassword() {
        return new User(this.id, this.registeredKeeper, this.Roles, "Hidden");
    }
}
	


