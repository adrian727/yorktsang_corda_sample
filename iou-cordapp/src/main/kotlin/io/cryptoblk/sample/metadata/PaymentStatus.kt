package io.cryptoblk.sample.metadata

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class PaymentStatus {
	PAYMENT_REQUEST,
	PAYMENT_COMPLETED,
	PAYMENT_FAILED,
}