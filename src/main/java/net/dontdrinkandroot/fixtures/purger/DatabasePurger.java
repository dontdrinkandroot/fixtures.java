package net.dontdrinkandroot.fixtures.purger;

/**
 * A {@link DatabasePurger} is responsible for cleaning the data from the database.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public interface DatabasePurger
{
    /**
     * Purges the database.
     */
    void purge();
}
