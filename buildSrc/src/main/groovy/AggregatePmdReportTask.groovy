import groovy.xml.MarkupBuilder
import groovy.xml.XmlParser

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Définition de l'agrégateur de fichier pmd
 * */
class AggregatePmdReportsTask extends DefaultTask {
    @InputFiles
    FileCollection projectsFiles

    @OutputFile
    File reportFile

    @TaskAction
    def agregate() {
        println 'Aggregating pmd reports'
        Map<String, List<String>> aggregatedRules = [:]
        def xmlParser = new XmlParser()

        projectsFiles.each { it
            println "inspecting ${it.path}"
            def reportAsXml = xmlParser.parse(it)

            reportAsXml.file
                    .violation
                    .groupBy {it.'@rule'}
                    .each {
                        key, values ->
                            if(aggregatedRules.containsKey(key)) {
                                aggregatedRules[key].addAll(values)
                            } else {
                                aggregatedRules[key] = values
                            }
                    }
        }

        reportFile.withPrintWriter('utf-8', { writer ->
            def html = new MarkupBuilder(writer)
            html.html {
                head {
                    title: "PMD Agregated Result"
                    meta(name:'viewport', content: 'width=device-width, initial-scale=1')
                    link(rel: 'stylesheet', href: "https://www.w3schools.com/w3css/4/w3.css")
                }
                body(id: "main") {
                    div(class: 'w3-container') {
                        h1 "PMD Agregated Result"
                        aggregatedRules.each { entries ->
                            div {
                                h2("${entries.key} (violation count: ${entries.value.size()})")
                                entries.value.each { violation ->
                                    div {
                                        strong('Type: ' + violation.'@class' + ' begins at ' + violation.'@beginline' + ' ends at ' + violation.'@endline')
                                        p(violation.text())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}
