package io.cryptoblk.sample

data class Payload (
    val lender : String,
    val value : Int
    ){
}

data class UpdatePayload(
    val ref: String,
    val value : Int
)