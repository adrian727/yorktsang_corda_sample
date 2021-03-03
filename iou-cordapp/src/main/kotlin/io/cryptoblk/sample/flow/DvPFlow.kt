/*
package io.cryptoblk.sample.flow

import co.paralleluniverse.fibers.Suspendable
import io.cryptoblk.sample.contract.DvPContract
import io.cryptoblk.sample.contract.IOUContract
import io.cryptoblk.sample.contract.PaymentContract
import io.cryptoblk.sample.metadata.Participant
import io.cryptoblk.sample.state.DvPState
import io.cryptoblk.sample.state.IOUState
import io.cryptoblk.sample.state.PaymentState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker


class DvPFlow {
    @StartableByRPC
    class Initiator(val otherParty: Party) : FlowLogic<SignedTransaction>() {
        /**
         * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
         * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
         */
        companion object {
            object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new IOU.")
            object VERIFYING_TRANSACTION : ProgressTracker.Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : ProgressTracker.Step("Signing transaction with our private key.")
            object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        override fun call(): SignedTransaction {
            val notary = serviceHub.networkMapCache.notaryIdentities.single() // METHOD 1
            // val notary = serviceHub.networkMapCache.getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB")) // METHOD 2

            // Stage 1.
            progressTracker.currentStep = GENERATING_TRANSACTION


            val myParty = serviceHub.myInfo.legalIdentities.first()
            // Generate an unsigned transaction.
            val myParticipant = Participant(myParty, myParty.name.toString(), "lei", "bic", "address1", "address2")
            val paymentState = PaymentState(myParty, myParticipant, myParticipant)
            val dvpState = DvPState()
            val paymentCommand = Command(PaymentContract.Commands.Create(), paymentState.payer.identity.owningKey)
            val dvpCommand = Command(DvPContract.Commands.DvPDraftCmd(), dvpState.operator.owningKey)
            val txBuilder = TransactionBuilder(notary)
                .addOutputState(paymentState, PaymentContract.javaClass.name)
                .addOutputState(dvpState, DvPContract.javaClass.name)
                .addCommand(paymentCommand)
                .addCommand(dvpCommand)



            // Stage 2.
            progressTracker.currentStep = VERIFYING_TRANSACTION
            // Verify that the transaction is valid.
            //txBuilder.verify(serviceHub)

            // Stage 3.
            progressTracker.currentStep = SIGNING_TRANSACTION
            // Sign the transaction.
            //val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

            // Stage 5.
            progressTracker.currentStep = FINALISING_TRANSACTION
            // Notarise and record the transaction in both parties' vaults.
            return subFlow(FinalityFlow(partSignedTx, FINALISING_TRANSACTION.childProgressTracker()))
        }
    }
}

 */