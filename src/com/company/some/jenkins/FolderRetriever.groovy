package com.company.some.jenkins

import com.cloudbees.groovy.cps.NonCPS
import groovy.transform.TypeChecked
import hudson.AbortException
import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import hudson.model.TopLevelItem
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever

import java.util.logging.Logger

@TypeChecked
class FolderRetriever extends LibraryRetriever {

    private static final Logger LOGGER = Logger.getLogger(FolderRetriever.class.getName())
    private final String libraryPath

    FolderRetriever(final String libraryPath) {
        this.libraryPath = libraryPath
    }

    @Override
    @NonCPS
    void retrieve(
            final String name,
            final String version,
            boolean changelog,
            final FilePath target,
            final Run<?, ?> run,
            final TaskListener listener
    ) throws Exception {
        doRetrieve(libraryPath, target, run, listener)
    }

    @Override
    @NonCPS
    void retrieve(
            final String name,
            final String version,
            final FilePath target,
            final Run<?, ?> run,
            final TaskListener listener
    ) throws Exception {
        doRetrieve(libraryPath, target, run, listener)
    }

    @NonCPS
    private static void doRetrieve(
            final String libraryPath,
            final FilePath target,
            final Run<?, ?> run,
            final TaskListener listener
    ) throws Exception {
        def node = Jenkins.get()

        FilePath dir
        def parent = run.getParent()
        if (parent instanceof TopLevelItem ) {
            dir = node.getWorkspaceFor(parent)
            if (dir == null) {
                throw new IOException("${node.displayName} may be offline")
            }
        } else {
            // should not happen, but just in case:
            throw new AbortException("Cannot load in non-top-level build")
        }
        def libraryFilePath = dir.child(libraryPath)
        libraryFilePath.copyRecursiveTo(
                "src/**/*.groovy,vars/*.groovy,vars/*.txt,resources/", "", target)
    }
}
