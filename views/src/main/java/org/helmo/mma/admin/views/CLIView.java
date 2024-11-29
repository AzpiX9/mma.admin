package org.helmo.mma.admin.views;

import org.helmo.mma.admin.presentations.BookingPresenter;
import org.helmo.mma.admin.presentations.MainView;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CLIView implements MainView, AutoCloseable {

    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
    private BookingPresenter presenter;
    private final Scanner scanner;
    private final PrintWriter writer;
    private LocalDate currentDate = LocalDate.now();

    public CLIView(InputStream input, OutputStream output){
        scanner = new Scanner(input);
        writer = new PrintWriter(output);
    }

    @Override
    public void close() throws IOException {
        this.scanner.close();
        this.writer.close();
    }

    public void run(){
        int choice;
        do {
            seeReservationsFor(currentDate);
            choice = displayMenu();
            handleChoice(choice);
        } while (choice != 5);
    }

    /**
     * Gestion des choix fait par l'utilisateur
     * @param choice
     */
    private void handleChoice(int choice) {
        switch (choice){
            case 1 -> changeDate();
            case 2 -> encodeNewBookedRoom();
            case 3 -> viewReservation();
            case 4 -> seeAvailable();
            case 5 -> {
            return;
            }
            default -> displayError("Choix invalide");
        }
    }



    private void seeAvailable() {
        var builder = new StringBuilder();
        builder.append((LocalDate) getInput("Date",LocalDate::parse));
        builder.append(", ").append((int) getInput("Nombre de personnes", Integer::parseInt));
        builder.append(", ").append((LocalTime) getInput("Durée", s-> LocalTime.parse(s, TIME_FORMATTER)));

        presenter.availableRequest(builder.toString());
    }

    private void changeDate() {
        currentDate = getInput("Entrez une date",LocalDate::parse);
    }

    private int displayMenu() {
        System.out.println("""
                1. Changer de date
                2. Encoder une réservation
                3. Consulter une réservation
                4. Voir les disponibilités
                5. Quitter
                """);
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public void viewReservation() {
        StringBuilder builder = new StringBuilder();
        builder.append((LocalDate) getInput("Date", LocalDate::parse));
        builder.append(", ").append((String) getInput("Id Salle", s->s));
        builder.append(", ").append((LocalTime) getInput("Horaire", s-> LocalTime.parse(s, TIME_FORMATTER)));

        presenter.viewRequest(builder.toString());
    }

    private void encodeNewBookedRoom() {
        StringBuilder builder = new StringBuilder();

        builder.append((String) getInput("Id salle", s -> s));
        builder.append(", ").append((String) getInput("Matricule", s -> s));
        builder.append(", ").append((LocalDate) getInput("Jour", LocalDate::parse));
        builder.append(", ").append((LocalTime) getInput("Heure début", s -> LocalTime.parse(s, TIME_FORMATTER)));
        builder.append(", ").append((LocalTime) getInput("Heure fin", s -> LocalTime.parse(s, TIME_FORMATTER)));
        builder.append(", ").append((String) getInput("Description", s -> s));
        builder.append(", ").append((int) getInput("Nombre de personnes", Integer::parseInt));

        presenter.writeEventRequest(builder.toString());
    }

    /**
     * Demande à l'utilisateur d'écrire dans la console
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
    public void setPresenter(BookingPresenter presenter) {
        this.presenter = presenter;

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
    public void displayAReservation(String bookingGiven, List<String> servicesChosen){
        var values = bookingGiven.split(",");
        var room = values[0].split("_");
        var organizer = values[4].split("_");


        System.out.println("\nSalle : "+room[0]+" (Capacité. "+room[1]+")"); //nom de la salle et sa capacité
        System.out.printf("%s, de %s à %s%n",values[1],values[2],values[3]); //date, crénau
        System.out.printf("Description : %s%n",values[5]);
        System.out.printf("Responsable : %s (%s, %s)\n",organizer[0]+" "+organizer[1],organizer[2],organizer[3]);
        System.out.printf("Services prévus : %s \n", servicesChosen.isEmpty() ?"Rien":servicesChosen.toString());
    }

    /**
     * Permet d'afficher en grille les locaux pris via le crénau
     * @param elems
     */
    @Override
    public void displayByLocalEvs(String name,List<String> elems){
        LocalTime reference = LocalTime.of(8,0);
        System.out.printf("%4s |",name);
        for (int i = 0; i < 18; i++) {
            String result = String.format("%5s|"," ");
            for (var elem : elems) {
                var begin = LocalTime.parse(elem.split("-")[0]);
                var end = LocalTime.parse(elem.split("-")[1]);
                if((reference.isAfter(begin) || reference.equals(begin))&& reference.isBefore(end)){
                    result = String.format(" %s |","X".repeat(3));
                    break;
                }
            }

            System.out.print(result);
            reference = reference.plusMinutes(30);
        }
        System.out.println();
    }

    @Override
    public Set<String> askService(List<String> availableServices) {
        int index = 0;
        for (String service : availableServices) {
            System.out.printf("%d. %s \n",index+1,service);
            index++;
        }
        var choosen = getInput("Service choisis (séparez les choix par des virgules ',') \nLaissez vide si vous ne prenez pas de services",s -> s);
        if (choosen == null || choosen.trim().isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(List.of(choosen.split(",")));
    }

    @Override
    public void displayAvailable(List<String> evs) {
        System.out.printf("%n%5s | %10s | %s | %s |%n","Local", "Date","Heure de début","Heure de Fin");
        for (String ev : evs) {
            var attributes = ev.split(",");
            var beginTime = attributes[2].split("-")[0];
            var endTime = attributes[2].split("-")[1];
            System.out.printf("%-5s | %10s | %14s | %12s |\n",attributes[0],attributes[1],beginTime,endTime);
        }
        System.out.println();
    }

    @Override
    public void displayMessage(String message) {
        System.out.printf("%s - %s : %s\n",LocalDate.now(),LocalTime.now(),message);
    }


}
