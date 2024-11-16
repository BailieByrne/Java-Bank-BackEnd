package Account;

import java.time.LocalDateTime;
import org.apache.logging.log4j.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.example.demo.DemoApplication;
import jakarta.validation.constraints.*;

@Table("checkingaccounts")
public record CheckingAcc(
    @Id
    @Column("id")
    Integer id,
    @Column("RegisteredKeeper")
    @NotEmpty
    String RegisteredKeeper,
    @Column("AccountCreatedOn")
    LocalDateTime AccountCreatedOn,
    @Column("Balance")
    @PositiveOrZero
    double Balance,
    @Column("OwnerID")
    Integer ownerID
) {
    private static final Logger log = DemoApplication.log;

    public CheckingAcc {
        if (Balance < 0) {
            log.error("Balance Error For ID " + id);
            throw new IllegalArgumentException("Invalid Balance");
        }
        if (RegisteredKeeper.isBlank()) {
            log.error("Keeper Error For ID " + id);
            throw new IllegalArgumentException("Invalid Keeper");
        }
    }
}
