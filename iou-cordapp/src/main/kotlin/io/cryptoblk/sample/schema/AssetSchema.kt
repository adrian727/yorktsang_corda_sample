package io.cryptoblk.sample.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * The family of schemas for AssetState.
 */
object AssetSchema

/**
 * An DvPState schema.
 */
object AssetSchemaV1 : MappedSchema(
	schemaFamily = AssetSchema::class.java,
	version = 1,
	mappedTypes = listOf(PersistentAsset::class.java)
) {
	@Entity
	@Table(name = "asset_states")
	class PersistentAsset(
		@Column(name = "operator")
		var operatorName: String,
		@Column(name = "owner")
		var ownerName: String,
		@Column(name = "asset_type")
		var assetType: String,
		@Column(name = "linear_id")
		@Type(type = "uuid-char")
		var linearId: UUID
	) : PersistentState() {
		constructor() : this("", "", "", UUID.randomUUID())
	}
}