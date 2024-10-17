/*
 * This source file is an example
 */
package org.helmo.mma.admin.views;

import org.helmo.mma.admin.presentations.BookingPresenter;
import org.helmo.mma.admin.presentations.MainView;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class CLIView implements MainView, AutoCloseable {
    @Override
    public void close() throws IOException {
        this.scanner.close();
        this.writer.close();
    }

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
            int choice = displayMenu();

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

    private int displayMenu() {
        System.out.println("""
                1. Changer de date
                2. Encoder une réservation
                3. Consulter une réservation
                4. Quitter
                """);
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public void viewReservation() {
        StringBuilder builder = new StringBuilder();
        builder.append((LocalDate) getInput("Date", LocalDate::parse));
        builder.append(", ").append((String) getInput("Id Salle", s->s));
        builder.append(", ").append((LocalTime) getInput("Horaire", LocalTime::parse));


        //envoi de la requete
        presenter.viewRequest(builder.toString()); //TODO: Refactoriser
    }

    private void encodeNewBookedRoom() {
        StringBuilder builder = new StringBuilder();

        builder.append((String) getInput("Id salle : ", s -> s));
        builder.append(", ").append((String) getInput("Matricule : ", s -> s));
        builder.append(", ").append((LocalDate) getInput("Jour : ", LocalDate::parse));
        builder.append(", ").append((LocalTime) getInput("Heure début : ", s -> LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"))));
        builder.append(", ").append((LocalTime) getInput("Heure fin : ", s -> LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"))));
        builder.append(", ").append((String) getInput("Description : ", s -> s));
        builder.append(", ").append((int) getInput("Nombre de personnes : ", Integer::parseInt));

        presenter.handleRequest(builder.toString());
    }

    /**
     *
     * @param prompt
     * @param parser
     * @return
     * @param <T>
     */
    private <T> T getInput(String prompt, Function<String, T> parser) {
        System.out.println(prompt + " : ");
        String input = scanner.nextLine();
        return parser.apply(input);
    }

    @Override
    public void seeReservationsFor(LocalDate dateGiven) {
        System.out.println("Disponibilités pour "+ dateGiven.toString());
        LocalTime timeDay = LocalTime.of(8,0);
        System.out.printf("%-5c",' ');
        for (int i = 0; i < 18; i++) {
            var time = (i%2 == 0)?"|"+timeDay.toString():String.format("|%5s", " ");
            System.out.print(time);
            timeDay = timeDay.plusMinutes(30);
        }
        System.out.println("|");
        presenter.seeBookingRequest(dateGiven);


    }


    @Override
    public void displayError(String error) {
        System.err.println("[ERREUR] : "+error);
    }

    @Override
    public void displayAReservation(String bookingGiven){
        var values = bookingGiven.split(",");
        var room = values[0].split("_");
        var organizer = values[4].split("_");

        System.out.println("\nSalle : "+room[0]+" (Capacité. "+room[1]+")"); //nom de la salle et sa capacité
        System.out.println(String.format("%s, de %s à %s",values[1],values[2],values[3])); //date, crénau
        System.out.println(String.format("Description : %s",values[5]));
        System.out.println(String.format("Responsable : %s (%s, %s)\n",organizer[0]+" "+organizer[1],organizer[2],organizer[3]));
    }

    /**
     * Permet d'afficher en grille les locaux pris via le crénau
     * @param elems
     */
    public void displayAllLocalEvs(List<String> elems){
        for (var elem : elems){
            System.out.print(elem+" |");
            for (int i = 0; i < 18; i++) {

            }
        }
    }
}
