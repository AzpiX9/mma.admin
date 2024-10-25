package org.helmo.mma.admin.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.helmo.mma.admin.app.Program;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchTest {

    private static String globalPackageName;

    @BeforeAll
    static void setup() {
        String packageNameApp = Program.class.getPackageName();
        globalPackageName = packageNameApp.substring(0, packageNameApp.length()-4);
    }

    @Test
    void should_respects_architecture() {
        var archRule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("App").definedBy(globalPackageName + ".app")
                .layer("Presentations").definedBy(globalPackageName + ".presentations..")
                .layer("Views").definedBy(globalPackageName + ".views..")
                .layer("Domains").definedBy(globalPackageName + ".domains..")
                .layer("Infrastructures").definedBy(globalPackageName + ".infrastructures..")

                .whereLayer("Views").mayOnlyBeAccessedByLayers("App")
                .whereLayer("Presentations").mayOnlyBeAccessedByLayers("App","Views")
                .whereLayer("Domains").mayOnlyBeAccessedByLayers("App","Presentations","Infrastructures")
                .whereLayer("Infrastructures").mayOnlyBeAccessedByLayers("App");
        archRule.check(new ClassFileImporter().importPackages(globalPackageName + ".."));
    }

    @Test
    void domains_should_not_depend_on_third_parties() {

        var domainRule = classes().that()
                .resideInAPackage("..domains..")
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..",
                        "org.apache.logging.log4j..",
                        globalPackageName + ".domains..");

        domainRule.check(new ClassFileImporter().importPackages(globalPackageName + ".."));
    }

    @Test
    void infrastructures_should_depend_on_domains() {
        var presentationRule = classes().that()
                .resideInAPackage("..infrastructures..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..",
                        "org.apache.logging.log4j..",
                        "net.fortuna.ical4j..",
                        globalPackageName + ".infrastructures..",
                        globalPackageName + ".domains..");

        presentationRule.check(new ClassFileImporter().importPackages(globalPackageName + ".."));
    }

    @Test
    void presentations_should_on_domains() {
        var presentationRule = classes().that()
                .resideInAPackage("..presentations..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java..",
                        "org.apache.logging.log4j..",
                        globalPackageName + ".presentations..",
                        globalPackageName + ".domains..");

        presentationRule.check(new ClassFileImporter().importPackages(globalPackageName + ".."));
    }

    @Test
    void views_should_depend_on_presentations() {
        var presentationRule = classes().that()
                .resideInAPackage("..views..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..",
                        "org.apache.logging.log4j..",
                        "joptsimple",
                        globalPackageName + ".views..",
                        globalPackageName + ".presentations..");

        presentationRule.check(new ClassFileImporter().importPackages(globalPackageName + ".."));
    }

}

