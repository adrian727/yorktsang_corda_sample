package io.cryptoblk.sample

import com.fasterxml.jackson.databind.ObjectMapper
import net.corda.client.jackson.JacksonSupport.createNonRpcMapper
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import io.cryptoblk.sample.flow.ExampleFlow
import io.cryptoblk.sample.flow.ModifyFlow
import io.cryptoblk.sample.state.IOUState
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import javax.servlet.http.HttpServletRequest
import net.corda.client.jackson.JacksonSupport
import net.corda.core.contracts.StateRef
import net.corda.core.crypto.SecureHash
import net.corda.core.node.services.KeyManagementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.*
import javax.print.attribute.standard.Media

val SERVICE_NAMES = listOf("Notary", "Network Map Service")

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {


    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }
    @Bean
    open fun mappingJackson2HttpMessageConverter(@Autowired rpcConnection: NodeRPCConnection): MappingJackson2HttpMessageConverter {
        val mapper = JacksonSupport.createDefaultMapper(rpcConnection.proxy)
        val converter = MappingJackson2HttpMessageConverter()
        converter.objectMapper = mapper
        return converter
    }


    private val myLegalName = rpc.proxy.nodeInfo().legalIdentities.first().name
    private val proxy = rpc.proxy

    /**
     * Returns the node's name.
     */
    @GetMapping(value = ["me"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun whoami() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the network map service. These names can be used to look up identities using
     * the identity service.
     */
    @GetMapping(value = ["peers"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = proxy.networkMapSnapshot()
        return mapOf("peers" to nodeInfo
            .map { it.legalIdentities.first().name }
            //filter out myself, notary and eventual network map started by driver
            .filter { it.organisation !in (SERVICE_NAMES + myLegalName.organisation) })
    }

    /**
     * Displays all IOU states that exist in the node's vault.
     */
    @GetMapping(value = ["ious"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getIOUs() : ResponseEntity<List<StateAndRef<IOUState>>> {
        return ResponseEntity.ok(proxy.vaultQueryBy<IOUState>().states)
    }

    @PostMapping(value = ["create"], produces =  [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody obj:Payload) : ResponseEntity<String> {
        if (obj.value <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n")
        }
        val partyX500Name = CordaX500Name.parse(obj.lender)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named ${obj.lender} cannot be found.\n")
        return try {
            val signedTx = proxy.startTrackedFlow(ExampleFlow::Initiator, obj.value, otherParty).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }

    }

    @PostMapping(value = ["update"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateIOU(@RequestBody obj:UpdatePayload): ResponseEntity<String> {
        val ref = obj.ref
        val iouValue = obj.value

        if (iouValue <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n")
        }
        val stateRef = parseString(ref)

        return try {
            val signedTx = proxy.startTrackedFlow(ModifyFlow::Initiator, stateRef, iouValue).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }



    /**
     * Initiates a flow to agree an IOU between two parties.
     *
     * Once the flow finishes it will have written the IOU to ledger. Both the lender and the borrower will be able to
     * see it when calling /spring/api/ious on their respective nodes.
     *
     * This end-point takes a Party name parameter as part of the path. If the serving node can't find the other party
     * in its network map cache, it will return an HTTP bad request.
     *
     * The flow is invoked asynchronously. It returns a future when the flow's call() method returns.
     */

    @PostMapping(value = ["create-iou"], produces = [MediaType.TEXT_PLAIN_VALUE], headers = ["Content-Type=application/x-www-form-urlencoded"])
    fun createIOU(request: HttpServletRequest): ResponseEntity<String> {
        val iouValue = request.getParameter("iouValue").toInt()
        val partyName = request.getParameter("partyName")
        if(partyName == null){
            return ResponseEntity.badRequest().body("Query parameter 'partyName' must not be null.\n")
        }
        if (iouValue <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n")
        }
        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n")

        return try {
            val signedTx = proxy.startTrackedFlow(ExampleFlow::Initiator, iouValue, otherParty).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    @PostMapping(value = ["update-iou"], produces = [MediaType.TEXT_PLAIN_VALUE], headers = ["Content-Type=application/x-www-form-urlencoded"])
    fun updateIOU(request: HttpServletRequest): ResponseEntity<String> {
        val ref = request.getParameter("ref").toString()
        val iouValue = request.getParameter("iouValue").toInt()

        if (iouValue <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n")
        }
        val stateRef = parseString(ref)

        return try {
            val signedTx = proxy.startTrackedFlow(ModifyFlow::Initiator, stateRef, iouValue).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Displays all IOU states that only this node has been involved in.
     */
    @GetMapping(value = ["my-ious"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMyIOUs(): ResponseEntity<List<StateAndRef<IOUState>>> {
        val myious = proxy.vaultQueryBy<IOUState>().states.filter { it.state.data.lender.equals(proxy.nodeInfo().legalIdentities.first()) }
        return ResponseEntity.ok(myious)
    }

    fun parseString(str: String): StateRef {
        val parts = str.split('(', ')')
        return when (parts.size) {
            3 -> {
                require(parts[2].isEmpty())
                StateRef(SecureHash.parse(parts[0]), parts[1].toInt())
            }
            1 -> StateRef(SecureHash.parse(parts[0]), 0)
            else -> throw IllegalArgumentException()
        }
    }
}