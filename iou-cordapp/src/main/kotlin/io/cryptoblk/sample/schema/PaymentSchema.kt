package io.cryptoblk.sample.schema

import io.cryptoblk.sample.metadata.PaymentStatus
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.Type
import java.math.BigDecimal
import java.time.Instant
import java.util.Currency
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The family of schemas for PaymentState.
 */
object PaymentSchema

/**
 * An PaymentState schema.
 */
object PaymentSchemaV1 : MappedSchema(
	schemaFamily = PaymentSchema::class.java,
	version = 1,
	mappedTypes = listOf(PersistentPayment::class.java)
) {
	@Entity
	@Table(name = "payment_states")
	class PersistentPayment(
		@Column(name = "settlement_bank")
		var settlementBankName: String,

		@Column(name = "payer")
		var payerName: String,

		@Column(name = "payee")
		var payeeName: String,

		@Column(name = "from_account_number")
		var fromAccountNum: String,

		@Column(name = "from_account_name")
		var fromAccountName: String,

		@Column(name = "to_account_number")
		var toAccountNum: String,

		@Column(name = "to_account_name")
		var toAccountName: String,

		@Column(name = "customer_ref_number")
		var customerRefNumber: String,

		@Column(name = "instructed_mv_unit")
		var instructedMVUnit: BigDecimal,

		@Column(name = "instructed_mv_currency")
		var instructedMVCurrency: Currency,

		@Column(name = "settlement_bank_ref")
		var settlementBankRef: String?,

		@Column(name = "settlement_date")
		var settlementDate: Instant?,

		@Column(name = "reject_code")
		var rejectCode: String?,

		@Column(name = "reject_reason")
		var rejectReason: String?,

		@Column(name = "reject_date")
		var rejectDate: Instant?,

		@Column(name = "status")
		var status: PaymentStatus,

		@Column(name = "linear_id")
		@Type(type = "uuid-char")
		var linearId: UUID
	) : PersistentState()
}