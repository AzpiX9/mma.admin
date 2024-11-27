package org.helmo.mma.admin.infrastructures;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDbRepositoryTest {

    private static final String GIVEN_PATH = "jdbc:sqlite:src/test/resources/dbTests.sqlite";
    private Connection connection;
    private UserDbRepository userDbRepository;

    @BeforeEach
    void setUp() {
        try {
            connection = DriverManager.getConnection(GIVEN_PATH);
            for (var query : DbTestUtils.insertAllMembers()){
                connection.createStatement().executeUpdate(query);
            }
            userDbRepository = new UserDbRepository(connection);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS Member;");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnAllMembersAndSize(){
        var allUsers = userDbRepository.getUsers();

        assertEquals(DbTestUtils.insertAllMembers().size() - 1,allUsers.size());
    }

    @Test
    public void shouldReturnAnyMemberByItsId(){
        var anyId = "B234567";
        var thatUser = userDbRepository.getUser(anyId);
        assertTrue(userDbRepository.exists(anyId));
        assertEquals("Marie Dubois",thatUser.Prenom()+" "+thatUser.Nom());
        assertEquals("m.dubois@helmo.be",thatUser.Email());
        assertEquals(anyId,thatUser.Matricule());
    }

    @Test
    public void shouldReturnNullByInvalidId(){
        var anyId = "X999999";
        var thatUser = userDbRepository.getUser(anyId);

        assertNull(thatUser);
        assertFalse(userDbRepository.exists(anyId));
    }

}