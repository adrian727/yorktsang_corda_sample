package io.cryptoblk.sample.contract

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class AssetContract : Contract {
	companion object {
		@JvmStatic
		val ID = "com.hsbc.mirrorvalue.cordapp.contract.AssetContract"
	}

	interface Commands : CommandData {
		class IssueAssetCmd : TypeOnlyCommandData(), Commands
		class ChangeOwnershipCmd : TypeOnlyCommandData(), Commands
	}

	override fun verify(tx: LedgerTransaction) {
		// TODO: not yet implemented
	}
}