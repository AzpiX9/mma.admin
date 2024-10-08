/*
 * This source file is an example
 */
package org.helmo.example.views;

import org.apache.commons.lang3.NotImplementedException;
import org.helmo.example.presentations.BookingPresenter;
import org.helmo.example.presentations.MainView;

import java.time.LocalDateTime;

public class CLIView implements MainView {

    private BookingPresenter presenter;

    public CLIView(BookingPresenter presenter){
        this.presenter = presenter;
        this.presenter.setView(this);
    }

    @Override
    public void seeReservationsFor(LocalDateTime dateTimeGiven) {
        throw new NotImplementedException();
    }

    @Override
    public void bookLocal(String idLocal, String matricule, LocalDateTime localDate) {
        throw new NotImplementedException();

    }
}
