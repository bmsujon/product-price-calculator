package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Main method should execute without errors")
    void mainMethodShouldExecuteWithoutErrors() {
        // Act
        Main.main(new String[]{});
        
        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Total Cost for 10 units: USD 100"));
        assertTrue(output.contains("Total Cost for 18 units: USD "));
        assertTrue(output.contains("Total Cost for 25 units: USD "));
        assertFalse(output.contains("Error occurred"));
    }
}