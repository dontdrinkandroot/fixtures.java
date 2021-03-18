package net.dontdrinkandroot.fixtures.purger

/**
 * A [DatabasePurger] that does nothing.
 */
class NoopDatabasePurger : DatabasePurger {

    override fun purge() {
        /* Noop */
    }
}