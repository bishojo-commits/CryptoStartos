package com.bishojo.crypto.validation;

import com.bishojo.crypto.crypto.Crypto;
import com.bishojo.crypto.transaction.Transaction;
import com.bishojo.crypto.utxo.UTXO;
import com.bishojo.crypto.utxo.UTXOPool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

public class InputValidator {

    private Transaction tx;

    private UTXOPool utxoPool;

    public InputValidator(Transaction tx, UTXOPool utxoPool) {
        this.tx = tx;
        this.utxoPool = utxoPool;
    }

    public Boolean isValidSignature() {
        ArrayList<Transaction.Output> txOutputs;
        txOutputs = tx.getOutputs();

        ArrayList<Transaction.Input> txInputs;
        txInputs = tx.getInputs();

        double sumInput = 0.00;
        double sumOutput = 0.00;

        for (Transaction.Input txInput : txInputs) {
            int txOutputIndex = txInput.outputIndex;
            byte[] message = null;
            PublicKey address = null;

            //I get publicKey
            for (int i = 0; i < txOutputs.size(); i++) {

                if (txOutputIndex == i) {
                    address = txOutputs.get(i).address;
                    sumInput += txOutputs.get(i).value;
                }
                //all of txs output values are non-negative
                if (txOutputs.get(i).value < 0) {
                    return false;
                }

                sumOutput += txOutputs.get(i).value;
            }

            //II get message
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(tx.getRawTx());
                message = md.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            //III get signature
            byte[] signature = txInput.signature;

            if (!Crypto.verifySignature(address, message, signature)) {
                return false;
            }

            if (sumInput < sumOutput) {
                return false;
            }
        }

        return true;
    }

    public Boolean isUtxoClaimedOnce() {
        ArrayList<Transaction.Input> txInputs;
        txInputs = tx.getInputs();

        ArrayList<UTXO> utxos;
        utxos = utxoPool.getAllUTXO();

        int count = 0;

        for (Transaction.Input txInput : txInputs) {
            UTXO u = new UTXO(txInput.prevTxHash, txInput.outputIndex);
            for (UTXO utxo : utxos) {
                if (utxo.equals(u)) {
                    count++;
                    if (count > 1) {
                        return false;
                    }
                }
            }
            count = 0;
        }

        return true;
    }
}
