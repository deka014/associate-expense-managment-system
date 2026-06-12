package com.adp.ExpenseService.service;

import com.adp.ExpenseService.entities.Receipt;
import com.adp.ExpenseService.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public Receipt createReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    public List<Receipt> read() {
        return receiptRepository.findAll();
    }

    public Receipt read(Integer id) {
        return receiptRepository.findById(id).orElse(null);
    }

    public Receipt update(Receipt receipt) {
        Receipt temp = read(receipt.getId());

        if (temp != null) {
            temp = receipt;
            receiptRepository.save(temp);
        }

        return temp;
    }

    public Receipt delete(Integer id) {
        Receipt receipt = read(id);

        if (receipt != null) {

            receiptRepository.delete(receipt);
        }

        return receipt;
    }
}
