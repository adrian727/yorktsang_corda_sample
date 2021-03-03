package io.cryptoblk.sample.metadata

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class PartyRole {
	OPERATOR,
	SETTLEMENT_BANK,
	PAYER,
	PAYEE,
	BUYER, // TODO: need to beautify this
	SELLER
}