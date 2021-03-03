package io.cryptoblk.sample.metadata

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class ChargeBearer {
    DEBT,
    CRED,
    SHAR,
    SLEV,
    OTHR
}