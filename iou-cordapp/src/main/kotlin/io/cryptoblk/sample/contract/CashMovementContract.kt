package io.cryptoblk.sample.contract

import io.cryptoblk.sample.metadata.CashMovementStatus
import io.cryptoblk.sample.metadata.PartyRole.OPERATOR as OPERATOR
import io.cryptoblk.sample.metadata.PartyRole.SETTLEMENT_BANK as SETTLEMENT_BANK
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class CashMovementContract : Contract {
	companion object {
		@JvmStatic
		val ID = "com.hsbc.mirrorvalue.cordapp.contract.CashMovementContract"

		val transitions = mapOf(
			Commands.CashLoadRequestCmd::class to Transition(listOf(OPERATOR), null, CashMovementStatus.LOAD_REQUEST),
			Commands.CashLoadCompleteCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.LOAD_REQUEST, CashMovementStatus.LOAD_COMPLETED),
			Commands.CashLoadFailCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.LOAD_REQUEST, CashMovementStatus.LOAD_FAILED),
			Commands.CashTransferRequestCmd::class to Transition(listOf(OPERATOR), null, CashMovementStatus.TRANSFER_REQUEST),
			Commands.CashTransferCompleteCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.TRANSFER_REQUEST, CashMovementStatus.TRANSFER_COMPLETED),
			Commands.CashTransferFailCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.TRANSFER_REQUEST, CashMovementStatus.TRANSFER_FAILED),
			Commands.CashUnloadRequestCmd::class to Transition(listOf(OPERATOR), null, CashMovementStatus.UNLOAD_REQUEST),
			Commands.CashUnloadCompleteCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.UNLOAD_REQUEST, CashMovementStatus.UNLOAD_COMPLETED),
			Commands.CashUnloadFailCmd::class to Transition(listOf(SETTLEMENT_BANK), CashMovementStatus.UNLOAD_REQUEST, CashMovementStatus.UNLOAD_FAILED)
		)
	}

	interface Commands : CommandData {
		class CashLoadRequestCmd : TypeOnlyCommandData(), Commands
		class CashLoadCompleteCmd : TypeOnlyCommandData(), Commands
		class CashLoadFailCmd : TypeOnlyCommandData(), Commands
		class CashTransferRequestCmd : TypeOnlyCommandData(), Commands
		class CashTransferCompleteCmd : TypeOnlyCommandData(), Commands
		class CashTransferFailCmd : TypeOnlyCommandData(), Commands
		class CashUnloadRequestCmd : TypeOnlyCommandData(), Commands
		class CashUnloadCompleteCmd : TypeOnlyCommandData(), Commands
		class CashUnloadFailCmd : TypeOnlyCommandData(), Commands
	}

	// TODO: confirm and add more checking criteria
	override fun verify(tx: LedgerTransaction) {
//		val cashMovementInput = tx.inputs.singleOrNull()?.state?.data as CashMovementState?
//		val cashMovementOutput = tx.outputs.single().data as CashMovementState
//
//		val validator = ContractValidator(cashMovementInput, cashMovementOutput, transitions)
//
//		tx.commands.forEach {
//			validator.verify(it)
//
//			when (it.value) {
//				is Commands.CashLoadRequestCmd, is Commands.CashTransferRequestCmd, is Commands.CashUnloadRequestCmd -> {
//					validator.Output().isPositive(CashMovementState::instructedMVUnit)
//				}
//				is Commands.CashLoadCompleteCmd, is Commands.CashTransferCompleteCmd, is Commands.CashUnloadCompleteCmd -> {
//					validator.Output().isNotNullOrBlank(CashMovementState::settlementBankRef)
//					validator.Output().isNotNull(CashMovementState::settlementDate)
//				}
//				is Commands.CashLoadFailCmd, is Commands.CashTransferFailCmd, is Commands.CashUnloadFailCmd -> {
//					validator.Output().isNotNullOrBlank(CashMovementState::rejectCode)
//				}
//			}
//		}
	}

}
