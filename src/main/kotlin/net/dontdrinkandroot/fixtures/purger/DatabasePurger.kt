package net.dontdrinkandroot.fixtures.purger

/**
 * A [DatabasePurger] is responsible for cleaning the data from the database.
 */
interface DatabasePurger {

    /**
     * Purges the database.
     */
    fun purge()
}