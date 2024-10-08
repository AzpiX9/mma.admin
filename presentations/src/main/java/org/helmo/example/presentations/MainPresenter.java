/*
 * This source file is an example
 */
package org.helmo.example.presentations;

public class MainPresenter implements BookingPresenter {

    private MainView view;

    @Override
    public void setView(MainView view) {
        this.view = view;
    }
}
