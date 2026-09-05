package dev.minecraft.rpg.identity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentityTest {
    @Test
    void keepsStableRuntimeIdentityAndDisplayNameSeparate() {
        UUID id = UUID.randomUUID();

        Identity identity = new Identity(id, "DevPlayer");

        assertEquals(id, identity.id());
        assertEquals("DevPlayer", identity.displayName());
    }
}

