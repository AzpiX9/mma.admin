/*
 * This source file is an example
 */
package org.helmo.mma.admin.views;

import org.apache.commons.lang3.NotImplementedException;
import org.helmo.mma.admin.presentations.BookingPresenter;
import org.helmo.mma.admin.presentations.MainView;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class CLIView implements MainView {

    private BookingPresenter presenter;
    private Scanner scanner;
    private PrintWriter writer;
    private LocalDate currentDate = LocalDate.now();

    public CLIView(InputStream input, OutputStream output, BookingPresenter presenter){
        scanner = new Scanner(input);
        writer = new PrintWriter(output);
        this.presenter = presenter;
        this.presenter.setView(this);
    }

    public void run(){
        while (true){
            seeReservationsFor(currentDate);
            System.out.println("""
                    1. Changer de date
                    2. Encoder une réservation
                    3. Quitter
                    """);
            int choice = scanner.nextInt();

            switch (choice){
                case 1 -> {
                    System.out.println("Entrez une date ");
                    String date = scanner.next();
                    currentDate = LocalDate.parse(date);
                }
                case 2 -> {
                    System.out.println("Id salle : ");
                    String salleId = scanner.next();

                    System.out.println("Matricule : ");
                    String matricule = scanner.next();

                    System.out.println("Jour : ");
                    String day = scanner.next();
                    LocalDate jour = LocalDate.parse(day);

                    System.out.println("Heure début : ");
                    String heureB = scanner.next();
                    LocalTime debut = LocalTime.parse(heureB);

                    System.out.println("Heure fin : ");
                    String heureF = scanner.next();
                    LocalTime fin = LocalTime.parse(heureF);

                    System.out.println("Description : ");
                    String description = scanner.next();

                    System.out.println("Nombre de personnes : ");
                    int nombre = scanner.nextInt();
                }
                case 3 -> {
                    return;
                }
                default -> System.out.println("Choix invalide");

            }

        }

    }

    @Override
    public void seeReservationsFor(LocalDate dateGiven) {
        System.out.println("Disponibilités pour "+ dateGiven.toString());
        LocalTime timeDay = LocalTime.of(8,0);
        System.out.printf("%-5c",' ');
        for (int i = 0; i < 18; i++) {
            System.out.print(timeDay.toString() + " ");
            timeDay = timeDay.plusMinutes(30);
        }
        System.out.println();
        presenter.seeBookingRequest(dateGiven);


    }

    @Override
    public void bookLocal(String idLocal, String matricule, LocalDateTime localDate) {
        throw new NotImplementedException();

    }
}
