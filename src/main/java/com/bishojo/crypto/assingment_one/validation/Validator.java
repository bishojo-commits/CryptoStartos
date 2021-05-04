package com.bishojo.crypto.assingment_one.validation;

import com.bishojo.crypto.assingment_one.crypto.Crypto;
import com.bishojo.crypto.assignment_three.Transaction;
import com.bishojo.crypto.assignment_three.UTXO;
import com.bishojo.crypto.assignment_three.UTXOPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Validator {

    private Transaction tx;

    private UTXOPool utxoPool;

    private double totalInput;

    private double totalOuput;

    public Validator(Transaction tx, UTXOPool utxoPool) {
        this.tx = tx;
        this.utxoPool = utxoPool;
    }

    public Boolean outputsInUtxoPool() {
        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input input = tx.getInput(i);
            UTXO u = new UTXO(input.prevTxHash, input.outputIndex);
            if (!utxoPool.contains(u)) {
                return false;
            }
        }
        return true;
    }

    public Boolean inputSignatureIsValid() {
        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input input = tx.getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output out = utxoPool.getTxOutput(utxo);
            boolean valid = Crypto.verifySignature(out.address, tx.getRawDataToSign(i), input.signature);

            if (!valid) {
                return false;
            } else {
                setTotalInput(out.value);
            }
        }
        return true;
    }

    public Boolean allTxOutputsPositive() {
        for (int i = 0; i < tx.numOutputs(); i++) {
            Transaction.Output out = tx.getOutput(i);
            if (out.value < 0) {
                return false;
            } else {
                setTotalOuput(out.value);
            }
        }
        return true;
    }

    public Boolean txUtxoIsUnique() {
        ArrayList<UTXO> allUTXO = utxoPool.getAllUTXO();
        Set<UTXO> setUTXO = new HashSet<UTXO>(allUTXO.size());

        for (int i = 0; i < tx.numInputs(); i++) {
            Transaction.Input input = tx.getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (!setUTXO.add(utxo)) {
                return false;
            }
        }
        return true;
    }

    public double getTotalInput() {
        return totalInput;
    }

    private void setTotalInput(double amount) {
        this.totalInput += amount;
    }

    public double getTotalOuput() {
        return totalOuput;
    }

    private void setTotalOuput(double amount) {
        this.totalOuput += amount;
    }
}
