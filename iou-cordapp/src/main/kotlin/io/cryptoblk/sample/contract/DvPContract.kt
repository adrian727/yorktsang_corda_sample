package io.cryptoblk.sample.contract

import io.cryptoblk.sample.metadata.DvPStatus
import io.cryptoblk.sample.metadata.PartyRole
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class DvPContract : Contract {
	companion object {
		@JvmStatic
		val ID = "com.hsbc.mirrorvalue.cordapp.contract.DvPContract"

		val transitions = mapOf(
			Commands.DvPDraftCmd::class to Transition(listOf(PartyRole.OPERATOR), null, DvPStatus.DVP_DRAFTED),
			Commands.DvPBuyerProposeCmd::class to Transition(listOf(PartyRole.BUYER), DvPStatus.DVP_DRAFTED, DvPStatus.DVP_PROPOSED),
			Commands.DvPBuyerApproveCmd::class to Transition(listOf(PartyRole.SELLER), DvPStatus.DVP_PROPOSED, DvPStatus.DVP_APPROVED),
			Commands.DvPSettleCmd::class to Transition(listOf(PartyRole.OPERATOR), DvPStatus.DVP_APPROVED, DvPStatus.DVP_SETTLED)
		)
	}

	interface Commands : CommandData {
		class DvPDraftCmd : TypeOnlyCommandData(), Commands
		class DvPSellerSignCmd : TypeOnlyCommandData(), Commands
		class DvPBuyerProposeCmd : TypeOnlyCommandData(), Commands
		class DvPBuyerApproveCmd : TypeOnlyCommandData(), Commands
		class DvPSettleCmd : TypeOnlyCommandData(), Commands
	}

	override fun verify(tx: LedgerTransaction) {
//		val dvpInput = tx.inputs.singleOrNull()?.state?.data as DvPState?
//		val dvpOutput = tx.outputs.first().data as DvPState
//
//		val validator = ContractValidator(dvpInput, dvpOutput, transitions)

//		tx.commands.forEach {
//			validator.verify(it)
//
//			// TODO: add more test cases
//		}
	}
}