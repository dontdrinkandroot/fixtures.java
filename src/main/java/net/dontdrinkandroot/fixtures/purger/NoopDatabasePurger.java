package net.dontdrinkandroot.fixtures.purger;

/**
 * A {@link DatabasePurger} that does nothing.
 *
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class NoopDatabasePurger implements DatabasePurger
{
    @Override
    public void purge()
    {
        /* Noop */
    }
}
