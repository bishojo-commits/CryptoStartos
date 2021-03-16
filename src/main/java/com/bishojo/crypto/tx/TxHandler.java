package com.bishojo.crypto.tx;

import com.bishojo.crypto.transaction.Transaction;
import com.bishojo.crypto.utxo.UTXO;
import com.bishojo.crypto.utxo.UTXOPool;
import com.bishojo.crypto.validation.InputValidator;
import com.bishojo.crypto.validation.OutputValidator;
import org.apache.commons.lang3.ArrayUtils;

public class TxHandler {

    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        InputValidator inputValidator = new InputValidator(tx, utxoPool);
        OutputValidator outputValidator = new OutputValidator(tx, utxoPool);

        //1. all outputs claimed by tx are in the current UTXO pool
        if (!outputValidator.txOutputsInUtxoPool()) {
            return false;
        }

        //2. the signatures on each input of tx are valid
        //4. all of txs output values are non-negative
        //5 the sum of tx input values is greater than
        // or equal to the sum of its output values
        if (!inputValidator.isValidSignature()) {
            return false;
        }

        //3. no UTXO is claimed multiple times by {@code tx}
        if (!inputValidator.isUtxoClaimedOnce()) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {

        for (int i =0; i < possibleTxs.length; i++) {
            if (!this.isValidTx(possibleTxs[i])) {
                for (Transaction.Input txInput : possibleTxs[i].getInputs()) {
                    UTXO utxo = new UTXO(txInput.prevTxHash, txInput.outputIndex);
                    this.utxoPool.removeUTXO(utxo);

                    ArrayUtils.remove(possibleTxs, i);
                }

            }
        }

        return possibleTxs;
    }
}
