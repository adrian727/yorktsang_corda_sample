package io.cryptoblk.sample.contract

import io.cryptoblk.sample.metadata.PaymentStatus
import io.cryptoblk.sample.metadata.PartyRole.SETTLEMENT_BANK as SETTLEMENT_BANK
import io.cryptoblk.sample.metadata.PartyRole.PAYER as PAYER
import io.cryptoblk.sample.state.PaymentState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class PaymentContract : Contract {
	companion object {
		val transitions = mapOf(
			Commands.Create::class to Transition(listOf(PAYER), null, PaymentStatus.PAYMENT_REQUEST),
			Commands.Accept::class to Transition(listOf(SETTLEMENT_BANK), PaymentStatus.PAYMENT_REQUEST, PaymentStatus.PAYMENT_COMPLETED),
			Commands.Reject::class to Transition(listOf(SETTLEMENT_BANK), PaymentStatus.PAYMENT_REQUEST, PaymentStatus.PAYMENT_FAILED)
		)
	}

	interface Commands : CommandData {
		class Create : TypeOnlyCommandData(), Commands
		class Accept : TypeOnlyCommandData(), Commands
		class Reject : TypeOnlyCommandData(), Commands
	}

	// TODO: confirm and add more checking criteria
	override fun verify(tx: LedgerTransaction) {
		val paymentInput = tx.inputs.singleOrNull()?.state?.data as PaymentState?
		val paymentOutput = tx.outputs.single().data as PaymentState

		val validator = ContractValidator(paymentInput, paymentOutput, transitions)

		tx.commands.forEach {
			validator.verify(it)

			when (it.value) {
				is Commands.Create -> {
					validator.Output().isPositive(PaymentState::instructedMVUnit)
				}
				is Commands.Accept -> {
					validator.isUnchanged(
						PaymentState::fromAccount,
						PaymentState::toAccount,
						PaymentState::instructedMVUnit,
						PaymentState::instructedMVCurrency)
					validator.Output().isNotNullOrBlank(PaymentState::settlementBankRef)
					validator.Output().isNotNull(PaymentState::settlementDate)
				}
				is Commands.Reject -> {
					validator.Output().isNotNullOrBlank(PaymentState::rejectCode)
				}
			}
		}
	}

}
