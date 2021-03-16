package com.bishojo.crypto.tx;

import com.bishojo.crypto.transaction.Transaction;
import org.apache.commons.lang3.ArrayUtils;

public class MaxFeeTxHandler {

    /**
     *  finds a set of transactions with maximum total transaction fees --
     *  i.e. maximize the sum over all transactions in the set of
     *  (sum of input values - sum of output values)).
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs, double maximumFees) {
        double sumInput = 0.00;
        double sumOutput = 0.00;

        for (int i =0; i < possibleTxs.length; i++) {
            for (Transaction.Input txInput : possibleTxs[i].getInputs()) {
                sumInput += possibleTxs[i].getOutput(txInput.outputIndex).value;
            }

            for (Transaction.Output txOutput : possibleTxs[i].getOutputs()) {
                sumOutput += txOutput.value;
            }

            if(maximumFees > (sumInput - sumOutput)) {
                ArrayUtils.remove(possibleTxs, i);
            }
        }
        return possibleTxs;
    }
}
