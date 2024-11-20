package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.SevicesRepository;

import java.sql.Connection;
import java.util.Objects;

public class ServicesDbRepository implements SevicesRepository {

    private Connection connection;

    public ServicesDbRepository(Connection connection) {
        this.connection = Objects.requireNonNull(connection);
        // readTo();
    }
}
