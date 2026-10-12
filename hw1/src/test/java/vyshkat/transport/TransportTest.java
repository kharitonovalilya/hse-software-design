package vyshkat.transport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportTest {

    @Test
    void shouldBeSuitableForBeginnerWhenFriendlinessIsSix() {
        Bike bike = new Bike(
                "Bike",
                6,
                true,
                80
        );
        assertTrue(bike.isSuitableForBeginner());
    }

    @Test
    void shouldNotBeSuitableForBeginnerWhenFriendlinessIsFive() {
        Bike bike = new Bike(
                "Bike",
                5,
                true,
                80
        );
        assertFalse(bike.isSuitableForBeginner());
    }

    @Test
    void shouldRejectFriendlinessBelowOne() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bike(
                        "Bike",
                        0,
                        true,
                        80
                )
        );
    }

    @Test
    void shouldRejectFriendlinessAboveTen() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bike(
                        "Bike",
                        11,
                        true,
                        80
                )
        );
    }

    @Test
    void shouldRejectTyreConditionBelowZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bike(
                        "Bike",
                        7,
                        true,
                        -1
                )
        );
    }

    @Test
    void shouldRejectTyreConditionAboveHundred() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Bike(
                        "Bike",
                        7,
                        true,
                        101
                )
        );
    }
}