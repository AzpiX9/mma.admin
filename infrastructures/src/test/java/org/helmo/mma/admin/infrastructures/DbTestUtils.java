package org.helmo.mma.admin.infrastructures;

import java.util.ArrayList;
import java.util.List;

public class DbTestUtils {

    public static List<String> insertAllRooms(){
        var roomAddQuery = new ArrayList<String>();
        roomAddQuery.add("CREATE TABLE Rooms(idRoom VARCHAR PRIMARY KEY,roomName VARCHAR,roomSize INTEGER NOT NULL);");
        roomAddQuery.add("INSERT INTO Rooms(idRoom, roomName, roomSize)VALUES ('LC1', 'Learning Center 1', 20);");
        roomAddQuery.add("INSERT INTO Rooms(idRoom, roomName, roomSize)VALUES ('LC2', 'Learning Center 2', 20);");
        roomAddQuery.add("INSERT INTO Rooms(idRoom, roomName, roomSize)VALUES ('LB1', 'Learning Box 1', 5);");
        roomAddQuery.add("INSERT INTO Rooms(idRoom, roomName, roomSize)VALUES ('LB2', 'Learning Box 2', 5);");
        return roomAddQuery;
    }

    public static List<String> insertAllServices(){
        var servicesAddQuery = new ArrayList<String>();
        servicesAddQuery.add("CREATE TABLE Services(idService INTEGER PRIMARY KEY,description VARCHAR);");
        servicesAddQuery.add("INSERT INTO Services(idService, description)VALUES (1, 'Cafe et boissons');");
        servicesAddQuery.add("INSERT INTO Services(idService, description)VALUES (2, 'Nourriture');");
        servicesAddQuery.add("INSERT INTO Services(idService, description)VALUES (3, 'Projecteur');");
        servicesAddQuery.add("INSERT INTO Services(idService, description)VALUES (4, 'Matériel Sonore');");
        return servicesAddQuery;
    }

    public static List<String> insertAllMembers(){
        var memberAddQuery = new ArrayList<String>();
        memberAddQuery.add("CREATE TABLE Member(userMatr VARCHAR PRIMARY KEY,userFullName VARCHAR,userMail VARCHAR);");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('A123456', 'Jean Dupont', 'j.dupont@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('B234567', 'Marie Dubois', 'm.dubois@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('C345678', 'Pierre Martin', 'p.martin@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('D456789', 'Sophie Lambert', 's.lmabert@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('E567890', 'Luc Lefevre', 'l.lefevre@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('F678901', 'Claire Simon', 'c.simon@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('G789012', 'Marc Durand', 'm.durand@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('H890123', 'Julie Bernard', 'j.bernard@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('I901234', 'Paul Richard', 'p.richard@helmo.be');");
        memberAddQuery.add("INSERT INTO Member(userMatr, userFullName, userMail)VALUES ('J012345', 'Emma Petit', 'e.petit@helmo.be');");
        return memberAddQuery;
    }

    public static List<String> insertAllReservation(){
        var reservationAddQuery = new ArrayList<String>();
        reservationAddQuery.add("CREATE TABLE Reservation (idReservation INTEGER PRIMARY KEY AUTOINCREMENT,salle VARCHAR(5),matricule VARCHAR(7),jourReservation VARCHAR(11),debut VARCHAR(11),fin VARCHAR(11),description VARCHAR(25),nbPersonnes INTEGER);");
        reservationAddQuery.add("INSERT INTO Reservation (salle, matricule, jourReservation, debut, fin, description, nbPersonnes)VALUES('A101', 'MAT1234', '2024-12-01', '09:00:00', '11:00:00', 'Réunion projet', 8);");
        reservationAddQuery.add("INSERT INTO Reservation (salle, matricule, jourReservation, debut, fin, description, nbPersonnes)VALUES('B202', 'MAT5678', '2024-12-02', '14:00:00', '15:30:00', 'Présentation client', 12);");
        return reservationAddQuery;
    }
}
