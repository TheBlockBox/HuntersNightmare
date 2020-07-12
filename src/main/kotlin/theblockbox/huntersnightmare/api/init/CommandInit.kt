package theblockbox.huntersnightmare.api.init

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.command.ISuggestionProvider
import net.minecraft.command.arguments.EntityArgument.entity
import net.minecraft.command.arguments.EntityArgument.getEntity
import net.minecraft.entity.Entity
import net.minecraft.util.text.TranslationTextComponent
import theblockbox.huntersnightmare.HuntersNightmare
import theblockbox.huntersnightmare.api.transformation.Transformation
import theblockbox.huntersnightmare.api.transformation.Transformation.Companion.getTransformation
import theblockbox.huntersnightmare.util.handlers.ForgeEventHandler
import java.util.concurrent.CompletableFuture

/**
 * Object that is registers all Hunter's Nightmare commands
 */
// TODO: Add help if it's possible
object CommandInit {
    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
                literal("htnm")
                        .executes {
                            it.source.sendFeedback(TranslationTextComponent("command." + HuntersNightmare.MODID + ".info", HuntersNightmare.VERSION,
                                    TranslationTextComponent(ForgeEventHandler.outdatedStatus), HuntersNightmare.DOWNLOAD_LINK), true)
                            1
                        }
                        .then(
                                literal("transformation").requires { it.hasPermissionLevel(2) }
                                        .then(literal("get").executes {
                                            getTransformation(it.source, it.source.entity)
                                        }.then(argument("entity", entity()).executes {
                                            getTransformation(it.source, getEntity(it, "entity"))
                                        }))
                                        .then(literal("set").then(argument("new_transformation", TransformationArgument()).executes {
                                            changeTransformation(it.source, it.source.entity, it.getArgument("new_transformation", Transformation::class.java))
                                        }.then(argument("entity", entity()).executes {
                                            changeTransformation(it.source, getEntity(it, "entity"), it.getArgument("new_transformation", Transformation::class.java))
                                        })))
                        )
        )
    }

    private fun getTransformation(commandSource: CommandSource, entity: Entity?): Int {
        commandSource.sendFeedback(TranslationTextComponent("command." + HuntersNightmare.MODID + ".transformation.get", entity?.displayName
                ?: "Unknown",
                entity.getTransformation().translationKey), true)
        return 1
    }

    private fun changeTransformation(commandSource: CommandSource, entity: Entity?, newTransformation: Transformation?): Int {
        if ((entity != null) && (newTransformation?.exists() == true)) {
            val iTransformation = entity.getCapability(CapabilityInit.transformationCapability)
            iTransformation.ifPresent {
                it.transformation = newTransformation
                // TODO: Set the transformation using an extra method
                commandSource.sendFeedback(TranslationTextComponent("command." + HuntersNightmare.MODID + ".transformation.set.success", entity.displayName, newTransformation.translationKey), true)
            }
            if (iTransformation.isPresent) return 1
        }
        commandSource.sendFeedback(TranslationTextComponent("command." + HuntersNightmare.MODID + ".transformation.set.failure", entity?.displayName
                ?: "Unknown"), true)
        return 0 // TODO: Should we return 0 here?
    }

    private fun StringReader.readResourceLocationString(): String? {
        fun isAllowedCharacter(c: Char) = c in '0'..'9' || c in 'A'..'Z' || c in 'a'..'z' || c == '_' || c == '-' || c == '.' || c == '+' || c == ':'

        val start: Int = this.cursor
        while (this.canRead() && isAllowedCharacter(this.peek()))
            this.skip()
        return this.string.substring(start, this.cursor)
    }

    class TransformationArgument : ArgumentType<Transformation> {
        private val examples: MutableCollection<String> = mutableListOf(TransformationInit.human.get().registryName.toString(), TransformationInit.werewolf.get().registryName.toString())

        override fun parse(reader: StringReader?): Transformation? {
            val transformation = Transformation.getFromName(reader?.readResourceLocationString())
            reader?.readUnquotedString()
            // TODO: Fix unexpected error here
            return if (transformation.exists()) transformation else null
        }

        override fun <S : Any?> listSuggestions(context: CommandContext<S>?, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            return ISuggestionProvider.suggest(Transformation.transformations.values.stream().filter { it.exists() }.map { it.registryName.toString() }, builder)
        }

        override fun getExamples(): MutableCollection<String> {
            return examples
        }
    }
}