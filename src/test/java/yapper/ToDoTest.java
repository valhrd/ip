package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import yapper.components.ToDo;
import yapper.exceptions.YapperException;

public class ToDoTest {

    @Test
    public void getDesc() {
        assertEquals("| T |   | return book", new ToDo("return book").getDesc());
        assertEquals("| T |   | this", new ToDo("this").getDesc());
        assertEquals("| T |   | that", new ToDo("that").getDesc());
        assertEquals("| T |   | make thing", new ToDo("make thing").getDesc());
        assertEquals("| T |   | fight", new ToDo("fight").getDesc());
    }

    @Test
    public void getDesc_emptyString_exceptionThrown() {
        try {
            assertEquals("| T |   | ", new ToDo("").getDesc());
            System.out.println("Fail");
        } catch (YapperException e) {
            assertEquals("Please provide a description", e.getMessage());
        }
    }
}
