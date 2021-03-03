package io.cryptoblk.sample.state

import io.cryptoblk.sample.contract.PaymentContract
import io.cryptoblk.sample.metadata.AccountInfo
import io.cryptoblk.sample.metadata.ChargeBearer
import io.cryptoblk.sample.metadata.Participant
import io.cryptoblk.sample.metadata.PartyRole
import io.cryptoblk.sample.metadata.PaymentStatus
import io.cryptoblk.sample.schema.PaymentSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency

@BelongsToContract(PaymentContract::class)
data class PaymentState(
	val settlementBank: Party,
	val payer: Participant,
	val payee: Participant,
	val fromAccount: AccountInfo,
	val toAccount: AccountInfo,
	val customerRefNumber: String,
	val instructedMVUnit: BigDecimal,
	val instructedMVCurrency: Currency,
	val chargeBearer: ChargeBearer,
	val settlementBankRef: String? = null,
	val settlementDate: Instant? = null,
	val rejectCode: String? = null,
	val rejectReason: String? = null,
	val rejectDate: Instant? = null,
	override val status: PaymentStatus,
	override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState, VerifiableState {
	override val participants: List<AbstractParty>
		get() = listOf(settlementBank, payer.identity, payee.identity)

	override fun generateMappedObject(schema: MappedSchema): PersistentState {
		return when (schema) {
			is PaymentSchemaV1 -> PaymentSchemaV1.PersistentPayment(
				settlementBankName = this.settlementBank.name.toString(),
				payerName = this.payer.identity.name.toString(),
				payeeName = this.payee.identity.name.toString(),
				fromAccountNum = this.fromAccount.accountNumber,
				fromAccountName = this.fromAccount.accountName,
				toAccountNum = this.toAccount.accountNumber,
				toAccountName = this.toAccount.accountName,
				customerRefNumber = this.customerRefNumber,
				instructedMVUnit = this.instructedMVUnit,
				instructedMVCurrency = this.instructedMVCurrency,
				settlementBankRef = this.settlementBankRef,
				settlementDate = this.settlementDate,
				rejectCode = this.rejectCode,
				rejectDate = this.rejectDate,
				rejectReason = this.rejectReason,
				status = this.status,
				linearId = this.linearId.id
			)
			else -> throw IllegalArgumentException("Unrecognised schema $schema")
		}
	}

	override fun supportedSchemas(): Iterable<MappedSchema> = listOf(PaymentSchemaV1)

	override fun partyOf(role: PartyRole): Party = when (role) {
		PartyRole.SETTLEMENT_BANK -> settlementBank
		PartyRole.PAYER -> payer.identity
		PartyRole.PAYEE -> payee.identity
		else -> throw IllegalArgumentException("Illegal party role $role")
	}
}