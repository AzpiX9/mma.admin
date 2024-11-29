package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.booking.CalendarRepository;
import org.helmo.mma.admin.domains.services.BaseStorage;
import org.helmo.mma.admin.domains.services.SevicesRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLStorage implements BaseStorage {

    private final Connection connection;
    private final SevicesRepository sevicesRepository;
    private final CalendarRepository calendarRepository;

    public SQLStorage(Connection connection) {
        this.connection = connection;
        this.sevicesRepository = new ServicesDbRepository(connection);
        this.calendarRepository = new CalDbRepository(connection);
    }

    @Override
    public SevicesRepository getSevicesRepository() {
        return sevicesRepository;
    }

    @Override
    public CalendarRepository getCalendarRepository() {
        return calendarRepository;
    }

    @Override
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollbackTransaction() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commitTransaction() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
