package net.tools.language.postgres

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ParseTest {

    @Test
    fun `Select a bad sql statement`() {
        Assertions.assertEquals(
            null,
            parseCall("SELECT BAD STATEMENT").getDefinition()
        )
    }

    @Test
    fun `Select from a table should return null`() {
        Assertions.assertEquals(
            null,
            parseCall("SELECT * FROM public.my_table;").getDefinition()
        )
    }

    @Test
    fun `Select without ending`() {
        Assertions.assertEquals(
            FunctionCall(
                "public", "test_function", listOf()
            ),
            parseCall("SELECT * FROM test_function()").getDefinition()
        )
    }

    @Test
    fun `Select an function without arguments`() {
        Assertions.assertEquals(
            FunctionCall(
                "public", "test_function", listOf()
            ),
            parseCall("SELECT * FROM test_function();").getDefinition()
        )
    }

    @Test
    fun `Select an function with a schema`() {
        Assertions.assertEquals(
            FunctionCall(
                "my_schema", "test_function", listOf()
            ),
            parseCall("SELECT * FROM my_schema.test_function();").getDefinition()
        )
    }

    @Test
    fun `Select an function with arguments`() {
        Assertions.assertEquals(
            FunctionCall(
                "public", "test_function", listOf(
                    FunctionArgument("", "1"),
                    FunctionArgument("", "'test'")
                )
            ),
            parseCall("SELECT * FROM public.test_function(1, 'test');").getDefinition()
        )
    }

    @Test
    fun `Select an function with named arguments`() {
        Assertions.assertEquals(
            FunctionCall(
                "public", "test_function", listOf(
                    FunctionArgument("value", "1"),
                    FunctionArgument("value2", "'test'")
                )
            ),
            parseCall("SELECT * FROM public.test_function(value := 1 , value2 :='test');").getDefinition()
        )
    }

}