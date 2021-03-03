package io.cryptoblk.sample.metadata

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Participant(
	val identity: Party,
	val legalName: String,
	val lei: String,
	val bic: String,
	val addressLn1: String?,
	val addressLn2: String?
)