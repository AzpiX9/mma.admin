/*
 * This source file is an example
 */
package org.helmo.mma.admin.views;

import org.helmo.mma.admin.presentations.BookingPresenter;
import org.helmo.mma.admin.presentations.MainView;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
            System.out.println("""
                    1. Changer de date
                    2. Encoder une réservation
                    3. Consulter une réservation
                    4. Quitter
                    """);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1 -> {
                    System.out.println("Entrez une date ");
                    String date = scanner.nextLine();
                    currentDate = LocalDate.parse(date);
                }
                case 2 -> encodeNewBookedRoom();
                case 3 -> viewReservation();
                case 4 -> {
                    return;
                }
                default -> displayError("Choix invalide");
            }
        }
    }

    public void viewReservation() {
        StringBuilder builder = new StringBuilder();
        System.out.println("Date : ");
        String date = scanner.nextLine();
        builder.append(date);

        System.out.println("Id salle : ");
        String id = scanner.nextLine();
        builder.append(id+", ");

        System.out.println("Horaire : ");
        String horaire = scanner.nextLine();
        builder.append(horaire);

        //envoi de la requete
        presenter.handleRequest(builder.toString()); //TODO: Refactoriser
    }

    private void encodeNewBookedRoom() {
        StringBuilder builder = new StringBuilder();
        System.out.println("Id salle : ");
        String salleId = scanner.nextLine();
        builder.append(salleId);

        System.out.println("Matricule : ");
        String matricule = scanner.nextLine();
        builder.append(", "+matricule);

        System.out.println("Jour : ");
        String day = scanner.nextLine();
        LocalDate jour = LocalDate.parse(day);
        builder.append(", "+jour);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("Heure début : ");
        String heureB = scanner.nextLine();
        LocalTime debut = LocalTime.parse(heureB,formatter);
        builder.append(", "+debut);

        System.out.println("Heure fin : ");
        String heureF = scanner.nextLine();
        LocalTime fin = LocalTime.parse(heureF,formatter);
        builder.append(", "+fin);

        System.out.println("Description : ");
        String description = scanner.nextLine();
        builder.append(", "+description);

        System.out.println("Nombre de personnes : ");
        int nombre = scanner.nextInt();
        builder.append(", "+nombre);

        presenter.handleRequest(builder.toString());
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
    public void displayError(String error) {
        System.err.println("[ERREUR] : "+error);
    }

    @Override
    public void displayAReservation(String bookingGiven){
        System.out.println(bookingGiven);

        System.out.println("Salle :"); //nom de la salle et sa capacité
        System.out.println(" ,"); //date, crénau
        System.out.println("Description : %s");
        System.out.println("Responsable : %s (%s, %s)");
    }
}
