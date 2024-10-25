/*
 * This source file is an example
 */
package org.helmo.mma.admin.domains.core;

import java.util.regex.Pattern;

public record User(String Matricule, String Nom, String Prenom, String Email) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");

    public User{

        if (Matricule == null || Matricule.isBlank()) {
            throw new IllegalArgumentException("Le matricule ne peut pas être nul ou vide.");
        }
        if (Nom == null || Nom.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (Prenom == null || Prenom.isBlank()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
        }
        if (!EMAIL_PATTERN.matcher(Email).matches()) {
            throw new IllegalArgumentException("L'email est invalide.");
        }
    }

}
