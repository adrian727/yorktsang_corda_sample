package io.cryptoblk.sample.metadata

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class AccountInfo(
	val accountNumber: String,
	val accountName: String,
	val addressLn1: String?,
	val addressLn2: String?
)