package io.cryptoblk.sample.state

import io.cryptoblk.sample.metadata.PartyRole
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party

interface VerifiableState: ContractState {
	val status: Enum<*>

	fun partyOf(role: PartyRole): Party
}