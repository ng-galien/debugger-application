package net.tools.language.postgres

import net.tools.language.postgresql.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker


data class FunctionArgument(val name: String, val value: String)

data class FunctionCall (
    val schema: String,
    val name: String,
    val args: List<FunctionArgument>
)

class Listener: PostgreSQLParserBaseListener() {

    var function: String? = null

    var args = mutableListOf<String>()

    override fun enterFunc_name(ctx: PostgreSQLParser.Func_nameContext?) {
        function = ctx!!.text
    }

    override fun enterFunc_arg_expr(ctx: PostgreSQLParser.Func_arg_exprContext?) {
        args.add(ctx!!.text)
    }

    fun getDefinition(): FunctionCall? {
        return function?.let { name ->
            val names = name.split(".")
            FunctionCall(
                if(names.size == 2) names[0] else "public",
                if (names.size == 2) names[1] else names[0],
                args.map {
                    it.split(":=").let { split ->
                        if (split.size == 2) FunctionArgument(split[0], split[1])
                        else FunctionArgument("", split[0])
                    }
                }
            )
        }
    }

}

fun parseCall(sql: String): Listener {
    val input = CharStreams.fromString(sql + if (sql.endsWith(";")) "" else ";")
    val lexer = PostgreSQLLexer(input)
    val tokens = CommonTokenStream(lexer)
    val parser = PostgreSQLParser(tokens)
    parser.addParseListener(Listener())
    val walker = ParseTreeWalker()
    val listener = Listener()
    walker.walk(listener, parser.selectstmt())
    return listener
}