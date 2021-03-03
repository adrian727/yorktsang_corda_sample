package io.cryptoblk.sample.state

import io.cryptoblk.sample.contract.DvPContract
import io.cryptoblk.sample.metadata.DvPStatus
import io.cryptoblk.sample.metadata.Participant
import io.cryptoblk.sample.metadata.PartyRole
import io.cryptoblk.sample.schema.DvPSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

@BelongsToContract(DvPContract::class)
data class DvPState(
	val operator: Party,
	val buyer: Participant,
	val seller: Participant,
	override val status: DvPStatus,
	override val linearId: UniqueIdentifier = UniqueIdentifier()
): LinearState, QueryableState, VerifiableState {

	override val participants: List<AbstractParty>
		get() = listOf(operator, buyer.identity, seller.identity)

	override fun generateMappedObject(schema: MappedSchema): PersistentState {
		return when (schema) {
			is DvPSchemaV1 -> DvPSchemaV1.PersistentDvP(
				operatorName = this.operator.name.toString(),
				buyerName = this.buyer.legalName,
				sellerName = this.seller.legalName,
				status = this.status,
				linearId = this.linearId.id
			)
			else -> throw IllegalArgumentException("Unrecognised schema $schema")
		}
	}

	override fun supportedSchemas(): Iterable<MappedSchema> = listOf(DvPSchemaV1)

	override fun partyOf(role: PartyRole): Party = when (role) {
		PartyRole.OPERATOR -> operator
		PartyRole.BUYER -> buyer.identity
		PartyRole.SELLER -> seller.identity
		else -> throw IllegalArgumentException("Role $role is not supported in this state")
	}
}