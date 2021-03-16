package com.bishojo.crypto.validation;

import com.bishojo.crypto.transaction.Transaction;
import com.bishojo.crypto.utxo.UTXO;
import com.bishojo.crypto.utxo.UTXOPool;

import java.util.ArrayList;

public class OutputValidator {

    private Transaction tx;

    private UTXOPool utxoPool;

    public OutputValidator(Transaction tx, UTXOPool utxoPool) {
        this.tx = tx;
        this.utxoPool = utxoPool;
    }

    public Boolean txOutputsInUtxoPool() {
        ArrayList<Transaction.Output> txOutputs;
        txOutputs = tx.getOutputs();

        ArrayList<UTXO> utxos;
        utxos = utxoPool.getAllUTXO();

        for (Transaction.Output txOutput : txOutputs) {
            for (UTXO utxo : utxos) {
                if (!txOutput.equals(utxoPool.getTxOutput(utxo))) {
                    return false;
                }
            }
        }

        return true;
    }
}
