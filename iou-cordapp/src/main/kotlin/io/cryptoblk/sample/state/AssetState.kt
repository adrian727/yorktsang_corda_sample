package io.cryptoblk.sample.state

import io.cryptoblk.sample.contract.AssetContract
import io.cryptoblk.sample.schema.AssetSchemaV1
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

@BelongsToContract(AssetContract::class)
data class AssetState(
	val operator: Party,
	val owner: Party,
	val assetType: String, // TODO: change to enum
	override val linearId: UniqueIdentifier = UniqueIdentifier()
	): LinearState, QueryableState {

	override val participants: List<AbstractParty>
		get() = listOf(operator, owner)

	override fun generateMappedObject(schema: MappedSchema): PersistentState {
		return when (schema) {
			is AssetSchemaV1 -> AssetSchemaV1.PersistentAsset(
				operatorName = this.operator.name.toString(),
				ownerName = this.owner.name.toString(),
				assetType = assetType,
				linearId = this.linearId.id
			)
			else -> throw IllegalArgumentException("Unrecognised schema $schema")
		}
	}

	override fun supportedSchemas(): Iterable<MappedSchema> =  listOf(AssetSchemaV1)
}