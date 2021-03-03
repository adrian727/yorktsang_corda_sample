package io.cryptoblk.sample.metadata

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class CashMovementStatus {
	LOAD_REQUEST,
	LOAD_COMPLETED,
	LOAD_FAILED,
	TRANSFER_REQUEST,
	TRANSFER_COMPLETED,
	TRANSFER_FAILED,
	UNLOAD_REQUEST,
	UNLOAD_COMPLETED,
	UNLOAD_FAILED
}