package org.helmo.mma.admin.infrastructures;

import org.helmo.mma.admin.domains.core.User;
import org.helmo.mma.admin.domains.exceptions.UserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFileRepositoryTest {

    private File fileTest;
    private List<User> expectedUsers = new ArrayList<>();
    private final String VALID_PATH = "src/test/resources/users.csv";

    @Test
    public void shouldThrows_Exception_WhenPathIsEmpty() {
        buildFile("");
        var userReader = new UserFileRepository("");
        UserException userException = assertThrows(UserException.class,userReader::getUsers);
        assertEquals(userException.getMessage(),"Aucun fichier utilisateur");
    }

    @Test
    public void shouldRetrieve_DataFromFile_AndChecksItsSize() {
        buildFile(VALID_PATH);
        var userReader = new UserFileRepository(VALID_PATH);

        try {
            assertTrue(!userReader.getUsers().isEmpty());
            assertTrue(userReader.getUsers().size() == 3);
            assertEquals(expectedUsers,userReader.getUsers());
        } catch (UserException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void shouldRetrieve_OneUserAndItsValues() {
        buildFile(VALID_PATH);
        var userReader = new UserFileRepository(VALID_PATH);
        var matriculeGiven = "A123456";

        var expected = new User("A123456","Jan","Doput","dpt@helmo.be");
        var actual = userReader.getUser(matriculeGiven);

        assertEquals(expected,actual);
    }

    @Test
    public void userGiven_ShouldExist(){
        buildFile(VALID_PATH);
        var userReader = new UserFileRepository(VALID_PATH);
        var matriculeGiven = "A123456";
        var actual = userReader.exists(matriculeGiven);
        var existingUser = userReader.getUser(matriculeGiven);

        assertTrue(actual);
        var expected = new User("A123456","Jan","Doput","dpt@helmo.be");
        assertEquals(expected,existingUser);

    }

    @Test
    public void userGiven_ShouldNotExist(){
        buildFile(VALID_PATH);
        var userReader = new UserFileRepository(VALID_PATH);
        var matriculeGiven = "X000000";
        var actual = userReader.exists(matriculeGiven);
        assertFalse(actual);
    }

    private void buildFile(String path){
        if(path.isEmpty() || path.isBlank()){
            return;
        }
        expectedUsers.add(new User("A123456","Jan","Doput","dpt@helmo.be"));
        expectedUsers.add(new User("B234567","Mary","Dubs","dbs@helmo.be"));
        expectedUsers.add(new User("C345678","Pier","Marin","mrn@helmo.be"));
        fileTest = new File(path);
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileTest.getPath()))){
            for (User user : expectedUsers) {
                bw.write(String.format("%s;%s;%s;%s",user.Matricule(),user.Nom(),user.Prenom(),user.Email()));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterEach
    void tearDown(){
        if(fileTest == null){
            return;
        }
        if(!fileTest.exists()){
            return;
        }
        fileTest.delete();
    }



}