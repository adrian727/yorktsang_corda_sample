package io.cryptoblk.sample.contract

import io.cryptoblk.sample.metadata.PartyRole
import io.cryptoblk.sample.state.VerifiableState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.CommandWithParties
import net.corda.core.contracts.requireThat
import java.math.BigDecimal
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class ContractValidator <T: VerifiableState, E: Enum<E>> (
	private val input: T?,
	private val output: T,
	private val transitions: Map<KClass<out CommandData>, Transition<E>>
) {
	fun verify (command: CommandWithParties<CommandData>) {
		val ts = transitions[command.value::class]
		if (ts == null) {
			throw IllegalStateException("Command ${command.value} is not allowed")
		}
		else {
			// Verify signers
			val legalSigners = ts.signers.map { output.partyOf(it).owningKey }
			requireThat {
				"Command ${command.value} must be signed by all signers" using (legalSigners.all { it in command.signers })
				"Command ${command.value} is not allowed to be signed by illegal signers" using (command.signers.all { it in legalSigners })
			}

			// Verify status
			val inputStatus = ts.inputStatus
			val outputStatus = ts.outputStatus
			requireThat {
				"Status of the input state must be $inputStatus" using (input?.status == inputStatus)
				"Status of the output state must be $outputStatus" using (output.status == outputStatus)
			}
		}
	}

	inner class Output {
		fun isPositive(prop: KProperty1<T, BigDecimal>) = isPositive(output, prop)
		fun isNotNullOrBlank(prop: KProperty1<T, String?>) = isNotNullOrBlank(output, prop)
		fun isNotNull(prop: KProperty1<T, *>) = isNotNull(output, prop)
	}

	private fun isPositive(state: T, prop: KProperty1<T, BigDecimal>) {
		requireThat {
			"${prop.name} must be larger than ZERO" using (prop.get(state) > BigDecimal.ZERO)
		}
	}

	private fun isNotNullOrBlank(state: T, prop: KProperty1<T, String?>) {
		requireThat {
			"${prop.name} must not be null nor blank" using (!prop.get(state).isNullOrBlank())
		}
	}

	private fun isNotNull(state: T, prop: KProperty1<T, *>) {
		requireThat {
			"${prop.name} must not be null" using (prop.get(state) != null)
		}
	}

	fun isUnchanged(vararg props: KProperty1<T, *>) {
		props.forEach {
			requireThat {
				"${it.name} in input and output must be the same" using (it.get(input!!) == it.get(output))
			}
		}
	}
}

class Transition <E: Enum<E>> (
	val signers: List<PartyRole>,
	val inputStatus: E?,
	val outputStatus: E
)