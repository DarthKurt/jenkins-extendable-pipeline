package com.company.some.jenkins

import org.jenkinsci.plugins.workflow.cps.CpsScript


class DynamicLibraryAccessor {

    private static final String tempFolder = "tmp"

    private final CpsScript script
    private final String masterNode

    DynamicLibraryAccessor(final CpsScript script, final String masterNode = 'master') {
        this.script = script
        this.masterNode = masterNode
    }

    /**
     * Loads library from provided path.
     * @param libraryPath Path inside the current repository.
     * It should represent a valid Jenkins library folder structure.
     */
    final load(final String libraryPath) {
        this.script.node(masterNode,  {
            final String repoPath = "${tempFolder}/${libraryPath}"

            this.script.dir(tempFolder) {
                // checkout target repo
                script.checkout(script.scm)

                // Use standard library step but with simple implementation of LibraryRetriever
                script.library(
                        identifier: 'local-lib@main',
                        retriever: new FolderRetriever(repoPath)
                )

                // After loading the library we can clean the workspace
                this.script.deleteDir()
            }

            this.script.echo("Done loading shared library")
        })
    }
}
