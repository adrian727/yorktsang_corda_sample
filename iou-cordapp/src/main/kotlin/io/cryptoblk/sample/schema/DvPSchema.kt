package io.cryptoblk.sample.schema

import io.cryptoblk.sample.metadata.DvPStatus
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The family of schemas for CashMovementState.
 */
object DvPSchema

/**
 * An CashMovementState schema.
 */
object DvPSchemaV1 : MappedSchema(
	schemaFamily = DvPSchema::class.java,
	version = 1,
	mappedTypes = listOf(PersistentDvP::class.java)
) {
	@Entity
	@Table(name = "dvp_states")
	class PersistentDvP(
		@Column(name = "operator")
		var operatorName: String,
		@Column(name = "buyer")
		var buyerName: String,
		@Column(name = "seller")
		var sellerName: String,
		@Column(name = "status")
		var status: DvPStatus?,
		@Column(name = "linear_id")
		@Type(type = "uuid-char")
		var linearId: UUID
	) : PersistentState() {
		constructor() : this("", "", "",
			DvPStatus.DVP_DRAFTED, UUID.randomUUID())
	}
}