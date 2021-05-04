package com.bishojo.crypto.assingment_one.tx;

import com.bishojo.crypto.assingment_one.transaction.Transaction;
import com.bishojo.crypto.assingment_one.utxo.UTXO;
import com.bishojo.crypto.assingment_one.utxo.UTXOPool;
import com.bishojo.crypto.assingment_one.validation.Validator;

import java.util.ArrayList;
import java.util.List;

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
     * values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        Validator validator = new Validator(tx, utxoPool);

        //1
        if (!validator.outputsInUtxoPool()) {
            return false;
        }

        //2
        if (!validator.inputSignatureIsValid()) {
            return false;
        }

        //3
        if (!validator.txUtxoIsUnique()) {
            return false;
        }

        //4
        if (!validator.allTxOutputsPositive()) {
            return false;
        }

        //5
        if (validator.getTotalOuput() > validator.getTotalInput()) {
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

        List<Transaction> validTx = new ArrayList<Transaction>();

        for (int i = 0; i < possibleTxs.length; i++) {
            Transaction tx = possibleTxs[i];
            if (isValidTx(tx)) {
                validTx.add(tx);

                // get all input coins from the tx and remove them from pool
                for (int j = 0; j < tx.numInputs(); j++) {
                    Transaction.Input input = tx.getInput(j);
                    UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                    utxoPool.removeUTXO(utxo);
                }

                // get all output coins from the tx and add them to Pool
                for (int k = 0; k < tx.numOutputs(); k++) {
                    Transaction.Output output = tx.getOutput(k);
                    UTXO utxo = new UTXO(tx.getHash(), k);
                    utxoPool.addUTXO(utxo, output);
                }
            }
        }
        Transaction[] result = new Transaction[validTx.size()];
        validTx.toArray(result);

        return result;
    }
}

